package org.duracloud.s3task;

import org.duracloud.storage.provider.TaskRunner;
import org.duracloud.s3storage.S3ProviderUtil;
import org.jets3t.service.S3Service;

/**
 * @author: Bill Branan
 * Date: May 21, 2010
 */
public class EnableStreamingTaskRunner implements TaskRunner {

    private static final String TASK_NAME = "enable-streaming";

    private String accessKeyId;    
    private S3Service s3Service;

    public EnableStreamingTaskRunner(String accessKeyId, S3Service s3Service) {
        this.accessKeyId = accessKeyId;
        this.s3Service = s3Service;
    }

    public String getName() {
        return TASK_NAME;
    }

    public String performTask(String taskParameters) {
        // Enable streaming
        return "Streaming Enabled";
    }

    public String getStatus() {
        // Get status
        return "Enable Streaming task complete";
    }

    protected String getBucketName(String spaceId) {
        return S3ProviderUtil.getBucketName(accessKeyId, spaceId);
    }

}
