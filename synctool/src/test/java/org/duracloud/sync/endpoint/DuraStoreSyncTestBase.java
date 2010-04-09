package org.duracloud.sync.endpoint;

import static junit.framework.Assert.assertEquals;
import org.apache.commons.io.FileUtils;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.common.model.Credential;
import org.duracloud.common.model.DuraCloudUserType;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.error.ContentStoreException;
import org.duracloud.sync.SyncTestBase;
import org.duracloud.sync.SyncToolTestConfig;
import org.duracloud.unittestdb.UnitTestDatabaseUtil;
import org.duracloud.unittestdb.domain.ResourceType;
import org.duracloud.unittestdb.util.StorageAccountTestUtil;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * @author: Bill Branan
 * Date: Apr 9, 2010
 */
public class DuraStoreSyncTestBase extends SyncTestBase {

    protected static String host;
    protected static String context;
    protected static String port;
    protected static ContentStore store;
    protected static String spaceId;
    protected static Credential rootCredential;
    protected File tempDir;

    @BeforeClass
    public static void beforeClass() throws Exception {
        host = "localhost";
        context = "durastore";
        port = getPort();

        String url =
            "http://" + host + ":" + port + "/" + context + "/stores";
        String accountXml = StorageAccountTestUtil.buildTestAccountXml();

        rootCredential = getRootCredential();
        RestHttpHelper restHelper = new RestHttpHelper(rootCredential);
        restHelper.post(url, accountXml, null);

        ContentStoreManager storeManager =
            new ContentStoreManagerImpl(host, port, context);
        assertNotNull(storeManager);
        storeManager.login(rootCredential);

        store = storeManager.getPrimaryContentStore();

        String random = String.valueOf(new Random().nextInt(99999));
        spaceId = "synctool-test-space-" + random;
    }

    private static Credential getRootCredential() throws Exception {
        UnitTestDatabaseUtil dbUtil = new UnitTestDatabaseUtil();
        ResourceType rootUser = ResourceType.fromDuraCloudUserType(
            DuraCloudUserType.ROOT);
        return dbUtil.findCredentialForResource(rootUser);
    }

    private static String getPort() throws Exception {
        String port = new SyncToolTestConfig().getPort();
        try { // Ensure the port is a valid port value
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            port = "8080";
        }
        return port;
    }

    @AfterClass
    public static void afterClass() {
        try {
            store.deleteSpace(spaceId);
        } catch(ContentStoreException e) {
            System.err.println("Failed to delete space " + spaceId +
                               " after tests");
        }
    }

    @Before
    public void setUp() throws Exception {
        tempDir = createTempDir("restart-dir");
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(tempDir);
    }

    protected void testSync(DuraStoreSyncEndpoint endpoint)
        throws Exception {
        // Space should be empty
        testEndpoint(endpoint, 0);

        // Sync new file
        File tempFile = File.createTempFile("temp", "file", tempDir);
        endpoint.syncFile(tempFile, tempDir);
        List<String> endpointContents = testEndpoint(endpoint, 1);
        assertEquals(tempFile.getName(), endpointContents.get(0));

        // Sync deleted file
        tempFile.delete();
        endpoint.syncFile(tempFile, tempDir);
        testEndpoint(endpoint, 0);
    }

    protected List<String> testEndpoint(DuraStoreSyncEndpoint endpoint,
                                        int expectedSize)
        throws Exception {
        List<String> spaceContents =
            iteratorToList(store.getSpaceContents(spaceId));
        assertEquals(expectedSize, spaceContents.size());

        List<String> endpointContents =
            iteratorToList(endpoint.getFilesList());
        assertEquals(expectedSize, endpointContents.size());

        assertEquals(spaceContents, endpointContents);
        return endpointContents;
    }

    protected List<String> iteratorToList(Iterator<String> it) {
        List<String> list = new ArrayList<String>();
        while(it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }


}
