package org.duracloud.chunk;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

/**
 * @author Andrew Woods
 *         Date: Feb 7, 2010
 */
public class ChunksManifestTest {

    private ChunksManifest manifest;
    private String parentContentId = "parentContentId";
    private String parentMimetype = "parentMimetype";

    @Before
    public void setUp() {
        manifest = new ChunksManifest(parentContentId, parentMimetype);
    }

    @After
    public void tearDown() {
        manifest = null;
    }

    @Test
    public void testNextChunkId() {
        String chunkId;
        String prefix = parentContentId + ".dura-chunk-";
        for (int i = 0; i < 10000; ++i) {
            chunkId = manifest.nextChunkId();
            Assert.assertNotNull(chunkId);
            Assert.assertEquals(prefix + getChunkIndex(i), chunkId);
        }

        boolean thrown = false;
        try {
            manifest.nextChunkId();
            Assert.fail("Exception expected");
        } catch (Exception e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    private String getChunkIndex(int i) {
        StringBuilder sb = new StringBuilder(Integer.toString(i));
        while (sb.length() < 4) {
            sb.insert(0, "0");
        }
        String num = sb.toString();
        return num;
    }

    @Test
    public void testGetBody() {
        String prefix = parentContentId + ".dura-chunk-";
        String chunkId;
        for (int i = 0; i < 11; ++i) {
            chunkId = prefix + getChunkIndex(i);
            manifest.addEntry(chunkId, "md5" + i);
        }

        InputStream body = manifest.getBody();
        Assert.assertNotNull(body);

        //todo: de-serialize xml, check fields 

    }
}
