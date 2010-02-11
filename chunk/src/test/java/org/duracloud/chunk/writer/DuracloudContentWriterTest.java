package org.duracloud.chunk.writer;

import org.duracloud.chunk.ChunkableContent;
import org.duracloud.chunk.error.NotFoundException;
import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author Andrew Woods
 *         Date: Feb 7, 2010
 */
public class DuracloudContentWriterTest {

    private DuracloudContentWriter writer;
    ContentStore contentStore;

    @Before
    public void setUp() throws ContentStoreException {
        writer = new DuracloudContentWriter(createMockContentStore());
    }

    private ContentStore createMockContentStore() throws ContentStoreException {
        contentStore = EasyMock.createMock(ContentStore.class);
        EasyMock.expect(contentStore.addContent(EasyMock.isA(String.class),
                                                EasyMock.isA(String.class),
                                                isChunkInputStream(),
                                                EasyMock.anyLong(),
                                                EasyMock.isA(String.class),
                                                (Map) EasyMock.anyObject()))
            .andReturn("")
            .times(5);

        contentStore.createSpace(EasyMock.isA(String.class),
                                 (Map) EasyMock.anyObject());
        EasyMock.expectLastCall();

        EasyMock.expect(contentStore.getSpaceAccess(EasyMock.isA(String.class)))
            .andReturn(ContentStore.AccessType.OPEN);
        EasyMock.replay(contentStore);
        return contentStore;
    }

    @After
    public void tearDown() {
        EasyMock.verify(contentStore);
        contentStore = null;
        writer = null;
    }

    @Test
    public void testWrite() throws NotFoundException {
        long contentSize = 4000;
        InputStream contentStream = createContentStream(contentSize);

        String spaceId = "test-spaceId";
        String contentId = "test-contentId";

        long maxChunkSize = 1024;
        ChunkableContent chunkable = new ChunkableContent(contentId,
                                                          contentStream,
                                                          contentSize,
                                                          maxChunkSize);
        writer.write(spaceId, chunkable);

    }

    private InputStream createContentStream(long size) {
        Assert.assertTrue("let's keep it reasonable", size < 10001);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (long i = 0; i < size; ++i) {
            if (i % 101 == 0) {
                out.write('\n');
            } else {
                out.write('a');
            }
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * This class is an EasyMock helper.
     */
    public static class ChunkInputsStreamMatcher implements IArgumentMatcher {

        public boolean matches(Object o) {
            if (null == o || !(o instanceof InputStream)) {
                return false;
            } else {
                InputStream is = (InputStream) o;
                try {
                    while (is.read() != -1) {
                        // spin through content;
                    }
                } catch (IOException e) {
                    // do nothing
                }
            }
            return true;
        }

        public void appendTo(StringBuffer stringBuffer) {
            stringBuffer.append(ChunkInputsStreamMatcher.class.getCanonicalName());
        }
    }

    /**
     * This method registers the EasyMock helper.
     *
     * @return
     */
    public static InputStream isChunkInputStream() {
        EasyMock.reportMatcher(new ChunkInputsStreamMatcher());
        return null;
    }

}
