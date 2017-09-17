package org.testng.lambda;

public interface AssertListener {
	  void onAssertSuccess();
	  void onAssertFailure(AssertionError ex);
	  void onBeforeAssert();
	  void onAfterAssert();
}
