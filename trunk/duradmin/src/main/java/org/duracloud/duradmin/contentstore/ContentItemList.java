package org.duracloud.duradmin.contentstore;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.domain.Space;
import org.duracloud.duradmin.util.DataRetrievalException;
import org.duracloud.duradmin.util.ScrollableList;


public class ContentItemList extends ScrollableList<String>{
    
    private ContentStoreProvider contentStoreProvider;
    
    private String spaceId; 
    
    private String contentIdFilterString = null;
    
    public ContentItemList(String spaceId, ContentStoreProvider contentStoreProvider){
        if(contentStoreProvider == null){
            throw new NullPointerException("contentStoreProvider must be non-null");
        }

        if(spaceId == null){
            throw new NullPointerException("spaceId must be non-null");
        }

        this.contentStoreProvider = contentStoreProvider;
        this.spaceId = spaceId;
    }
    
    @Override
    protected void updateList() throws DataRetrievalException {
        try {
            ContentStore contentStore = this.contentStoreProvider.getContentStore();
            Space space = contentStore.getSpace(spaceId, contentIdFilterString, getFirstResultIndex()==-1?0:getFirstResultIndex(), getMaxResultsPerPage());
            update(Long.valueOf(space.getMetadata().get(ContentStore.SPACE_QUERY_COUNT)), space.getContentIds());
        } catch (ContentStoreException e) {
            throw new DataRetrievalException(e);
        }
    }

    public String getContentIdFilterString() {
        return contentIdFilterString;
    }





    
}
