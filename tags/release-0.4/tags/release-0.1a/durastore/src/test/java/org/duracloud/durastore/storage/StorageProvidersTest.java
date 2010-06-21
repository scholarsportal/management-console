
package org.duracloud.durastore.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.duracloud.common.model.Credential;
import org.duracloud.emcstorage.ProbedEMCStorageProvider;
import org.duracloud.rackspacestorage.ProbedRackspaceStorageProvider;
import org.duracloud.s3storage.ProbedS3StorageProvider;
import org.duracloud.storage.error.StorageException;
import org.duracloud.storage.domain.StorageProviderType;
import org.duracloud.storage.domain.test.db.UnitTestDatabaseUtil;
import org.duracloud.storage.provider.StorageProvider;

import static org.junit.Assert.assertNotNull;

/**
 * This class runs a suite of tests on the StorageProvider interface across all
 * support provider implementations.
 *
 * @author Andrew Woods
 */
public class StorageProvidersTest {

    protected final static Logger log =
            Logger.getLogger(StorageProvidersTest.class);

    private final static StorageProvidersTestInterface tester =
            new StorageProvidersTestProxyPipe();

    private final static StorageProvidersTestCore testCore =
            new StorageProvidersTestCore();

    private final static List<StorageProvider> storageProviders =
            new ArrayList<StorageProvider>();

    private final List<String> spaceIds = new ArrayList<String>();

    @BeforeClass
    public static void beforeClass() throws StorageException {

        final int NUM_PROVIDERS = 2;
        for (StorageProviderType providerType : StorageProviderType.values()) {
            Credential credential = getCredential(providerType);
            if (credential != null) {
                String user = credential.getUsername();
                String pass = credential.getPassword();

                StorageProvider provider = null;
                if (StorageProviderType.AMAZON_S3.equals(providerType)) {
                    provider = new ProbedS3StorageProvider(user, pass);
                } else if (StorageProviderType.EMC.equals(providerType)) {
                    //                    provider = new ProbedEMCStorageProvider(user, pass);
                } else if (StorageProviderType.RACKSPACE.equals(providerType)) {
                    provider = new ProbedRackspaceStorageProvider(user, pass);
                } else {
                    StringBuffer sb = new StringBuffer("NOT TESTING ");
                    sb.append("storage-provider: '" + providerType + "'");
                    log.info(sb.toString());
                }

                if (provider != null) {
                    storageProviders.add(provider);
                }
            }
        }

        Assert.assertEquals(NUM_PROVIDERS, storageProviders.size());
    }

    private static Credential getCredential(StorageProviderType type) {
        log.debug("Getting credential for: '" + type + "'");

        Credential credential = null;
        try {
            UnitTestDatabaseUtil dbUtil = new UnitTestDatabaseUtil();
            credential = dbUtil.findCredentialForProvider(type);
            assertNotNull(credential);

            String username = credential.getUsername();
            String password = credential.getPassword();
            assertNotNull(username);
            assertNotNull(password);
        } catch (Exception e) {
            log.warn("Error getting credential for: '" + type + "'");
        }

        return credential;
    }

    @Before
    public void setUp() {
        try {
            clean();
        } catch (Exception e) {
            log.info(e);
        }
    }

    @After
    public void tearDown() throws Exception {
        try {
            clean();
        } catch (Exception e) {
            log.info(e);
        }
    }

    @AfterClass
    public static void afterClass() {
        tester.close();
    }

    private void clean() {
        for (StorageProvider provider : storageProviders) {
            assertNotNull(provider);

            for (String spaceId : spaceIds) {
                deleteSpace(provider, spaceId);
            }
        }
    }

    private void deleteSpace(StorageProvider provider, String spaceId) {
        try {
            log.debug(provider.getClass().getName() + " delete: " + spaceId);
            provider.deleteSpace(spaceId);
        } catch (Exception e) {
        }
    }

    private String getNewSpaceId() {
        String random = String.valueOf(new Random().nextInt(99999));
        String spaceId = "duracloud-test-space." + random;
        spaceIds.add(spaceId);
        return spaceId;
    }

    private String getNewContentId() {
        String random = String.valueOf(new Random().nextInt(99999));
        String contentId = "duracloud-test-content." + random;
        return contentId;
    }

    @Test
    public void testGetSpaces() throws StorageException {
        log.debug("testGetSpaces()");
        String spaceId0 = getNewSpaceId();
        String spaceId1 = getNewSpaceId();
        for (StorageProvider provider : storageProviders) {
            tester.testGetSpaces(provider, spaceId0, spaceId1);
        }
    }

    @Test
    public void testGetSpaceContents() throws StorageException {
        log.debug("testGetSpaceContents()");
        String spaceId0 = getNewSpaceId();
        String contentId0 = getNewContentId();
        String contentId1 = getNewContentId();

        for (StorageProvider provider : storageProviders) {
            tester.testGetSpaceContents(provider,
                                        spaceId0,
                                        contentId0,
                                        contentId1);
        }
    }

