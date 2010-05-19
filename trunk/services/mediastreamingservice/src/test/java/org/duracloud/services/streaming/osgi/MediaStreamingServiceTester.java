package org.duracloud.services.streaming.osgi;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.common.model.Credential;
import org.duracloud.services.streaming.MediaStreamingService;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Performs a test of the Media Streaming Service from within an OSGi
 * container. This test requires that a DuraStore application be available
 * on localhost:8080.
 *
 * @author Bill Branan
 *         Date: May 13, 2010
 */
public class MediaStreamingServiceTester {

    private MediaStreamingService service;
    private String workDir;

    // This needs to be set (and should not be included in commit) in order
    // for this test to pass. This is available for testing in lieu of
    // including the unit-test-db project in the OSGi container. 
    private String rootPassword = "";

    private final static String BASE_DIR_PROP = "base.dir";

    public MediaStreamingServiceTester(MediaStreamingService service)
        throws IOException {
        this.service = service;

        File workDir = new File(service.getServiceWorkDir());
        workDir.mkdirs();
        this.workDir = workDir.getAbsolutePath();
    }

    public void testMediaStreamingService() throws Exception {
        String serviceWorkDir = service.getServiceWorkDir();
        assertNotNull(serviceWorkDir);
        assertTrue(new File(serviceWorkDir).exists());
        testStartStopCycle();
    }

    public void testStartStopCycle() throws Exception {
        // Set Up
        String random = getRandom();
        String sourceSpaceId = "media-source-" + random;
        String viewerSpaceId = "media-viewer-" + random;
        service.setMediaSourceSpaceId(sourceSpaceId);
        service.setMediaViewerSpaceId(viewerSpaceId);
        service.setUsername("root");
        service.setPassword(rootPassword);

        ContentStore contentStore = getContentStore();

        // TODO: Test
        
//        } finally {
//            // Clean up
//            try {
//                contentStore.deleteSpace(sourceSpaceId);
//                contentStore.deleteSpace(destSpaceId);
//            } catch(ContentStoreException e) {
//                // Ignore
//            }
//        }
    }

    private ContentStore getContentStore() throws Exception {
        ContentStoreManager storeManager =
            new ContentStoreManagerImpl(service.getDuraStoreHost(),
                                        service.getDuraStorePort(),
                                        service.getDuraStoreContext());
        storeManager.login(new Credential("root", rootPassword));
        return storeManager.getPrimaryContentStore();
    }

    private String getRandom() {
        return String.valueOf(new Random().nextInt(99999));
    }

    private String getResourceDir() {
        String baseDir = System.getProperty(BASE_DIR_PROP);
        assertNotNull(baseDir);

        return baseDir + File.separator + "src/test/resources/";
    }
    
}
