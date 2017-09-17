package org.testng.lambda;

import static java.lang.String.join;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

public class Assert {

	protected Assert() {
		// hide constructor
	}

	private static class AssertListenerDecorator implements Assertion {
		private Assertion assertion;
		private AssertListener listener;

		public AssertListenerDecorator(Assertion assertion, AssertListener listener) {
			this.assertion = assertion;
			this.listener = listener;
		}

		public void run() {
			try {
				listener.onBeforeAssert();
				assertion.run();
				listener.onAssertSuccess();
			} catch (AssertionError error) {
				listener.onAssertFailure(error);
				throw error;
			} finally {
				listener.onAfterAssert();
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

	public static Assertion assertTrue(boolean condition) {
		return () -> org.testng.Assert.assertTrue(condition);
	}

	public static Assertion assertTrue(boolean condition, AssertListener listener) {
		return new AssertListenerDecorator(assertTrue(condition), listener);
	}

	public static Assertion assertTrue(boolean condition, String message) {
		return () -> org.testng.Assert.assertTrue(condition, message);
	}

	public static Assertion assertTrue(boolean condition, String message, AssertListener listener) {
		return new AssertListenerDecorator(assertTrue(condition, message), listener);
	}

	public static Assertion assertEquals(int actual, int expected, String message) {
		return () -> org.testng.Assert.assertEquals(actual, expected, message);	
	}

	public static Assertion assertEquals(int actual, int expected, String message, AssertListener listener) {
		return new AssertListenerDecorator(assertEquals(actual, expected, message), listener);
	}

	public static void assertAll(Assertion... assertions) {
		new CompositeAssertion(assertions).run();
	}

	public static void assertAll(AssertListener listener, Assertion... assertions) {
		new AssertListenerDecorator(
			  new CompositeAssertion(assertions) 
			, listener
		).run();
	}

	public static void main(String[] args) {

		AssertListener assertListener = new AssertListener() {

			@Override
			public void onAssertSuccess() {
				System.out.println("success");
			}

			@Override
			public void onAssertFailure(AssertionError ex) {
				System.out.println("failure");
			}

			@Override
			public void onBeforeAssert() {
				System.out.println("before");
			}

			@Override
			public void onAfterAssert() {
				System.out.println("after");
			}
		};

		AssertListener groupAssertListener = new AssertListener() {

			@Override
			public void onAssertSuccess() {
				System.out.println("group success");
			}

			@Override
			public void onAssertFailure(AssertionError ex) {
				System.out.println("group failure");
			}

			@Override
			public void onBeforeAssert() {
				System.out.println("group before");
			}

			@Override
			public void onAfterAssert() {
				System.out.println("group after");
			}
		};

		assertAll(
			  groupAssertListener
			, assertTrue(false)
			, assertTrue(false, "false with message")
			, assertEquals(1, 2, "1 is not 2")
			, assertTrue(true, assertListener)
			, assertTrue(false, assertListener)
		);
	}
}
