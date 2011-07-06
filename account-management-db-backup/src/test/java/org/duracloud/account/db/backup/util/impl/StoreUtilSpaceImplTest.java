/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.backup.util.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.duracloud.client.ContentStore;
import org.duracloud.domain.Content;
import org.duracloud.error.ContentStoreException;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Woods
 *         Date: 6/30/11
 */
public class StoreUtilSpaceImplTest {

    private StoreUtilSpaceImpl spaceUtil;

    private ContentStore contentStore;
    private String spaceId = "space-id";

    private File testDir = new File("target", "unit-test-spaceutil");

    @Before
    public void setUp() throws Exception {
        if (!testDir.exists()) {
            Assert.assertTrue("must exist: " + testDir.getAbsolutePath(),
                              testDir.mkdir());
        }

        contentStore = EasyMock.createMock("ContentStore", ContentStore.class);

        spaceUtil = new StoreUtilSpaceImpl(contentStore, spaceId);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(contentStore);
    }

    private void replayMocks() {
        EasyMock.replay(contentStore);
    }

    @Test
    public void testGetMostRecentContentId() throws Exception {
        String id = createMocksGetMostRecentContentId();
        replayMocks();

        String pattern = "^\\d{4}-\\d{2}$";
        String contentId = spaceUtil.getMostRecentContentId(pattern);

        Assert.assertNotNull(contentId);
        Assert.assertEquals(id, contentId);

    }

    private String createMocksGetMostRecentContentId()
        throws ContentStoreException {
        String mostRecentId = "1239-56";
        List<String> items = new ArrayList<String>();
        items.add("hello");
        items.add("1234-56");
        items.add("junk");
        items.add(mostRecentId);
        items.add("junk");
        items.add("0234-56");
        items.add("goodbye");

        EasyMock.expect(contentStore.getSpaceContents(spaceId))
            .andReturn(items.iterator());

        return mostRecentId;
    }

    @Test
    public void testDownloadContentToDirectory() throws Exception {
        String contentId = "content-id";
        createMocksDownloadContentToDirectory(contentId);
        replayMocks();

        File dir = new File(testDir, "download");
        spaceUtil.downloadContentToDirectory(contentId, dir);
    }

    private void createMocksDownloadContentToDirectory(String contentId)
        throws ContentStoreException {
        Content content = EasyMock.createMock("Content", Content.class);

        InputStream stream = new AutoCloseInputStream(new ByteArrayInputStream(
            "hello".getBytes()));
        EasyMock.expect(content.getStream()).andReturn(stream);
        EasyMock.replay(content);


        EasyMock.expect(contentStore.getContent(spaceId, contentId)).andReturn(
            content);

    }

    @Test
    public void testUploadFile() throws Exception {
        String contentId = "content-id";
        File file = new File(testDir, "upload-content.txt");
        FileUtils.writeStringToFile(file, "test content");

        createMocksUploadFile(contentId, file);
        replayMocks();

        spaceUtil.uploadFile(contentId, file);

    }

    private void createMocksUploadFile(String contentId, File file)
        throws FileNotFoundException, ContentStoreException {
        EasyMock.expect(contentStore.addContent(EasyMock.eq(spaceId),
                                                EasyMock.eq(contentId),
                                                EasyMock.<InputStream>anyObject(),
                                                EasyMock.eq(file.length()),
                                                EasyMock.<String>isNull(),
                                                EasyMock.<String>isNull(),
                                                EasyMock.<Map<String, String>>isNull()))
            .andReturn(null);
    }

}
