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

import org.junit.Test;

import java.util.EventListener;
import java.util.Random;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventDeliveryTest extends AbstractEventBusTest<TestListener> {

	@Override
	protected Class<TestListener> getListenerClass() {
		return TestListener.class;
	}

	@Test
	public void testZeroListeners() {
		// Intentionally don't register any listeners.

		publisher.foo();
		shutdownBus();

		// Nothing to verify.
	}

	@Test
	public void testOneListener() {
		TestListener listener = newMockListener();

		publisher.foo();
		shutdownBus();

		verify(listener).foo();
	}

	@Test
	public void testManyListeners() {
		int n = 2 + new Random().nextInt(100);
		TestListener[] listeners = new TestListener[n];
		for (int i = 0; i < n; i++) {
			listeners[i] = newMockListener();
		}

		publisher.foo();
		shutdownBus();

		for (TestListener listener : listeners) {
			verify(listener).foo();
		}
	}

	@Test
	public void testMultipleCalls() {
		TestListener listener = newMockListener();

		int n = 2 + new Random().nextInt(100);
		for(int i = 0; i < n; i++) {
			publisher.foo();
		}
		shutdownBus();

		verify(listener, times(n)).foo();
	}

	@Test
	public void testCorrectListenerMethodCalled() {
		TestListener listener = newMockListener();

		publisher.foo();
		shutdownBus();

		verify(listener).foo();
		verify(listener, never()).bar(anyInt());
	}
	
}

// TODO: Should probably promote this to its own file.
// It needs to live outside of the test class in order to be able to be used as a type parameter for the test case, but
// the way it's currently written it's a package level class visible to everything in the entire package.
interface TestListener extends EventListener {
	void foo();
	void bar(int i);
}
