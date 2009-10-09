package org.duracloud.durastore.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.duracloud.storage.error.StorageException;

public class RetryAdvice implements MethodInterceptor {

    private final Logger log = Logger.getLogger(getClass());

    private int maxRetries;

    public Object invoke(MethodInvocation invocation) throws Throwable {
        int numAttempts = 0;
        do {
            numAttempts++;
            try {
                return invocation.proceed();
            } catch (StorageException se) {
                if (se.isRetry()) {
                    if (numAttempts <= maxRetries) {
                        logRetry(invocation, se.getMessage());
                        Thread.sleep(5000);
                    } else {
                        logRetriesExceeded(invocation, se.getMessage());
                        throw se;
                    }
                } else {
                    throw se;
                }
            }
        } while (true);
    }

    private void logRetry(MethodInvocation invocation, String errorMsg) {
        if (log.isDebugEnabled()) {
            StringBuilder logMsg = new StringBuilder();
            logMsg.append("Caught StorageException (");
            logMsg.append(errorMsg);
            logMsg.append(") when attempting to call ");
            logMsg.append(invocation.getMethod());
            logMsg.append(" with arguments [");
            logMsg.append(buildMethodArgs(invocation));
            logMsg.append("] Retrying call.");
            log.debug(logMsg.toString());
        }
    }

    private void logRetriesExceeded(MethodInvocation invocation,
                                    String errorMsg) {
        if (log.isDebugEnabled()) {
            StringBuilder logMsg = new StringBuilder();
            logMsg.append("Caught StorageException (");
            logMsg.append(errorMsg);
            logMsg.append(") when attempting to call ");
            logMsg.append(invocation.getMethod());
            logMsg.append(" with arguments [");
            logMsg.append(buildMethodArgs(invocation));
            logMsg.append("] Max retries exceeded, throwing.");
            log.debug(logMsg.toString());
        }
    }

    private String buildMethodArgs(MethodInvocation invocation) {
        StringBuilder methodArgs = new StringBuilder();
        Object[] arguments = invocation.getArguments();
        for (int i = 0; i < arguments.length; i++) {
            Object argument = arguments[i];
            methodArgs.append(argument.toString());
            if (i < arguments.length - 1) {
                methodArgs.append(", ");
            }
        }
        return methodArgs.toString();
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
}