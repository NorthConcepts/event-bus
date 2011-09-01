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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class LogicalFilterTest {

	protected final EventFilter TRUE_FILTER = newMockFilter(true);
	protected final EventFilter FALSE_FILTER = newMockFilter(false);

	protected final Event<?> event = mock(Event.class);
	protected final Object listener = mock(Object.class);

	private static EventFilter newMockFilter(boolean result) {
		EventFilter filter = mock(EventFilter.class);
		when(filter.allow(any(Event.class), any())).thenReturn(result);

		return filter;
	}

}
