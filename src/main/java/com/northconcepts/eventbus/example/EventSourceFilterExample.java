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

import com.northconcepts.eventbus.EventBus;
import com.northconcepts.eventbus.filter.EventSourceFilter;

public class EventSourceFilterExample {

	public static void main(String[] args) throws Throwable {
		EventSourceFilterExample eventSource1 = new EventSourceFilterExample();
		EventSourceFilterExample eventSource2 = new EventSourceFilterExample();
		Location store = new Location();
		
		EventBus eventBus = new EventBus();

		// listen for events from eventSource1
		eventBus.addListener(WalkListener.class, 
				new EventSourceFilter(eventSource1), 
				new WalkListener() {
			public void walkTo(Location location) {
				System.out.println("walking from eventSource1...");
			}
		});
		
		// listen for events from eventSource1 or eventSource2
		eventBus.addListener(WalkListener.class, 
				new EventSourceFilter(eventSource1, eventSource2), 
				new WalkListener() {
			public void walkTo(Location location) {
				System.out.println("walking from eventSource1 or eventSource2...");
			}
		});

		
		// eventSource2 is set as the source for this publisher
		WalkListener publisher = eventBus.getPublisher(eventSource2, WalkListener.class);

		publisher.walkTo(store);
		publisher.walkTo(store);
		publisher.walkTo(store);

		Thread.sleep(2000L);
		eventBus.shutdown();
		System.out.println("The End.");
	}

}
