package org.testng.lambda;

import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

public class Assert {

	private static final List<AssertListener> suiteAssertListeners = new ArrayList<>();

	protected Assert() {
		// hide constructor
	}

	private static class AssertListenerDecorator implements Assertion {
		private Assertion assertion;
		private List<AssertListener> listeners;

		public AssertListenerDecorator(Assertion assertion, AssertListener listener) {
			this(assertion, asList(listener));
		}

		public AssertListenerDecorator(Assertion assertion, List<AssertListener> listeners) {
			this.assertion = assertion;
			this.listeners = listeners;
		}

		public void run() {
			try {
				for (AssertListener listener : listeners) {
					listener.onBeforeAssert();
				}
				assertion.run();
				for (AssertListener listener : listeners) {
					listener.onAssertSuccess();
				}
			} catch (AssertionError error) {
				for (AssertListener listener : listeners) {
					listener.onAssertFailure(error);
				}
				throw error;
			} finally {
				for (AssertListener listener : listeners) {
					listener.onAfterAssert();
				}
			}
		}
	}

	private static class CompositeAssertion implements Assertion {

		private Assertion[] assertions;

		public CompositeAssertion(Assertion... assertions) {
			this.assertions = assertions;
		}

		public void run() {
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
	}

	public static void addSuiteAssertListener(AssertListener assertListener) {
		suiteAssertListeners.add(assertListener);
	}

	public static Assertion assertTrue(boolean condition) {
		return new AssertListenerDecorator(
			() -> org.testng.Assert.assertTrue(condition)
			, suiteAssertListeners
		);
	}

	public static Assertion assertTrue(boolean condition, AssertListener listener) {
		return new AssertListenerDecorator(
			new AssertListenerDecorator(assertTrue(condition), listener)
			, suiteAssertListeners
		);
	}

	public static Assertion assertTrue(boolean condition, String message) {
		return new AssertListenerDecorator(
			() -> org.testng.Assert.assertTrue(condition, message)
			, suiteAssertListeners
		);
	}

	public static Assertion assertTrue(boolean condition, String message, AssertListener listener) {
		return new AssertListenerDecorator(
			new AssertListenerDecorator(assertTrue(condition, message), listener)
			, suiteAssertListeners
		);
	}

	public static Assertion assertEquals(int actual, int expected, String message) {
		return new AssertListenerDecorator(
			() -> org.testng.Assert.assertEquals(actual, expected, message)
			, suiteAssertListeners
		);
	}

	public static Assertion assertEquals(int actual, int expected, String message, AssertListener listener) {
		return new AssertListenerDecorator(
			new AssertListenerDecorator(assertEquals(actual, expected, message), listener)
			, suiteAssertListeners
		);
	}

	public static void assertAll(Assertion... assertions) {
		new AssertListenerDecorator(
			new CompositeAssertion(assertions)
			, suiteAssertListeners
		).run();
	}

	public static void assertAll(AssertListener listener, Assertion... assertions) {
		new AssertListenerDecorator(
			new AssertListenerDecorator(
				  new CompositeAssertion(assertions) 
				, listener
		), suiteAssertListeners).run();
	}
}
