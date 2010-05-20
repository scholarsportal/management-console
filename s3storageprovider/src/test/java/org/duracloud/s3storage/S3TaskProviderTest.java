package org.duracloud.s3storage;

import org.duracloud.common.model.Credential;
import org.duracloud.storage.error.UnsupportedTaskException;
import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.fail;
import static junit.framework.Assert.assertNotNull;

/**
 * @author: Bill Branan
 * Date: May 20, 2010
 */
public class S3TaskProviderTest extends S3ProviderTestBase {

    private S3TaskProvider taskProvider;

    @Before
    public void setUp() throws Exception {
        Credential s3Credential = getCredential();
        taskProvider = new S3TaskProvider(s3Credential.getUsername(), 
                                          s3Credential.getPassword());
    }

    @Test
    public void testPerformTask() throws Exception {
        try {
            taskProvider.performTask("task", "parameters");
            fail("Exception expected performing unknown task");
        } catch(UnsupportedTaskException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void testGetTaskStatus() throws Exception {
        try {
            taskProvider.performTask("task", "parameters");
            fail("Exception expected performing unknown task");
        } catch(UnsupportedTaskException expected) {
            assertNotNull(expected);
        }        
    }
}
