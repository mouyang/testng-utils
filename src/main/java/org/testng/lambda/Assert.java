package org.testng.lambda;

import static java.lang.String.join;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

public class Assert {

	protected Assert() {
		// hide constructor
	}

	public static Assertion assertTrue(boolean condition) {
		return () -> org.testng.Assert.assertTrue(condition);
	}

	public static Assertion assertTrue(boolean condition, String message) {
		return () -> org.testng.Assert.assertTrue(condition, message);
	}

	public static Assertion assertEquals(int actual, int expected, String message) {
		return () -> org.testng.Assert.assertEquals(actual, expected, message);	
	}

	public static void assertAll(Assertion... assertions) {
		List<AssertionError> errors = new ArrayList<>();
		for (Assertion assertion : assertions) {
			try {
				assertion.run();				
			} catch (AssertionError ae) {
				errors.add(ae);
			}
		}
		if (!errors.isEmpty()) {
			org.testng.Assert.fail("some assertions failed: " 
				+ join(", ", errors.stream().map(e -> e.getMessage()).collect(toList())));
		}
	}

	public static void main(String[] args) {
		assertAll(
			assertTrue(false)
			, assertTrue(false, "false with message")
			, assertEquals(1, 2, "1 is not 2")
		);
	}
}
