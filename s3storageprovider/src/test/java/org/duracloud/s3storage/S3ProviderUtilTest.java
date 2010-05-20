package org.duracloud.s3storage;

import org.junit.Test;
import static junit.framework.Assert.assertEquals;

/**
 * @author: Bill Branan
 * Date: May 20, 2010
 */
public class S3ProviderUtilTest {

    @Test
    public void testGetBucketName() throws Exception {
        String accessKey = "abc";

        String bucketName = S3ProviderUtil.getBucketName(accessKey, "xyz");
        assertEquals("abc.xyz", bucketName);

        bucketName = S3ProviderUtil.getBucketName(accessKey,
                                                  "x~!@#$%^&*(){}:;'\"<>,?/|z");
        assertEquals("abc.x-z", bucketName);

        bucketName = S3ProviderUtil.getBucketName(accessKey, "x--..y..--z-.");
        assertEquals("abc.x-y.z", bucketName);
    }
}
