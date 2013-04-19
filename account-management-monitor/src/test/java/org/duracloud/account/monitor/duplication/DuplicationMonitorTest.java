/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.duplication;

import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.monitor.duplication.domain.DuplicationInfo;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreImpl;
import org.duracloud.client.ContentStoreManager;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Bill Branan
 *         Date: 4/19/13
 */
public class DuplicationMonitorTest {

    private ContentStoreManager storeManager;
    private ContentStore store;
    private DuracloudAccountRepo acctRepo;
    private DuracloudInstanceRepo instanceRepo;
    private DuracloudServerImageRepo imageRepo;
    private Map<String, String> dupHosts;
    private DuplicationMonitor dupMonitor;

    @Before
    public void setup() {
        storeManager = EasyMock.createMock(ContentStoreManager.class);
        store = EasyMock.createMock(ContentStore.class);
        acctRepo = EasyMock.createMock(DuracloudAccountRepo.class);
        instanceRepo = EasyMock.createMock(DuracloudInstanceRepo.class);
        imageRepo = EasyMock.createMock(DuracloudServerImageRepo.class);
        dupHosts = new HashMap<>();
        dupMonitor = new DuplicationMonitor(acctRepo,
                                            instanceRepo,
                                            imageRepo,
                                            dupHosts);
    }

    private void replayMocks() {
        EasyMock.replay(storeManager, store, acctRepo, instanceRepo, imageRepo);
    }

    @After
    public void teardown() {
        EasyMock.verify(storeManager, store, acctRepo, instanceRepo, imageRepo);
    }

    @Test
    public void testGetSecondaryStore() throws Exception {
        String primaryId = "primary-id";
        String secondaryId = "secondary-id";
        Map<String, ContentStore> stores = new HashMap<>();
        stores.put(primaryId,
                   new ContentStoreImpl(null, null, primaryId, null));
        stores.put(secondaryId,
                   new ContentStoreImpl(null, null, secondaryId, null));

        EasyMock.expect(storeManager.getContentStores()).andReturn(stores);

        replayMocks();

        ContentStore secondary =
            dupMonitor.getSecondaryStore(storeManager, primaryId);
        assertNotNull(secondary);
        assertEquals(secondaryId, secondary.getStoreId());
    }

    @Test
    public void testGetSpacesAll() throws Exception {
        String host = "host";
        dupHosts.put(host, DuplicationMonitor.ALL_SPACES);
        List<String> allSpaces =
            Arrays.asList("space-1", "space-2", "x-duracloud-admin");
        EasyMock.expect(store.getSpaces()).andReturn(allSpaces);

        replayMocks();

        List<String> dupSpaces = dupMonitor.getSpaces(host, store);
        assertNotNull(dupSpaces);
        assertEquals(2, dupSpaces.size());
        assertTrue(dupSpaces.contains("space-1"));
        assertTrue(dupSpaces.contains("space-2"));
        assertFalse(dupSpaces.contains("x-duracloud-admin"));
    }

    @Test
    public void testGetSpacesLimited() throws Exception {
        String host = "host";
        dupHosts.put(host, "space-A,space-B");

        replayMocks();

        List<String> dupSpaces = dupMonitor.getSpaces(host, store);
        assertNotNull(dupSpaces);
        assertEquals(2, dupSpaces.size());
        assertTrue(dupSpaces.contains("space-A"));
        assertTrue(dupSpaces.contains("space-B"));
    }

    @Test
    public void testCountSpaces() throws Exception {
        String host = "host";
        String space1 = "space-1";
        String space2 = "space-2";
        DuplicationInfo dupInfo = new DuplicationInfo(host);
        List<String> spaces = Arrays.asList(space1, space2);

        EasyMock.expect(store.getSpaceContents(space1))
                .andReturn(Arrays.asList("1").iterator());
        EasyMock.expect(store.getSpaceContents(space2))
                .andReturn(Arrays.asList("1", "2").iterator());

        replayMocks();

        dupMonitor.countSpaces(host, dupInfo, store, spaces, true);
        Map<String, Long> spaceCounts = dupInfo.getPrimarySpaceCounts();
        assertEquals(new Long(1), spaceCounts.get(space1));
        assertEquals(new Long(2), spaceCounts.get(space2));
    }

    @Test
    public void testCompareSpaces() throws Exception {
        DuplicationInfo dupInfo = new DuplicationInfo("host");
        assertFalse(dupInfo.hasIssues());
        assertEquals(0, dupInfo.getIssues().size());

        String space1 = "space-1";
        dupInfo.addPrimarySpace(space1, 100);
        dupInfo.addSecondarySpace(space1, 200);
        assertFalse(dupInfo.hasIssues());

        dupMonitor.compareSpaces(dupInfo);
        assertTrue(dupInfo.hasIssues());
        assertEquals(1, dupInfo.getIssues().size());

        replayMocks();
    }

}
