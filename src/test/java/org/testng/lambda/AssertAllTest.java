package org.testng.lambda;

import org.testng.annotations.Test;

import static org.testng.Assert.fail;
import static org.testng.lambda.Assert.*;

public class AssertAllTest {

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

	@Test
	public void singleAssertAll() {
		assertAll(
			assertTrue(true, assertListener)
		);
	}
	
	@Test
	public void groupAssertAll_failSome() {
		try {
			assertAll(
				  groupAssertListener
				, assertTrue(false)
				, assertTrue(false, "false with message")
				, assertEquals(1, 2, "1 is not 2")
				, assertTrue(true, assertListener)
			);
			fail();
		} catch (AssertionError e) {
		}
	}

}
