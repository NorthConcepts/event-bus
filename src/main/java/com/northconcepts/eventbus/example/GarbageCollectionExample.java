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

import java.util.ArrayList;
import java.util.List;

import com.northconcepts.eventbus.EventBus;

public class GarbageCollectionExample {

	public static void main(String[] args) throws Throwable {
		GarbageCollectionExample eventSource = new GarbageCollectionExample();
		Location store = new Location();

		EventBus eventBus = new EventBus();
		eventBus.addListener(WalkListener.class, null, new WalkListener() {
			public void walkTo(Location location) {
				System.out.println("walking...");
			}
		});

		WalkListener publisher = eventBus.getPublisher(eventSource, WalkListener.class);

		publisher.walkTo(store);
		
		// Force GC.  Should remove weak references. 
		// Soft references should not be affected
		System.gc();
		
		publisher.walkTo(store);
		
		// Allocate memory until out-of-memory
		// garbage collector remove soft references
		List<byte[]> bytes = new ArrayList<byte[]>(10000);
		try {
			for (int i = 0; i < 10000; i++) {
				bytes.add(new byte[10 * 1024 * 1024]); // allocate 10MB blocks 
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		publisher.walkTo(store);
		
		
		Thread.sleep(2000L);
		eventBus.shutdown();
		System.out.println("The End.");
	}

}
