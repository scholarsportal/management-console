
package org.duracloud.duradmin.mock.contentstore;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.domain.Content;
import org.duracloud.domain.Space;
import org.duracloud.duradmin.contentstore.ContentStoreManagerFactory;

public class MockContentStoreManagerFactoryImpl
        implements ContentStoreManagerFactory {

    @Override
    public ContentStoreManager create() throws Exception {
        return new MockContentStoreManager();
    }

    private class MockContentStoreManager
            implements ContentStoreManager {

        @Override
        public ContentStore getContentStore(String storeID)
                throws ContentStoreException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Map<String, ContentStore> getContentStores()
                throws ContentStoreException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ContentStore getPrimaryContentStore()
                throws ContentStoreException {
            // TODO Auto-generated method stub
            return new MockContentStore();
        }

    }

    private class MockContentStore
            implements ContentStore {

        Map<String, Space> spaceMap = new HashMap<String, Space>();

        MockContentStore() {
            for (int i = 0; i < 10; i++) {
                Space s = new Space();
                s.setId("Space Number #" + i);
                Map<String, String> metadata = new HashMap<String, String>();
                metadata.put(ContentStore.SPACE_COUNT, String.valueOf(i % 4));
                metadata.put(ContentStore.SPACE_CREATED, new Date().toString());
                metadata.put(ContentStore.SPACE_ACCESS, AccessType.OPEN.name());
                s.setMetadata(metadata);
                spaceMap.put(s.getId(), s);
            }

        }

        @Override
        public String addContent(String spaceId,
                                 String contentId,
                                 InputStream content,
                                 long contentSize,
                                 String contentMimeType,
                                 Map<String, String> contentMetadata)
                throws ContentStoreException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void createSpace(String spaceId,
                                Map<String, String> spaceMetadata)
                throws ContentStoreException {
            // TODO Auto-generated method stub

        }

        @Override
        public void deleteContent(String spaceId, String contentId)
                throws ContentStoreException {
            // TODO Auto-generated method stub

        }

        @Override
        public void deleteSpace(String spaceId) throws ContentStoreException {
            // TODO Auto-generated method stub

        }

        @Override
        public String getBaseURL() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Content getContent(String spaceId, String contentId)
                throws ContentStoreException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Map<String, String> getContentMetadata(String spaceId,
                                                      String contentId)
                throws ContentStoreException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Space getSpace(String spaceId) throws ContentStoreException {
            return spaceMap.get(spaceId);
        }

        @Override
        public AccessType getSpaceAccess(String spaceId)
                throws ContentStoreException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Map<String, String> getSpaceMetadata(String spaceId)
                throws ContentStoreException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<Space> getSpaces() throws ContentStoreException {
            return new ArrayList<Space>(spaceMap.values());
        }

        @Override
        public String getStorageProviderType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getStoreId() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setContentMetadata(String spaceId,
                                       String contentId,
                                       Map<String, String> contentMetadata)
                throws ContentStoreException {
            // TODO Auto-generated method stub

        }

        @Override
        public void setSpaceAccess(String spaceId, AccessType spaceAccess)
                throws ContentStoreException {
            // TODO Auto-generated method stub

        }

        @Override
        public void setSpaceMetadata(String spaceId,
                                     Map<String, String> spaceMetadata)
                throws ContentStoreException {
            // TODO Auto-generated method stub

        }

    }

}
