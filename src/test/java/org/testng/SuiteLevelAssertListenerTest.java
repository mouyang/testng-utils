package org.testng;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.lambda.AssertListener;

import static org.testng.Assert.fail;
import static org.testng.lambda.Assert.*;

@Listeners(value = {SuiteLevelAssertListenerTest.SampleSuiteListener.class})
public class SuiteLevelAssertListenerTest {

	public static class SampleSuiteListener implements ISuiteListener {
		private static class SampleAssertListener implements AssertListener {
			
			private final int id;

			public SampleAssertListener(int id) {
				this.id = id;
			}

			@Override
			public void onAssertSuccess() {
				System.out.println("suite success" + id);
			}

			@Override
			public void onAssertFailure(AssertionError ex) {
				System.out.println("suite failure" + id);
			}

			@Override
			public void onBeforeAssert() {
				System.out.println("suite before" + id);
			}

			@Override
			public void onAfterAssert() {
				System.out.println("suite after" + id);
			}
		}

		@Override
		public void onStart(ISuite suite) {
			addSuiteAssertListener(new SampleAssertListener(1));
			addSuiteAssertListener(new SampleAssertListener(2));
		}

		@Override
		public void onFinish(ISuite suite) {
		}
	}

	@Test
	public void verifyOnSuccess() {
		assertTrue(true).run();
	}

	@Test
	public void verifyOnFailure() {
		try {
			assertTrue(false).run();
			fail();
		} catch (AssertionError e) {
		}
	}
}