    @Test
    public void testCreateSpace() throws StorageException {
        log.debug("testCreateSpace()");
        String spaceId0 = getNewSpaceId();

        for (StorageProvider provider : storageProviders) {
            tester.testCreateSpace(provider, spaceId0);
        }
    }

    @Test
    public void testDeleteSpace() throws StorageException {
        log.debug("testDeleteSpace()");
        String spaceId0 = getNewSpaceId();
        String spaceId1 = getNewSpaceId();

        for (StorageProvider provider : storageProviders) {
            tester.testDeleteSpace(provider, spaceId0, spaceId1);
        }
    }

    @Test
    public void testGetSpaceMetadata() throws StorageException {
        log.debug("testGetSpaceMetadata()");
        String spaceId0 = getNewSpaceId();

        for (StorageProvider provider : storageProviders) {
            tester.testGetSpaceMetadata(provider, spaceId0);
        }
    }

    // TODO: not all work
    @Test
    public void testSetSpaceMetadata() throws StorageException {
        log.debug("testSetSpaceMetadata()");
        String spaceId0 = getNewSpaceId();

        for (StorageProvider provider : storageProviders) {
            if (!ProbedEMCStorageProvider.class.isInstance(provider)) {
                tester.testSetSpaceMetadata(provider, spaceId0);
            }
        }
    }

    @Test
    public void testGetSpaceAccess() throws StorageException {
        log.debug("testGetSpaceAccess()");
        String spaceId0 = getNewSpaceId();

        for (StorageProvider provider : storageProviders) {
            tester.testGetSpaceAccess(provider, spaceId0);
        }
    }

    @Test
    public void testAddAndGetContent() throws Exception {
        log.debug("testAddAndGetContent()");
        String spaceId0 = getNewSpaceId();
        String contentId0 = getNewContentId();
        String contentId1 = getNewContentId();
        String contentId2 = getNewContentId();

        for (StorageProvider provider : storageProviders) {
            tester.testAddAndGetContent(provider,
                                        spaceId0,
                                        contentId0,
                                        contentId1,
                                        contentId2);
        }
    }

    // TODO: not all work
    @Test
    public void testAddAndGetContentOverwrite() throws Exception {
        log.debug("testAddAndGetContentOverwrite()");
        String spaceId0 = getNewSpaceId();
        String contentId0 = getNewContentId();
        String contentId1 = getNewContentId();

        for (StorageProvider provider : storageProviders) {
            if (!ProbedEMCStorageProvider.class.isInstance(provider)) {
                tester.testAddAndGetContentOverwrite(provider,
                                                     spaceId0,
                                                     contentId0,
                                                     contentId1);
            }
        }
    }

    @Test
    public void testAddContentLarge() throws Exception {
        log.debug("testAddContentLarge()");
        String spaceId0 = getNewSpaceId();
        String contentId0 = getNewContentId();
        String contentId1 = getNewContentId();

        for (StorageProvider provider : storageProviders) {
            tester.testAddContentLarge(provider,
                                       spaceId0,
                                       contentId0,
                                       contentId1);
        }
    }

    // TODO: not all work
    @Test
    public void testDeleteContent() throws StorageException {
        log.debug("testDeleteContent()");
        String spaceId0 = getNewSpaceId();
        String contentId0 = getNewContentId();
        String contentId1 = getNewContentId();

        for (StorageProvider provider : storageProviders) {
            if (!ProbedEMCStorageProvider.class.isInstance(provider)) {
                tester.testDeleteContent(provider,
                                         spaceId0,
                                         contentId0,
                                         contentId1);
            }
        }
    }

    @Test
    public void testSetContentMetadata() throws StorageException {
        log.debug("testSetContentMetadata()");
        String spaceId0 = getNewSpaceId();
        String spaceId1 = getNewSpaceId();
        String contentId0 = getNewContentId();

        for (StorageProvider provider : storageProviders) {
            tester.testSetContentMetadata(provider,
                                          spaceId0,
                                          spaceId1,
                                          contentId0);
        }
    }

    // TODO: not all work
    @Test
    public void testGetContentMetadata() throws StorageException {
        log.debug("testGetContentMetadata()");
        String spaceId0 = getNewSpaceId();
        String contentId0 = getNewContentId();

        for (StorageProvider provider : storageProviders) {
            if (!ProbedEMCStorageProvider.class.isInstance(provider)) {
                tester.testGetContentMetadata(provider, spaceId0, contentId0);
            }
        }
    }

    // TODO: need to implement
    @Test
    public void testSpaceAccess() throws Exception {

    }

    // TODO: need to implement
    @Test
    public void testContentAccess() throws Exception {

    }

}
