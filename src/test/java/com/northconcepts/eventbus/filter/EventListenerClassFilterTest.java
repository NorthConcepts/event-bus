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
import org.junit.Test;

import java.util.EventListener;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventListenerClassFilterTest {

	private static final class CLASS_A implements EventListener {}
	private static final class CLASS_B implements EventListener {}
	private static final class CLASS_C implements EventListener {}
	private static final class CLASS_D implements EventListener {}

	private final Event<?> EVENT_A = newMockEvent(CLASS_A.class);
	private final Event<?> EVENT_B = newMockEvent(CLASS_B.class);
	private final Event<?> EVENT_C = newMockEvent(CLASS_C.class);
	private final Event<?> EVENT_D = newMockEvent(CLASS_D.class);
	private final Event<?> EVENT_NULL = newMockEvent(null);

	private final Object listener = mock(Object.class);

	@Test
	public void testNullSource() {
		EventFilter filter = new EventListenerClassFilter((Class<?>[]) null);

		assertTrue(filter.allow(EVENT_A, listener));
		assertTrue(filter.allow(EVENT_B, listener));
		assertTrue(filter.allow(EVENT_C, listener));
		assertTrue(filter.allow(EVENT_D, listener));
		assertTrue(filter.allow(EVENT_NULL, listener));
	}

	@Test
	public void testZeroSources() {
		EventFilter filter = new EventListenerClassFilter();

		assertTrue(filter.allow(EVENT_A, listener));
		assertTrue(filter.allow(EVENT_B, listener));
		assertTrue(filter.allow(EVENT_C, listener));
		assertTrue(filter.allow(EVENT_D, listener));
		assertTrue(filter.allow(EVENT_NULL, listener));
	}

	@Test
	public void testOneSource() {
		EventFilter filter = new EventListenerClassFilter(CLASS_A.class);

		assertTrue(filter.allow(EVENT_A, listener));
		assertFalse(filter.allow(EVENT_B, listener));
		assertFalse(filter.allow(EVENT_C, listener));
		assertFalse(filter.allow(EVENT_D, listener));
		assertFalse(filter.allow(EVENT_NULL, listener));
	}

	@Test
	public void testManySources() {
		EventFilter filter = new EventListenerClassFilter(CLASS_A.class, CLASS_B.class, CLASS_C.class);

		assertTrue(filter.allow(EVENT_A, listener));
		assertTrue(filter.allow(EVENT_B, listener));
		assertTrue(filter.allow(EVENT_C, listener));
		assertFalse(filter.allow(EVENT_D, listener));
		assertFalse(filter.allow(EVENT_NULL, listener));
	}

	@SuppressWarnings("unchecked")
	private static <T extends EventListener> Event<T> newMockEvent(Class<T> cls) {
		Event<T> event = mock(Event.class);
		when(event.getEventListenerClass()).thenReturn(cls);

		return event;
	}

}