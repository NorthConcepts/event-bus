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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.EventListener;

public class EventSourceInvocationHandler<T extends EventListener> implements InvocationHandler {

	private final EventBus eventBus;
	private final Object eventSource;
	private final Class<T> eventListenerClass;
	private final Object topic;

	public EventSourceInvocationHandler(EventBus eventBus, Object eventSource, Class<T> eventListenerClass, Object topic) {
		this.eventBus = eventBus;
		this.eventSource = eventSource;
		this.eventListenerClass = eventListenerClass;
		this.topic = topic;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
        eventBus.publishEvent(new Event<T>(eventSource, proxy, eventListenerClass, topic, method, arguments));
        return null;
	}

}
