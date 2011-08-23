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
package com.northconcepts.eventbus.example;

import com.northconcepts.eventbus.Event;
import com.northconcepts.eventbus.EventBus;
import com.northconcepts.eventbus.ExceptionListener;

public class ExceptionListenerExample {
	
	public static void main(String[] args) throws Throwable {
		ExceptionListenerExample eventSource = new ExceptionListenerExample();
		Location store = new Location();

		EventBus eventBus = new EventBus();

		eventBus.addListener(WalkListener.class, null, new WalkListener() {
			public void walkTo(Location location) {
				throw new RuntimeException("always fails");
			}
		});
		
		eventBus.addExceptionListener(null, new ExceptionListener() {
			@Override
			public void onException(Event<?> event, Throwable exception, Object targetListener) {
				System.out.println("exception in " +
						event.getEventListenerClass().getSimpleName() +
						"." +
						event.getMethod().getName() +
						": " + exception.getMessage());
			}
		}); 

		WalkListener publisher = eventBus.getPublisher(eventSource, WalkListener.class);
		
		publisher.walkTo(store);
		publisher.walkTo(store);
		publisher.walkTo(store);
		
		Thread.sleep(2000L);
		eventBus.shutdown();
		System.out.println("The End.");
	}

}
