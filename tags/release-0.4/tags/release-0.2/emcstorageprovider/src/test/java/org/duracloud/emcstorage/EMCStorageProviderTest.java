package org.duracloud.emcstorage;

import com.emc.esu.api.EsuApi;
import com.emc.esu.api.Identifier;
import com.emc.esu.api.ObjectMetadata;
import com.emc.esu.api.rest.EsuRestApi;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import junit.framework.Assert;
import org.duracloud.common.model.Credential;
import org.duracloud.common.util.ChecksumUtil;
import org.duracloud.common.util.ChecksumUtil.Algorithm;
import org.duracloud.storage.domain.StorageProviderType;
import org.duracloud.storage.domain.test.db.UnitTestDatabaseUtil;
import org.duracloud.storage.error.StorageException;
import org.duracloud.storage.error.NotFoundException;
import org.duracloud.storage.provider.StorageProvider;
import org.duracloud.storage.provider.StorageProvider.AccessType;
import static org.duracloud.storage.util.StorageProviderUtil.compareChecksum;
import static org.duracloud.storage.util.StorageProviderUtil.contains;
import static org.duracloud.storage.util.StorageProviderUtil.count;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EMCStorageProviderTest {

    private EMCStorageProvider emcProvider;

    private final List<String> spaceIds = new ArrayList<String>();

    private final String contentId = "contentid";

    private final String contentId0 = "contentid0";

    private final String contentId1 = "contentid1";

    private final String mimeText = "text/plain";

    private final String mimeXml = "text/xml";

    final private String ESU_HOST = "accesspoint.emccis.com";

    final private int ESU_PORT = 80;

    @Before
    public void setUp() throws Exception {
        Credential emcCredential = getCredential(StorageProviderType.EMC);
        assertNotNull(emcCredential);

        String username = emcCredential.getUsername();
        String password = emcCredential.getPassword();
        assertNotNull(username);
        assertNotNull(password);
        try {
            emcProvider = new EMCStorageProvider(username, password);
        } catch (Exception e) {
            // do nothing
        }
        clean();
    }

    private Credential getCredential(StorageProviderType type)
        throws Exception {
        UnitTestDatabaseUtil dbUtil = new UnitTestDatabaseUtil();
        return dbUtil.findCredentialForProvider(type);
    }

    @After
    public void tearDown() throws Exception {
        clean();
        emcProvider = null;
    }

    private void clean() {
        assertNotNull(emcProvider);

        for (String spaceId : spaceIds) {
            deleteSpace(spaceId);
        }
    }

    private void fullClean() {
        Iterator<String> spaces = emcProvider.getSpaces();
        while (spaces.hasNext()) {
            deleteSpace(spaces.next());
        }
    }

    private void deleteSpace(String id) {
        try {
            emcProvider.deleteSpace(id);
        } catch (Exception e) {
            // do nothing
        }
    }

    private String getNewSpaceId() {
        String random = String.valueOf(new Random().nextInt(99999));
        String spaceId = "duracloud-test-space." + random;
        spaceIds.add(spaceId);
        return spaceId;
    }

    @Test
    public void testGetSpaces() throws StorageException {
        Iterator<String> spaces = emcProvider.getSpaces();
        assertNotNull(spaces);

        long initialNumSpaces = count(spaces);

        String spaceId0 = getNewSpaceId();
        String spaceId1 = getNewSpaceId();

        emcProvider.createSpace(spaceId0);
        emcProvider.createSpace(spaceId1);

        spaces = emcProvider.getSpaces();
        assertNotNull(spaces);
        assertEquals(initialNumSpaces + 2, count(spaces));

        spaces = emcProvider.getSpaces();
        assertNotNull(spaces);
        assertTrue(contains(spaces, spaceId0));

        spaces = emcProvider.getSpaces();
        assertNotNull(spaces);
        assertTrue(contains(spaces, spaceId1));
    }

    @Test
    public void testGetSpaceContents() throws StorageException {
        String spaceId0 = getNewSpaceId();

        Iterator<String> spaceContents;
        try {
            emcProvider.getSpaceContents(spaceId0, null);
            fail("Exception expected since space does not exist.");
        } catch (Exception e) {
            // do nothing
        }

        emcProvider.createSpace(spaceId0);

        // First content to add
        byte[] content0 = "hello world.".getBytes();
        addContent(spaceId0, contentId0, mimeText, content0);

        // Second content to add
        byte[] content1 = "<a>hello</a>".getBytes();
        addContent(spaceId0, contentId1, mimeXml, content1);

        spaceContents = emcProvider.getSpaceContents(spaceId0, null);
        assertNotNull(spaceContents);
        assertEquals(2, count(spaceContents));

        spaceContents = emcProvider.getSpaceContents(spaceId0, null);
        assertNotNull(spaceContents);
        assertTrue(contains(spaceContents, contentId0));

        spaceContents = emcProvider.getSpaceContents(spaceId0, null);
        assertNotNull(spaceContents);
        assertTrue(contains(spaceContents, contentId1));
    }

    @Test
    public void testGetSpaceContentsChunked() throws StorageException {
        String spaceId0 = getNewSpaceId();

        List<String> spaceContents;
        try {
            emcProvider.getSpaceContentsChunked(spaceId0, null, 10, null);
            fail("Exception expected since space does not exist.");
        } catch (Exception e) {
            // do nothing
        }

        emcProvider.createSpace(spaceId0);

        // First content to add
        byte[] content0 = "hello world.".getBytes();
        addContent(spaceId0, contentId0, mimeText, content0);

        // Second content to add
        byte[] content1 = "<a>hello</a>".getBytes();
        addContent(spaceId0, contentId1, mimeXml, content1);

        spaceContents =
            emcProvider.getSpaceContentsChunked(spaceId0, null, 10, null);
        assertNotNull(spaceContents);
        assertEquals(2, spaceContents.size());

        spaceContents =
            emcProvider.getSpaceContentsChunked(spaceId0, null, 10, null);
        assertNotNull(spaceContents);
        assertTrue(spaceContents.contains(contentId0));
        assertTrue(spaceContents.contains(contentId1));

        // TODO: Implement further tests once EMC supports list chunking
    }

    private void addContent(String spaceKey,
                            String contentKey,
                            String mime,
                            byte[] data) throws StorageException {
        ByteArrayInputStream contentStream = new ByteArrayInputStream(data);
        String checksum = emcProvider.addContent(spaceKey,
                                                 contentKey,
                                                 mime,
                                                 data.length,
                                                 contentStream);
        compareChecksum(emcProvider, spaceKey, contentKey, checksum);
    }

    @Test
    public void testCreateSpace() throws StorageException {
        String spaceId = getNewSpaceId();

        final boolean isExpected = true;
        verifySpaceExists(spaceId, !isExpected);

        emcProvider.createSpace(spaceId);

        verifySpaceExists(spaceId, isExpected);
        verifySpaceMetadata(spaceId);

        try {
            emcProvider.createSpace(spaceId);
            fail("Exception expected: space already exists.");
        } catch (Exception e) {
        }
    }

    private void verifySpaceExists(String space, boolean expected)
        throws StorageException {
        boolean found = false;
        try {
            Iterator<String> spaces = emcProvider.getSpaces();
            assertNotNull(spaces);

            while (spaces.hasNext()) {
                String s = spaces.next();
                if (space.equals(s)) {
                    found = true;
                }
            }
        } catch (StorageException e) {
            // do nothing
        }
        assertEquals(expected, found);
    }

    private void verifySpaceMetadata(String space) throws StorageException {
        Map<String, String> metadata = emcProvider.getSpaceMetadata(space);
        assertNotNull(metadata);

        String created = metadata.get(EMCStorageProvider.METADATA_SPACE_CREATED);
        assertNotNull(created);
    }

    @Test
    public void testDeleteSpace() throws StorageException {
        String spaceId1 = getNewSpaceId();
        String spaceId2 = getNewSpaceId();

        // Verify initial state.
        try {
            emcProvider.deleteSpace(spaceId1);
            fail("Exception expected.");
        } catch (Exception e) {
        }

        Iterator<String> spaces = emcProvider.getSpaces();
        assertNotNull(spaces);

        long initialNumSpaces = count(spaces);

        // Add some spaces.
        emcProvider.createSpace(spaceId1);
        emcProvider.createSpace(spaceId2);
        spaces = emcProvider.getSpaces();
        assertNotNull(spaces);
        assertEquals(initialNumSpaces + 2, count(spaces));

        spaces = emcProvider.getSpaces();
        assertNotNull(spaces);
        assertTrue(contains(spaces, spaceId1));

        spaces = emcProvider.getSpaces();
        assertNotNull(spaces);
        assertTrue(contains(spaces, spaceId2));

        // Now check deletions.
        // ...first.
        emcProvider.deleteSpace(spaceId2);
        spaces = emcProvider.getSpaces();
        assertNotNull(spaces);
        assertEquals(initialNumSpaces + 1, count(spaces));

        spaces = emcProvider.getSpaces();
        assertNotNull(spaces);
        assertTrue(contains(spaces, spaceId1));

        spaces = emcProvider.getSpaces();
        assertNotNull(spaces);
        assertFalse(contains(spaces, spaceId2));

        // ...second.
        emcProvider.deleteSpace(spaceId1);
        spaces = emcProvider.getSpaces();
        assertNotNull(spaces);
        assertEquals(initialNumSpaces, count(spaces));

    }

    @Test
    public void testGetSpaceMetadata() throws StorageException {
        String spaceId = getNewSpaceId();

        Map<String, String> spaceMd;
        try {
            emcProvider.getSpaceMetadata(spaceId);
            fail("Exception expected.");
        } catch (Exception e) {
        }

        emcProvider.createSpace(spaceId);
        spaceMd = emcProvider.getSpaceMetadata(spaceId);
        assertNotNull(spaceMd);

        assertTrue(spaceMd.containsKey(StorageProvider.METADATA_SPACE_CREATED));
        assertTrue(spaceMd.containsKey(StorageProvider.METADATA_SPACE_COUNT));
        assertTrue(spaceMd.containsKey(StorageProvider.METADATA_SPACE_ACCESS));

        assertNotNull(spaceMd.get(StorageProvider.METADATA_SPACE_CREATED));
        assertNotNull(spaceMd.get(StorageProvider.METADATA_SPACE_COUNT));
        assertNotNull(spaceMd.get(StorageProvider.METADATA_SPACE_ACCESS));
    }

    @Test
    public void testSetSpaceMetadata() throws StorageException {
        String spaceId2 = getNewSpaceId();

        try {
            emcProvider.setSpaceMetadata(spaceId2,
                                         new HashMap<String, String>());
            fail("Exception expected.");
        } catch (Exception e) {
            // do nothing
        }

        emcProvider.createSpace(spaceId2);

        final String key0 = "key0";
        final String key1 = "key1";
        final String key2 = "key2";
        final String val0 = "val0";
        final String val1 = "val1";
        final String val2 = "val2";

        Map<String, String> spaceMd = emcProvider.getSpaceMetadata(spaceId2);
        assertNotNull(spaceMd);

        final int numProps = spaceMd.size();
        assertTrue(numProps > 0);

        // Add some props.
        Map<String, String> newMd = new HashMap<String, String>();
        newMd.put(key0, val0);
        newMd.put(key2, val2);
        emcProvider.setSpaceMetadata(spaceId2, newMd);

        spaceMd = emcProvider.getSpaceMetadata(spaceId2);
        assertNotNull(spaceMd);
        assertEquals(numProps + 2, spaceMd.size());

        assertTrue(spaceMd.containsKey(key0));
        assertFalse(spaceMd.containsKey(key1));
        assertTrue(spaceMd.containsKey(key2));

        assertEquals(val0, spaceMd.get(key0));
        assertEquals(val2, spaceMd.get(key2));

        // Add some different props.
        Map<String, String> newerMd = new HashMap<String, String>();
        newerMd.put(key1, val1);
        newerMd.put(key2, val2);
        emcProvider.setSpaceMetadata(spaceId2, newerMd);

        spaceMd = emcProvider.getSpaceMetadata(spaceId2);
        assertNotNull(spaceMd);
        assertEquals(numProps + 2, spaceMd.size());

        assertFalse(spaceMd.containsKey(key0));
        assertTrue(spaceMd.containsKey(key1));
        assertTrue(spaceMd.containsKey(key2));

        assertEquals(val1, spaceMd.get(key1));
        assertEquals(val2, spaceMd.get(key2));

    }

    @Test
    public void testGetSpaceAccess() throws StorageException {
        String spaceId0 = getNewSpaceId();

        AccessType access;
        try {
            emcProvider.getSpaceAccess(spaceId0);
            fail("Exception expected.");
        } catch (Exception e) {
            // do nothing.
        }

        // Test default access.
        emcProvider.createSpace(spaceId0);
        access = emcProvider.getSpaceAccess(spaceId0);
        assertEquals(AccessType.CLOSED, access);

        // ...also check Access in user metadata.
        Map<String, String> spaceMd = emcProvider.getSpaceMetadata(spaceId0);
        assertNotNull(spaceMd);
        String prop = spaceMd.get(StorageProvider.METADATA_SPACE_ACCESS);
        assertNotNull(prop);
        assertEquals(AccessType.CLOSED.toString(), prop);

        // Close access, and test again.
        emcProvider.setSpaceAccess(spaceId0, AccessType.OPEN);
        access = emcProvider.getSpaceAccess(spaceId0);
        assertEquals(AccessType.OPEN, access);

        // ...also check Access in user metadata.
        spaceMd = emcProvider.getSpaceMetadata(spaceId0);
        assertNotNull(spaceMd);
        prop = spaceMd.get(StorageProvider.METADATA_SPACE_ACCESS);
        assertNotNull(prop);
        assertEquals(AccessType.OPEN.toString(), prop);
    }

    @Test
    public void testAddAndGetContent() throws Exception {
        String spaceId0 = getNewSpaceId();

        byte[] content0 = "hello,world.".getBytes();
        try {
            addContent(spaceId0, contentId, mimeText, content0);
            fail("Exception expected");
        } catch (Exception e) {
        }

        // Need to have a space.
        emcProvider.createSpace(spaceId0);

        // First content to add
        addContent(spaceId0, contentId0, mimeText, content0);

        // Second content to add
        byte[] content1 = "<a>hello</a>".getBytes();
        addContent(spaceId0, contentId1, mimeXml, content1);

        // Verify content on retrieval.
        InputStream is0 = emcProvider.getContent(spaceId0, contentId0);
        assertNotNull(is0);
        assertEquals(new String(content0), getData(is0));

        InputStream is1 = emcProvider.getContent(spaceId0, contentId1);
        assertNotNull(is1);
        assertEquals(new String(content1), getData(is1));

    }

    @Test
    public void testAddAndGetContentOverwrite() throws Exception {
        String spaceId0 = getNewSpaceId();

        emcProvider.createSpace(spaceId0);

        byte[] content0 = "hello,world.".getBytes();

        // First content to add
        addContent(spaceId0, contentId0, mimeText, content0);

        // Second content to add
        byte[] content1 = "<a>hello</a>".getBytes();
        addContent(spaceId0, contentId1, mimeXml, content1);

        // Verify content on retrieval.
        InputStream is0 = emcProvider.getContent(spaceId0, contentId0);
        assertNotNull(is0);
        assertEquals(new String(content0), getData(is0));

        // Overwrite existing content
        addContent(spaceId0, contentId0, mimeXml, content1);
        InputStream is1 = emcProvider.getContent(spaceId0, contentId0);
        assertNotNull(is1);
        assertEquals(new String(content1), getData(is1));
    }

    @Test
    public void testAddContentLarge() throws Exception {
        String spaceId0 = getNewSpaceId();

        // TODO: maybe turn this test on?
        System.err.println("==================");
        System.err.println("This test is not run because it " +
            "uploads 16MB of data to EMC: " +
            "EMCStorageProviderTest.testAddContentLarge()");
        System.err.println("==================");

        if (false) {
            // Create large data object (16MB)
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 1000000; ++i) {
                sb.append("xxxxxxxxxxxxxxxx");
            }
            byte[] content0 = sb.toString().getBytes();

            try {
                addContent(spaceId0, contentId, mimeText, content0);
                fail("Exception expected");
            } catch (Exception e) {
            }

            // Need to have a space.
            emcProvider.createSpace(spaceId0);

            // First content to add
            addContent(spaceId0, contentId0, mimeText, content0);

            // Verify content on retrieval.
            InputStream is0 = emcProvider.getContent(spaceId0, contentId0);
            assertNotNull(is0);
            assertEquals(new String(content0), getData(is0));
        }
    }

    private String getData(InputStream is) throws IOException {
        List<Byte> bytes = new ArrayList<Byte>();
        byte b = (byte) is.read();
        while (b != -1) {
            bytes.add(b);
            b = (byte) is.read();
        }

        byte[] data = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); ++i) {
            data[i] = bytes.get(i);
        }
        return new String(data);
    }

    @Test
    public void testDeleteContent() throws StorageException {
        String spaceId = getNewSpaceId();

        try {
            emcProvider.deleteContent(spaceId, contentId);
            fail("Exception expected.");
        } catch (Exception e) {
        }

        emcProvider.createSpace(spaceId);
        Iterator<String> spaceContents =
            emcProvider.getSpaceContents(spaceId, null);
        verifyContentListing(spaceContents);

        try {
            emcProvider.deleteContent(spaceId, contentId);
            fail("Exception expected.");
        } catch (Exception e) {
        }

        // Add some content.
        byte[] data = "sample-text".getBytes();
        addContent(spaceId, contentId, mimeText, data);

        spaceContents = emcProvider.getSpaceContents(spaceId, null);
        verifyContentListing(spaceContents, contentId);

        // Add more content.
        addContent(spaceId, contentId0, mimeText, data);

        spaceContents = emcProvider.getSpaceContents(spaceId, null);
        verifyContentListing(spaceContents, contentId, contentId0);

        // Delete content
        emcProvider.deleteContent(spaceId, contentId0);
        spaceContents = emcProvider.getSpaceContents(spaceId, null);
        verifyContentListing(spaceContents, contentId);

        emcProvider.deleteContent(spaceId, contentId);
        spaceContents = emcProvider.getSpaceContents(spaceId, null);
        verifyContentListing(spaceContents);

    }

    private void verifyContentListing(Iterator<String> listing,
                                      String... entries) {
        assertNotNull(listing);
        int count = 0;
        while (listing.hasNext()) {
            ++count;
            String listingItem = listing.next();
            boolean entryMatch = false;
            for (String entry : entries) {
                if (listingItem.equals(entry)) {
                    entryMatch = true;
                }
            }
            assertTrue(entryMatch);
        }
        assertEquals(entries.length, count);
    }

    @Test
    public void testSetContentMetadata() throws StorageException {
        String spaceId = getNewSpaceId();
        String spaceId2 = getNewSpaceId();

        Map<String, String> contentMetadata = new HashMap<String, String>();
        final String key0 = "key0";
        final String key1 = "key1";
        final String val0 = "val0";
        final String val1 = "val1";

        contentMetadata.put(key0, val0);
        try {
            emcProvider.setContentMetadata(spaceId, contentId, contentMetadata);
            fail("Exception expected.");
        } catch (Exception e) {
        }

        // Need to have a space and content.
        emcProvider.createSpace(spaceId2);
        addContent(spaceId2, contentId, mimeText, "hello".getBytes());

        // Check initial state of metadata.
        Map<String, String> initialMeta = emcProvider.getContentMetadata(
            spaceId2,
            contentId);
        assertNotNull(initialMeta);

        int initialSize = initialMeta.size();
        assertTrue(initialSize > 0);

        // Set and check.
        emcProvider.setContentMetadata(spaceId2, contentId, contentMetadata);
        Map<String, String> metadata = emcProvider.getContentMetadata(spaceId2,
                                                                      contentId);
        assertNotNull(metadata);
        assertEquals(initialSize + 1, metadata.size());
        assertTrue(metadata.containsKey(key0));
        assertEquals(val0, metadata.get(key0));

        final String newVal = "newVal0";
        contentMetadata.put(key0, newVal);
        contentMetadata.put(key1, val1);

        emcProvider.setContentMetadata(spaceId2, contentId, contentMetadata);
        metadata = emcProvider.getContentMetadata(spaceId2, contentId);
        assertNotNull(metadata);
        assertEquals(initialSize + 2, metadata.size());
        assertTrue(metadata.containsKey(key0));
        assertEquals(newVal, metadata.get(key0));

        assertTrue(metadata.containsKey(key1));
        assertEquals(val1, metadata.get(key1));

        // Clear properties.
        emcProvider.setContentMetadata(spaceId2,
                                       contentId,
                                       new HashMap<String, String>());
        metadata = emcProvider.getContentMetadata(spaceId2, contentId);
        assertNotNull(metadata);
        assertEquals(initialSize, metadata.size());
        assertFalse(metadata.containsKey(key0));
        assertFalse(metadata.containsKey(key1));
    }

    @Test
    public void testGetContentMetadata() throws StorageException {
        String spaceId0 = getNewSpaceId();

        final String mimeKey = StorageProvider.METADATA_CONTENT_MIMETYPE;
        final String sizeKey = StorageProvider.METADATA_CONTENT_SIZE;
        final String modifiedKey = StorageProvider.METADATA_CONTENT_MODIFIED;
        final String cksumKey = StorageProvider.METADATA_CONTENT_CHECKSUM;

        emcProvider.createSpace(spaceId0);
        byte[] data = "hello-friends".getBytes();
        ChecksumUtil chksum = new ChecksumUtil(Algorithm.MD5);
        String digest = chksum.generateChecksum(new ByteArrayInputStream(data));

        addContent(spaceId0, contentId0, mimeText, data);

        Map<String, String> metadata = emcProvider.getContentMetadata(spaceId0,
                                                                      contentId0);
        assertNotNull(metadata);
        assertTrue(metadata.containsKey(mimeKey));
        assertTrue(metadata.containsKey(sizeKey));
        assertTrue(metadata.containsKey(modifiedKey));
        assertTrue(metadata.containsKey(cksumKey));

        assertEquals(mimeText, metadata.get(mimeKey));
        assertEquals(data.length,
                     Integer.parseInt(metadata.get(sizeKey)));
        assertNotNull(metadata.get(modifiedKey));
        assertEquals(digest, metadata.get(cksumKey));

        // Set and check again.
        emcProvider.setContentMetadata(spaceId0,
                                       contentId0,
                                       new HashMap<String, String>());
        metadata = emcProvider.getContentMetadata(spaceId0, contentId0);
        assertNotNull(metadata);
        assertTrue(metadata.containsKey(mimeKey));
        assertTrue(metadata.containsKey(sizeKey));
        assertTrue(metadata.containsKey(modifiedKey));
        assertTrue(metadata.containsKey(cksumKey));

        assertEquals(mimeText, metadata.get(mimeKey));
        assertEquals(data.length, Integer.parseInt(metadata.get(sizeKey)));
        assertNotNull(metadata.get(modifiedKey));
        assertEquals(digest, metadata.get(cksumKey));

        // Set mimetype and check again.
        Map<String, String> newMeta = new HashMap<String, String>();
        newMeta.put(mimeKey, mimeXml);
        emcProvider.setContentMetadata(spaceId0,
                                       contentId0,
                                       newMeta);
        metadata = emcProvider.getContentMetadata(spaceId0, contentId0);
        assertNotNull(metadata);
        assertTrue(metadata.containsKey(mimeKey));
        assertEquals(mimeXml, metadata.get(mimeKey));
    }

    @Test
    public void testSpaceAccess() throws Exception {
        String spaceId1 = getNewSpaceId();

        emcProvider.createSpace(spaceId1);
        Identifier rootId = emcProvider.getRootId(spaceId1);

        emcProvider.setSpaceAccess(spaceId1, AccessType.OPEN);

        AccessType access = emcProvider.getSpaceAccess(spaceId1);
        assertEquals(AccessType.OPEN, access);

        // FIXME: The test below would work if metadata were available across users.
        //        List<String> spaces = createVisitorProvider().getSpaces();
        //        assertNotNull(spaces);
        //        assertTrue(spaces.contains(spaceId1));

        // FIXME: The 'createVisitor' test should be removed when the above works.
        EsuApi visitor = createVisitor();
        ObjectMetadata allMd = visitor.getAllMetadata(rootId);
        assertNotNull(allMd);

        emcProvider.setSpaceAccess(spaceId1, AccessType.CLOSED);
        access = emcProvider.getSpaceAccess(spaceId1);
        assertEquals(AccessType.CLOSED, access);

        // FIXME: The test below would work if metadata were available across users.
        //        List<String> spaces = createVisitorProvider().getSpaces();
        //        assertEquals(null, spaces);

        // FIXME: The 'createVisitor' test should be removed when the above works.
        try {
            visitor.getAllMetadata(rootId);
            fail("Exception expected.");
        } catch (Exception e) {
        }
    }

    @Test
    public void testContentAccess() throws Exception {
        String spaceId1 = getNewSpaceId();

        emcProvider.createSpace(spaceId1);
        addContent(spaceId1,
                   contentId1,
                   mimeText,
                   "testing-content".getBytes());
        Identifier objId = emcProvider.getObjectPath(spaceId1, contentId1);

        emcProvider.setSpaceAccess(spaceId1, AccessType.OPEN);

        AccessType access = emcProvider.getSpaceAccess(spaceId1);
        assertEquals(AccessType.OPEN, access);

        // FIXME: The test below would work if metadata were available across users.
        //        List<String> spaces = createVisitorProvider().getSpaces();
        //        assertNotNull(spaces);
        //        assertTrue(spaces.contains(spaceId1));

        // FIXME: The 'createVisitor' test should be removed when the above works.
        EsuApi visitor = createVisitor();
        ObjectMetadata allMd = visitor.getAllMetadata(objId);
        assertNotNull(allMd);

        emcProvider.setSpaceAccess(spaceId1, AccessType.CLOSED);
        access = emcProvider.getSpaceAccess(spaceId1);
        assertEquals(AccessType.CLOSED, access);

        // FIXME: The test below would work if metadata were available across users.
        //        List<String> spaces = createVisitorProvider().getSpaces();
        //        assertEquals(null, spaces);

        // FIXME: The 'createVisitor' test should be removed when the above works.
        try {
            visitor.getAllMetadata(objId);
            fail("Exception expected.");
        } catch (Exception e) {
        }
    }

    private EsuApi createVisitor() throws Exception {
        Credential visitorCredential = getCredential(StorageProviderType.EMC_SECONDARY);

        String username = visitorCredential.getUsername();
        String password = visitorCredential.getPassword();
        assertNotNull(username);
        assertNotNull(password);
        return new EsuRestApi(ESU_HOST, ESU_PORT, username, password);
    }

    @Test
    public void testNotFound() {
        String spaceId = "NonExistantSpace";
        String contentId = "NonExistantContent";
        String failMsg = "Should throw NotFoundException attempting to " +
                         "access a space which does not exist";
        byte[] content = "test-content".getBytes();

        // Space Not Found

        try {
            emcProvider.getSpaceMetadata(spaceId);
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }

        try {
            emcProvider.setSpaceMetadata(spaceId,
                                         new HashMap<String, String>());
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }

        try {
            emcProvider.getSpaceContents(spaceId, null);
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }

        try {
            emcProvider.getSpaceContentsChunked(spaceId, null, 100, null);
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }

        try {
            emcProvider.getSpaceAccess(spaceId);
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }

        try {
            emcProvider.setSpaceAccess(spaceId, AccessType.CLOSED);
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }

        try {
            emcProvider.deleteSpace(spaceId);
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }

        try {
            int contentSize = content.length;
            ByteArrayInputStream contentStream =
                new ByteArrayInputStream(content);
            emcProvider.addContent(spaceId,
                                   contentId,
                                   mimeText,
                                   contentSize,
                                   contentStream);
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }

        try {
            emcProvider.getContent(spaceId, contentId);
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }

        try {
            emcProvider.getContentMetadata(spaceId, contentId);
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }

        try {
            emcProvider.setContentMetadata(spaceId,
                                           contentId,
                                           new HashMap<String, String>());
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }

        try {
            emcProvider.deleteContent(spaceId, contentId);
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }

        // Content Not Found

        spaceId = getNewSpaceId();
        emcProvider.createSpace(spaceId);
        failMsg = "Should throw NotFoundException attempting to " +
            "access content which does not exist";

        try {
            emcProvider.getContent(spaceId, contentId);
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }

        try {
            emcProvider.getContentMetadata(spaceId, contentId);
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }

        try {
            emcProvider.setContentMetadata(spaceId,
                                           contentId,
                                           new HashMap<String, String>());
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }

        try {
            emcProvider.deleteContent(spaceId, contentId);
            Assert.fail(failMsg);
        } catch (NotFoundException expected) {
            assertNotNull(expected);
        }
    }

}
