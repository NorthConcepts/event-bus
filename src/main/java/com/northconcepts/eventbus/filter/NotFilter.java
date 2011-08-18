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
package com.northconcepts.eventbus.filter;

import com.northconcepts.eventbus.Event;
import com.northconcepts.eventbus.EventFilter;

public class NotFilter implements EventFilter {
	
	private final EventFilter[] filter;

	public NotFilter(EventFilter ... filter) {
		this.filter = filter;
	}

	@Override
	public boolean allow(Event<?> event, Object listener) {
		for (EventFilter c : filter) {
			if (c.allow(event, listener)) {
				return false;
			}
		}
		return true;
	}
	
}