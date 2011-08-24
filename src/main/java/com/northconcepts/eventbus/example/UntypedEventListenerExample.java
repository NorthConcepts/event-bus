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
import com.northconcepts.eventbus.UntypedEventListener;
import com.northconcepts.eventbus.filter.TopicFilter;

public class UntypedEventListenerExample {
	
	public static void main(String[] args) throws Throwable {
		UntypedEventListenerExample eventSource = new UntypedEventListenerExample();
		Location store = new Location();

		EventBus eventBus = new EventBus();

		eventBus.addListener(null, new UntypedEventListener() {
			public void onEvent(Event<?> event) {
				System.out.println("event: " + 
					event.getEventListenerClass().getSimpleName() +
					"." +
					event.getMethod().getName()
					);
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
