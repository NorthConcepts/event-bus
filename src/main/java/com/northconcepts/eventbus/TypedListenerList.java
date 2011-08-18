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

import java.util.EventListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class TypedListenerList<T extends EventListener> {

	private final Class<T> type;
	private final CopyOnWriteArrayList<EventListenerStub<T>> list = new CopyOnWriteArrayList<EventListenerStub<T>>();

	public TypedListenerList(Class<T> type) {
		this.type = type;
	}
	
	public Class<T> getType() {
		return type;
	}
	
	public List<EventListenerStub<T>> getList() {
		return list;
	}
	
//	@SuppressWarnings("unchecked")
//	protected void dispatch(final Event<T> event) {
//		final List<EventListenerStub<T>> listeners = (List<EventListenerStub<T>>) this.listeners.clone();
//		EventBus.get().doDispatch(event, listeners);
//	}

}
