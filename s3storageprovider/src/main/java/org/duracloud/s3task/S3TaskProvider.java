package org.duracloud.s3task;

import org.duracloud.s3storage.S3ProviderUtil;
import org.duracloud.s3storage.S3StorageProvider;
import org.duracloud.storage.error.UnsupportedTaskException;
import org.duracloud.storage.provider.TaskProvider;
import org.duracloud.storage.provider.TaskRunner;
import org.jets3t.service.CloudFrontService;
import org.jets3t.service.S3Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles tasks specific to content stored in Amazon S3 
 *
 * @author: Bill Branan
 * Date: May 20, 2010
 */
public class S3TaskProvider implements TaskProvider {

    private List<TaskRunner> taskList = new ArrayList<TaskRunner>();

    public S3TaskProvider(String accessKey, String secretKey) {
        S3Service s3Service =
            S3ProviderUtil.getS3Service(accessKey, secretKey);
        S3StorageProvider s3Provider =
            new S3StorageProvider(accessKey, secretKey);
        CloudFrontService cfService =
            S3ProviderUtil.getCloudFrontService(accessKey, secretKey);

        taskList.add(new NoopTaskRunner());
        taskList.add(new EnableStreamingTaskRunner(s3Provider,
                                                   s3Service,
                                                   cfService));
        taskList.add(new DisableStreamingTaskRunner(s3Provider,
                                                    s3Service,
                                                    cfService));
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
    
}
