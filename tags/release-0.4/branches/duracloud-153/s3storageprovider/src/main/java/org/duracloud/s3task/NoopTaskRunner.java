package org.duracloud.s3task;

import org.duracloud.storage.provider.TaskRunner;

/**
 * This task does not actually do anything, but it does allow for tests to
 * ensure that task execution is operational.
 *
 * @author: Bill Branan
 * Date: May 21, 2010
 */
public class NoopTaskRunner implements TaskRunner {

    private static final String TASK_NAME = "noop";

    public String getName() {
        return TASK_NAME;
    }

    public String performTask(String taskParameters) {
        return "Success";
    }

    public String getStatus() {
        return "Success";
    }

}