package org.duracloud.s3task;

import org.duracloud.s3storage.S3ProviderUtil;
import org.duracloud.storage.error.UnsupportedTaskException;
import org.duracloud.storage.provider.TaskRunner;
import org.duracloud.storage.provider.TaskProvider;
import org.jets3t.service.S3Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Bill Branan
 * Date: May 20, 2010
 */
public class S3TaskProvider implements TaskProvider {

    private List<TaskRunner> taskList = new ArrayList<TaskRunner>();

    public S3TaskProvider(String accessKey, String secretKey) {
        S3Service s3Service = S3ProviderUtil.getS3Service(accessKey, secretKey);

        taskList.add(new NoopTaskRunner());
        taskList.add(new EnableStreamingTaskRunner(accessKey, s3Service));
        taskList.add(new DisableStreamingTaskRunner(accessKey, s3Service));
    }

    public List<String> getSupportedTasks() {
        List<String> supportedTasks = new ArrayList<String>();
        for(TaskRunner runner : taskList) {
            supportedTasks.add(runner.getName());
        }
        return supportedTasks;
    }

    public String performTask(String taskName, String taskParameters) {
        for(TaskRunner runner : taskList) {
            if(runner.getName().equals(taskName)) {
                return runner.performTask(taskParameters);
            }
        }
        throw new UnsupportedTaskException(taskName);
    }

    public String getTaskStatus(String taskName) {
        for(TaskRunner runner : taskList) {
            if(runner.getName().equals(taskName)) {
                return runner.getStatus();
            }
        }
        throw new UnsupportedTaskException(taskName);
    }
    
}
