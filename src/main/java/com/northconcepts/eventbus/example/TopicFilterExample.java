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
import com.northconcepts.eventbus.filter.TopicFilter;

public class TopicFilterExample {
	
	public static enum Topic { 	STROLL, RUSH }

	public static void main(String[] args) throws Throwable {
		TopicFilterExample eventSource = new TopicFilterExample();
		Location store = new Location();

		EventBus eventBus = new EventBus();

		// listen for events under the STROLL topic
		eventBus.addListener(WalkListener.class, 
				new TopicFilter(Topic.STROLL), 
				new WalkListener() {
			public void walkTo(Location location) {
				System.out.println("walking slowly...out for a stroll");
			}
		});
		
		// listen for events under the RUSH topic
		eventBus.addListener(WalkListener.class, 
				new TopicFilter(Topic.RUSH), 
				new WalkListener() {
			public void walkTo(Location location) {
				System.out.println("walking quickly...in a rush");
			}
		});

		
		// RUSH is set as the topic for this publisher
		WalkListener publisher = eventBus.getPublisher(eventSource, WalkListener.class, Topic.RUSH);
		
		publisher.walkTo(store);
		publisher.walkTo(store);
		publisher.walkTo(store);
		
		Thread.sleep(2000L);
		eventBus.shutdown();
		System.out.println("The End.");
	}

}
