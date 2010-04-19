package org.duracloud.sync;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.common.model.Credential;
import org.duracloud.common.model.DuraCloudUserType;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.unittestdb.util.StorageAccountTestUtil;
import org.duracloud.unittestdb.UnitTestDatabaseUtil;
import org.duracloud.unittestdb.domain.ResourceType;
import org.duracloud.error.ContentStoreException;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Random;

/**
 * @author: Bill Branan
 * Date: Apr 16, 2010
 */
public class SyncIntegrationTestBase extends SyncTestBase {

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

    @Override
    @Before
    public void setUp() throws Exception {
        tempDir = createTempDir("sync-test-dir");
    }

    @Override
    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(tempDir);
    }    

}
