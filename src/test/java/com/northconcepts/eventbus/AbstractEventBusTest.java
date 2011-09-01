package com.northconcepts.eventbus;

import org.junit.After;
import org.junit.Before;

import java.util.EventListener;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public abstract class AbstractEventBusTest<T extends EventListener> {

	protected EventBus bus;
	protected T publisher;
	private boolean busShutdown;

	@Before
	public void setup() {
		bus = new EventBus();
		publisher = bus.getPublisher(this, getListenerClass());
		busShutdown = false;
	}

	@After
	public void teardown() {
		publisher = null;
		bus = null;

		if (!busShutdown) {
			fail("Bus not shutdown, potential race condition in test case.");
		}
	}

	/**
	 * Return the class of the listener to be used for this test.
	 * 
	 * @return the listener class
	 */
	protected abstract Class<T> getListenerClass();

	/**
	 * Shuts down the bus to make sure that all events are delivered.  All events are guaranteed to be delivered and the
	 * bus rendered unusable once this method returns.
	 */
	protected void shutdownBus() {
		try {
			busShutdown = true;
			bus.shutdown();
		} catch (InterruptedException e) {
			fail("Unexpected InterruptedException");
		}
	}

	/**
	 * Create a new mock listener that is a subclass of the listener class.  This new mock listener is also registered
	 * with the bus automatically and uses a null event filter for the registration.
	 *
	 * @return the listener
	 * @see #newMockListener(EventFilter)
	 */
	protected T newMockListener() {
		return newMockListener(EventFilter.NULL);
	}

	/**
	 * Create a new mock listener that is only called for messages that the filter allows.  This new mock listener is also
	 * registered with the bus automatically.
	 *
	 * @param filter the event filter that the new listener should be registered with
	 * @return the listener
	 * @see #newMockListener()
	 */
	protected T newMockListener(EventFilter filter) {
		T listener = mock(getListenerClass());
		bus.addListener(getListenerClass(), filter, listener);

		return listener;
	}

}
