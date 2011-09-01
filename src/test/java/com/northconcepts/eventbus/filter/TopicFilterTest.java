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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TopicFilterTest {

	private final String TOPIC_A = "A";
	private final String TOPIC_B = "B";
	private final String TOPIC_C = "C";
	private final String TOPIC_D = "D";

	private final Event<?> EVENT_A = newMockEvent(TOPIC_A);
	private final Event<?> EVENT_B = newMockEvent(TOPIC_B);
	private final Event<?> EVENT_C = newMockEvent(TOPIC_C);
	private final Event<?> EVENT_D = newMockEvent(TOPIC_D);
	private final Event<?> EVENT_NULL = newMockEvent(null);

	private final Object listener = mock(Object.class);

	@Test
	public void testNullTopic() {
		EventFilter filter = new TopicFilter((Object[]) null);

		// Should allow any event since no restrictions have been placed...
		assertTrue(filter.allow(EVENT_A, listener));
		assertTrue(filter.allow(EVENT_B, listener));
		assertTrue(filter.allow(EVENT_C, listener));
		assertTrue(filter.allow(EVENT_D, listener));
		assertTrue(filter.allow(EVENT_NULL, listener));
	}

	@Test
	public void testZeroTopics() {
		EventFilter filter = new TopicFilter();

		// Should allow any event since no restriction have been placed...
		assertTrue(filter.allow(EVENT_A, listener));
		assertTrue(filter.allow(EVENT_B, listener));
		assertTrue(filter.allow(EVENT_C, listener));
		assertTrue(filter.allow(EVENT_D, listener));
		assertTrue(filter.allow(EVENT_NULL, listener));
	}

	@Test
	public void testOneTopic() {
		EventFilter filter = new TopicFilter(TOPIC_A);

		assertTrue(filter.allow(EVENT_A, listener));
		assertFalse(filter.allow(EVENT_B, listener));
		assertFalse(filter.allow(EVENT_C, listener));
		assertFalse(filter.allow(EVENT_D, listener));
		assertFalse(filter.allow(EVENT_NULL, listener));
	}

	@Test
	public void testManyTopics() {
		EventFilter filter = new TopicFilter(TOPIC_A, TOPIC_B, TOPIC_C);

		assertTrue(filter.allow(EVENT_A, listener));
		assertTrue(filter.allow(EVENT_B, listener));
		assertTrue(filter.allow(EVENT_C, listener));
		assertFalse(filter.allow(EVENT_D, listener));
		assertFalse(filter.allow(EVENT_NULL, listener));				
	}

	@Test
	public void testSimilarTopics() {
		EventFilter filter = new TopicFilter(TOPIC_A);

		// Create an event with a topic string that is the same as TOPIC_A, but will fail an == check
		Event<?> event = newMockEvent(new String(TOPIC_A));
		assertNotSame(event.getTopic(), TOPIC_A);

		assertTrue(filter.allow(event, listener));
	}

	private static Event<?> newMockEvent(Object topic) {
		Event<?> event = mock(Event.class);
		when(event.getTopic()).thenReturn(topic);
		
		return event;
	}

}
