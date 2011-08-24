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
import java.lang.ref.SoftReference;
import java.util.EventListener;


abstract class EventListenerStub<T> {
	
	private final EventFilter filter;
	private final Reference<T> listener;
	
	public EventListenerStub(EventFilter filter, T listener, ReferenceQueue<? super T> referenceQueue) {
		if (filter == null) {
			filter = EventFilter.NULL;
		}
		this.filter = filter;
		this.listener = new SoftReference<T>(listener, referenceQueue);
	}
	
	public EventFilter getFilter() {
		return filter;
	}
	
	protected T getListener() {
		return listener.get();
	}
	
	public boolean matchesListener(T listener) {
		return this.listener.get() == listener; 
	}
	
	public boolean matchesListenerReference(Reference<?> reference) {
		return this.listener == reference; 
	}
	
	public void deliverEvent(EventBus eventBus, Event<?> event) {
		try {
			if (filter.allow(event, getListener())) {
				deliverEventImpl(eventBus, event);
			}
		} catch (Throwable e) {
			eventBus.handleException(event, getListener(), e);
		}
	}

	protected abstract void deliverEventImpl(EventBus eventBus, Event<?> event) throws Throwable;

	//====================================================================
	//  Typed Implementation
	//====================================================================
	
	final static class Typed<S extends EventListener> extends EventListenerStub<S> {

		public Typed(EventFilter filter, S listener, ReferenceQueue<? super S> referenceQueue) {
			super(filter, listener, referenceQueue);
		}

		@Override
		protected void deliverEventImpl(EventBus eventBus, Event<?> event) throws Throwable {
			S listener = getListener();
			if (listener != null) {  // in case it was garbage collected
				event.getMethod().invoke(listener, event.getArguments());
			}
		}
	}
	
	//====================================================================
	//  Untyped Implementation
	//====================================================================
	
	final static class Untyped extends EventListenerStub<UntypedEventListener> {

		public Untyped(EventFilter filter, UntypedEventListener listener, ReferenceQueue<UntypedEventListener> referenceQueue) {
			super(filter, listener, referenceQueue);
		}

		@Override
		protected void deliverEventImpl(EventBus eventBus, Event<?> event) throws Throwable {
			UntypedEventListener listener = getListener();
			if (listener != null) {  // in case it was garbage collected
				listener.onEvent(event);
			}
		}
	}
	
	
}
	
