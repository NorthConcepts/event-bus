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

import java.lang.reflect.Method;
import java.util.EventListener;

public class Event<T extends EventListener> {

	private final Object eventSource;
	private final Object eventPublisher;
	private final Class<T> eventListenerClass;
	private final Object topic;
	private final Method method;
	private final Object[] arguments;

	public Event(Object eventSource, Object eventPublisher, Class<T> eventListenerClass, Object topic, Method method, Object[] arguments) {
		this.eventSource = eventSource;
		this.eventPublisher = eventPublisher;
		this.eventListenerClass = eventListenerClass;
		this.topic = topic;
		this.method = method;
		this.arguments = arguments;
	}

	public Object getEventSource() {
		return eventSource;
	}
	
	public Object getEventPublisher() {
		return eventPublisher;
	}

	public Class<T> getEventListenerClass() {
		return eventListenerClass;
	}
	
	public Object getTopic() {
		return topic;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public String toString() {
		return eventListenerClass.getName() + "." + method.getName().toString() + "#" + topic;
	}
	
}
