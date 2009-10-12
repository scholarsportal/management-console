
package org.duracloud.duradmin.mock.contentstore;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.domain.Content;
import org.duracloud.domain.Space;
import org.duracloud.duradmin.contentstore.ContentStoreManagerFactory;

public class MockContentStoreManagerFactoryImpl
        implements ContentStoreManagerFactory {

    private ContentStoreManager contentStoreManager;

    public ContentStoreManager create() throws Exception {
        if(this.contentStoreManager == null){
            this.contentStoreManager = new MockContentStoreManager();
        }
        return this.contentStoreManager;
    }

    private class MockContentStoreManager
            implements ContentStoreManager {
        private ContentStore primaryContentStore;
        private Map<String,ContentStore> contentStores = new HashMap<String,ContentStore>();
        public MockContentStoreManager(){

            for(int i = 0; i < 3; i++){
                String storeId ="Mock Store #"+i;
                this.contentStores.put(storeId, new MockContentStore(storeId));
            }
            
            this.primaryContentStore = this.contentStores.get(this.contentStores.keySet().iterator().next());
        }
        public ContentStore getContentStore(String storeID)
                throws ContentStoreException {
            return this.contentStores.get(storeID);
        }

        
        public Map<String, ContentStore> getContentStores()
                throws ContentStoreException {
            // TODO Auto-generated method stub
            return this.contentStores;
        }

        
        public ContentStore getPrimaryContentStore()
                throws ContentStoreException {
            return this.primaryContentStore;
        }

    }

    private class MockContentStore
            implements ContentStore {

        Map<String, Space> spaceMap = new HashMap<String, Space>();
        private String storeId;
        private String storageProviderType;
        
        MockContentStore(String storeId) {
            this.storeId = storeId;
            this.storageProviderType = "Mock Storage Provider [" + storeId + "]";
            
            for (int i = 0; i < 10; i++) {
                Space s = new Space();
                s.setId("space-number-" + i);
                Map<String, String> metadata = new HashMap<String, String>();
                metadata.put(ContentStore.SPACE_COUNT, String.valueOf(i % 4));
                metadata.put(ContentStore.SPACE_CREATED, new Date().toString());
                metadata.put(ContentStore.SPACE_ACCESS, AccessType.OPEN.name());
                s.setMetadata(metadata);
                
                List<String> contentIds = new ArrayList<String>();
                for (int j = 0; j < 10; j++) {
                    contentIds.add("Item-" + j);
                }
                
                s.setContentIds(contentIds);

                spaceMap.put(s.getId(), s);
            }

        }

        
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

        
        public void createSpace(String spaceId,
                                Map<String, String> spaceMetadata)
                throws ContentStoreException {
            
               Space space = new Space();
               space.setId(spaceId);
               if(spaceMetadata == null){
                   spaceMetadata = new HashMap<String,String>();
                   spaceMetadata.put(ContentStore.SPACE_COUNT, String.valueOf(0));
                   spaceMetadata.put(ContentStore.SPACE_CREATED, new Date().toString());
                   spaceMetadata.put(ContentStore.SPACE_ACCESS, AccessType.OPEN.name());
               }

               space.setMetadata(spaceMetadata);
               spaceMap.put(spaceId, space);
        }

        
        public void deleteContent(String spaceId, String contentId)
                throws ContentStoreException {

        }

        
        public void deleteSpace(String spaceId) throws ContentStoreException {
               spaceMap.remove(spaceId);
        }

        
        public String getBaseURL() {
            // TODO Auto-generated method stub
            return null;
        }

        
        public Content getContent(String spaceId, String contentId)
                throws ContentStoreException {
            // TODO Auto-generated method stub
            return null;
        }

        
        public Map<String, String> getContentMetadata(String spaceId,
                                                      String contentId)
                throws ContentStoreException {
            Map<String, String> metadata = new HashMap<String,String>();
            metadata.put(ContentStore.CONTENT_CHECKSUM, new String(Hex.encodeHex(String.valueOf(spaceId+contentId).getBytes())));
            metadata.put(ContentStore.CONTENT_MIMETYPE, "image/jpeg");
            metadata.put(ContentStore.CONTENT_SIZE, String.valueOf(100000));
            metadata.put(ContentStore.CONTENT_MODIFIED, new Date().toString());
            return metadata;
        }
        

        
        public Space getSpace(String spaceId) throws ContentStoreException {
            return spaceMap.get(spaceId);
        }

        
        public AccessType getSpaceAccess(String spaceId)
                throws ContentStoreException {
            String access = spaceMap.get(spaceId).getMetadata().get(ContentStore.SPACE_ACCESS);
            return AccessType.valueOf(access);
        }

        
        public Map<String, String> getSpaceMetadata(String spaceId)
                throws ContentStoreException {
            // TODO Auto-generated method stub
            return null;
        }

        
        public List<Space> getSpaces() throws ContentStoreException {
            return new ArrayList<Space>(spaceMap.values());
        }

        
        public String getStorageProviderType() {
            return this.storageProviderType;
        }

        
        public String getStoreId() {
            return this.storeId;
        }

        
        public void setContentMetadata(String spaceId,
                                       String contentId,
                                       Map<String, String> contentMetadata)
                throws ContentStoreException {
            // TODO Auto-generated method stub

        }

        
        public void setSpaceAccess(String spaceId, AccessType spaceAccess)
                throws ContentStoreException {
            // TODO Auto-generated method stub

        }

        
        public void setSpaceMetadata(String spaceId,
                                     Map<String, String> spaceMetadata)
                throws ContentStoreException {
            // TODO Auto-generated method stub

        }

    }

}
