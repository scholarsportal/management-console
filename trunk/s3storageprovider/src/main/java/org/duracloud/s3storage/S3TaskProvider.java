package org.duracloud.s3storage;

import org.duracloud.storage.error.UnsupportedTaskException;
import org.duracloud.storage.provider.TaskProvider;
import org.jets3t.service.S3Service;

/**
 * @author: Bill Branan
 * Date: May 20, 2010
 */
public class S3TaskProvider implements TaskProvider {

    private String accessKeyId = null;
    private S3Service s3Service = null;

    public S3TaskProvider(String accessKey, String secretKey) {
        this.accessKeyId = accessKey;
        this.s3Service = S3ProviderUtil.getS3Service(accessKey, secretKey);
    }

    public String performTask(String taskName, String taskParameters) {
        throw new UnsupportedTaskException(taskName);
    }

    public String getTaskStatus(String taskName) {
        throw new UnsupportedTaskException(taskName);
    }

    protected String getBucketName(String spaceId) {
        return S3ProviderUtil.getBucketName(accessKeyId, spaceId);
    }
}
