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

import com.northconcepts.eventbus.EventFilter;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AndFilterTest extends LogicalFilterTest {

	@Test
	public void testNullFilters() {
		EventFilter filter = new AndFilter((EventFilter[]) null);
		assertTrue(filter.allow(event, listener));
	}

	@Test
	public void testZeroFilters() {
		EventFilter filter = new AndFilter();
		assertTrue(filter.allow(event, listener));
	}

	@Test
	public void testOneTrueFilter() {
		EventFilter filter = new AndFilter(TRUE_FILTER);
		assertTrue(filter.allow(event, listener));
	}

	@Test
	public void testOneFalseFilter() {
		EventFilter filter = new AndFilter(FALSE_FILTER);
		assertFalse(filter.allow(event, listener));
	}

	@Test
	public void testManyTrueFilters() {
		EventFilter filter = new AndFilter(TRUE_FILTER, TRUE_FILTER, TRUE_FILTER);
		assertTrue(filter.allow(event, listener));
	}

	@Test
	public void testManyFalseFilters() {
		EventFilter filter = new AndFilter(FALSE_FILTER, FALSE_FILTER, FALSE_FILTER);
		assertFalse(filter.allow(event, listener));
	}

	@Test
	public void testManyMixedFilters() {
		EventFilter filter = new AndFilter(TRUE_FILTER, FALSE_FILTER, TRUE_FILTER);
		assertFalse(filter.allow(event, listener));
	}

}
