/*

Copyright 2011 North Concepts Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/
package com.northconcepts.eventbus;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EventBus {

	private final Map<Class<? extends EventListener>, TypedListenerList<?>> typedListeners = new HashMap<Class<? extends EventListener>, TypedListenerList<?>>();
	private final List<EventListenerStub<UntypedEventListener>> untypedListeners = new CopyOnWriteArrayList<EventListenerStub<UntypedEventListener>>();
	private final ReferenceQueue<? super EventListener> garbageTypedListeners = new ReferenceQueue<EventListener>();
	private final ReferenceQueue<UntypedEventListener> garbageUntypedListeners = new ReferenceQueue<UntypedEventListener>();
	private final ExecutorService executorService = Executors.newFixedThreadPool(1);
	private final ExceptionListener exceptionPublisher;
	private final ThreadLocal<Event<?>> currentEvent = new ThreadLocal<Event<?>>();

	public EventBus() {
		exceptionPublisher = getPublisher(this, ExceptionListener.class);
	}
	
	public Event<?> getCurrentEvent() {
		return currentEvent.get();
	}
	
	
	public void addExceptionListener(EventFilter filter, ExceptionListener listener) {
		addListener(ExceptionListener.class, filter, listener);
	}

	@SuppressWarnings("unchecked")
	protected <T extends EventListener> TypedListenerList<T> getTypedListenerList(Class<T> eventListenerClass) {
		synchronized (typedListeners) {
			return (TypedListenerList<T>) typedListeners.get(eventListenerClass);
		}
	}

	public <T extends EventListener> void addListener(Class<T> eventListenerClass, EventFilter filter, T listener) {
		TypedListenerList<T> list;
		synchronized (typedListeners) {
			list = getTypedListenerList(eventListenerClass);
			if (list == null) {
				list = new TypedListenerList<T>(eventListenerClass);
				typedListeners.put(eventListenerClass, list);
			}
		}
		list.getList().add(new EventListenerStub.Typed<T>(filter, listener, garbageTypedListeners));
	}

	public <T extends EventListener> void removeListener(Class<T> eventListenerClass, T listener) {
		TypedListenerList<T> list = getTypedListenerList(eventListenerClass);
		if (list != null) {
			List<EventListenerStub<T>> listeners = list.getList();
			for (EventListenerStub<T> stub : listeners) {
				if (stub.matchesListener(listener)) {
					listeners.remove(stub);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void removeListener(EventListener listener) {
		synchronized (typedListeners) {
			for (Class<? extends EventListener> eventListenerClass : typedListeners.keySet()) {
				TypedListenerList list = getTypedListenerList(eventListenerClass);
				if (list != null) {
					List<EventListenerStub> listeners = list.getList();
					for (EventListenerStub stub : listeners) {
						if (stub.matchesListener(listener)) {
							listeners.remove(stub);
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void removeTypedListenerReference(Reference<?> reference) {
		System.out.println("removeTypedListenerReference");
		synchronized (typedListeners) {
			for (Class<? extends EventListener> eventListenerClass : typedListeners.keySet()) {
				TypedListenerList list = getTypedListenerList(eventListenerClass);
				if (list != null) {
					List<EventListenerStub> listeners = list.getList();
					for (EventListenerStub stub : listeners) {
						if (stub.matchesListenerReference(reference)) {
							listeners.remove(stub);
						}
					}
				}
			}
		}
	}
	
	public void addListener(EventFilter filter, UntypedEventListener listener) {
		untypedListeners.add(new EventListenerStub.Untyped(filter, listener, garbageUntypedListeners));
	}
	
	public void removeListener(UntypedEventListener listener) {
		for (EventListenerStub<UntypedEventListener> stub : untypedListeners) {
			if (stub.matchesListener(listener)) {
				untypedListeners.remove(stub);
			}
		}
	}
	
	protected void removeUntypedListenerReference(Reference<?> reference) {
		System.out.println("removeUntypedListenerReference");
		for (EventListenerStub<UntypedEventListener> stub : untypedListeners) {
			if (stub.matchesListenerReference(reference)) {
				untypedListeners.remove(stub);
			}
		}
	}
	

	public <T extends EventListener> T getPublisher(Object eventSource, Class<T> eventListenerClass) {
		return getPublisher(eventSource, eventListenerClass, null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends EventListener> T getPublisher(Object eventSource, Class<T> eventListenerClass, Object topic) {
		EventSourceInvocationHandler<T> handler = 
			new EventSourceInvocationHandler<T>(this, eventSource, eventListenerClass, topic);
		return (T) Proxy.newProxyInstance(
				eventListenerClass.getClassLoader(), 
				new Class[]{eventListenerClass}, 
				handler);
	}

	protected <T extends EventListener> void publishEvent(final Event<T> event) {
		executorService.execute(new Runnable(){
			@Override
			public void run() {
				try {
					deliverEvent(event);
				} catch (RuntimeException e) {
					throw e;
				} catch (Throwable e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		});
	}

	protected <T extends EventListener> void deliverEvent(Event<T> event) throws Throwable {
		try {
			currentEvent.set(event);
			
			// Deliver to typed listeners
			TypedListenerList<T> list = getTypedListenerList(event.getEventListenerClass());
			if (list != null) {
				for (EventListenerStub<?> stub : list.getList()) {
					stub.deliverEvent(this, event);
				}
			}
			
			// Deliver to untyped listeners
			for (EventListenerStub<UntypedEventListener> stub : untypedListeners) {
				stub.deliverEvent(this, event);
			}
			
			cleanupGarbage();
		} finally {
			currentEvent.remove();
		}
	}

	protected void cleanupGarbage() {
		Reference<?> reference;

		while ((reference = garbageTypedListeners.poll()) != null) {
			removeTypedListenerReference(reference);
		}

		while ((reference = garbageUntypedListeners.poll()) != null) {
			removeUntypedListenerReference(reference);
		}
	}

	protected void handleException(Event<?> event, Object listener, Throwable e) {
		if (e instanceof InvocationTargetException) {
			e = e.getCause();
		}
		if (event.getEventPublisher() != exceptionPublisher) {
			exceptionPublisher.onException(event, e, listener);
		}
	}
	
	public void shutdown() throws InterruptedException {
		executorService.shutdown();
		executorService.awaitTermination(30, TimeUnit.SECONDS);
		executorService.shutdownNow();
	}

}
