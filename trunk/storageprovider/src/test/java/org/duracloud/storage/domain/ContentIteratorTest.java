package org.duracloud.storage.domain;

import static junit.framework.Assert.assertNotNull;
import org.duracloud.storage.error.StorageException;
import org.duracloud.storage.provider.StorageProvider;
import org.duracloud.storage.provider.mock.MockStorageProvider;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Bill Branan
 * Date: Dec 22, 2009
 */
public class ContentIteratorTest {

    @Test
    public void testIterator() throws Exception {
        for(int i=0; i<30; i++) {
            StorageProvider testProvider = new MockProvider(i);
            ContentIterator iterator =
                new ContentIterator(testProvider, "spaceId", "prefix", 10);
            while(iterator.hasNext()) {
                assertNotNull(iterator.next());
            }
        }
    }

    private class MockProvider extends MockStorageProvider {

        private long contentItems;

        public MockProvider(long contentItems) {
            this.contentItems = contentItems;
        }

        @Override
        public List<String> getSpaceContentsChunked(String spaceId,
                                                    String prefix,
                                                    long maxResults,
                                                    String marker)
            throws StorageException {

            long listSize;
            if(contentItems > maxResults) {
                listSize = maxResults;
                contentItems -= maxResults;
            } else if(contentItems == maxResults) {
                listSize = 0;
            } else {
                listSize = contentItems;
            }

            List<String> contentList = new ArrayList<String>();
            for(long i=0; i < listSize; i++) {
                contentList.add("test" + i);
            }
            return contentList;
        }
    }


}
