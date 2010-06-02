package org.duracloud.s3task;

import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.s3storage.S3StorageProvider;
import org.duracloud.storage.provider.TaskRunner;
import org.duracloud.s3storage.S3ProviderUtil;
import org.jets3t.service.CloudFrontService;
import org.jets3t.service.CloudFrontServiceException;
import org.jets3t.service.S3Service;
import org.jets3t.service.model.cloudfront.StreamingDistribution;

/**
 * @author: Bill Branan
 * Date: May 21, 2010
 */
public class DisableStreamingTaskRunner extends BaseStreamingTaskRunner {

    private static final String TASK_NAME = "disable-streaming";

    public DisableStreamingTaskRunner(S3StorageProvider s3Provider,
                                      S3Service s3Service,
                                      CloudFrontService cfService) {
        this.s3Provider = s3Provider;
        this.s3Service = s3Service;
        this.cfService = cfService;
    }

    public String getName() {
        return TASK_NAME;
    }

    public String performTask(String taskParameters) {
        String spaceId = taskParameters;
        String bucketName = s3Provider.getBucketName(spaceId);
        String results;

        try {
            StreamingDistribution existingDist =
                getExistingDistribution(bucketName);

            if(existingDist != null) {
                String distId = existingDist.getId();
                if(existingDist.isEnabled()) {
                    // Disable the distribution
                    cfService.updateStreamingDistributionConfig(distId,
                                                                null,
                                                                null,
                                                                false);
                }
            } else {
                throw new RuntimeException("No streaming distribution " +
                                           "exists for space " + spaceId);
            }

            results = "Disable Streaming Task completed successfully";
        } catch(CloudFrontServiceException e) {
            results = "Disable Streaming Task failed due to " + e.getMessage();
        }

        return results;
    }

}