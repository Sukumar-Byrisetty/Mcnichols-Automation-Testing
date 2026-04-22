package com.mcnichols.framework.util.listeners;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import com.mcnichols.framework.util.Logger;

public class InvokedMethodListener implements IInvokedMethodListener {
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (method.isTestMethod() && ITestResult.FAILURE == result.getStatus()) {
            Throwable throwable = result.getThrowable();
            String originalMessage = throwable.getMessage();
            String newMessage = originalMessage + "";
            try {
                //FieldUtils.writeField(throwable, "detailMessage", newMessage, true);
            	Logger.fail("FAILURE MESSAGE: " + "Method Name - " + method.getTestMethod().getMethodName() +  " - " + newMessage);
            	//Logger.fail("FAILURE MESSAGE: " + " - " + newMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}