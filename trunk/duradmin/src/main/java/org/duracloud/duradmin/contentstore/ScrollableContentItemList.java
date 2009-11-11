package org.duracloud.duradmin.contentstore;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.domain.Space;
import org.duracloud.duradmin.util.DataRetrievalException;
import org.duracloud.duradmin.util.ScrollableList;


public class ScrollableContentItemList extends ScrollableList<String>{
    
    private ContentStoreProvider contentStoreProvider;
    
    private String spaceId; 
    
    private String contentIdFilterString = null;
    
    @Override
    protected void updateList() throws DataRetrievalException {
        try {
            ContentStore contentStore = contentStoreProvider.getContentStore();
            Space space = contentStore.getSpace(spaceId, contentIdFilterString, getFirstResultIndex(), getMaxResultsPerPage());
            update(Long.valueOf(space.getMetadata().get(ContentStore.SPACE_QUERY_COUNT)), space.getContentIds());
        } catch (ContentStoreException e) {
            throw new DataRetrievalException(e);
        }
    }

    public String getContentIdFilterString() {
        return contentIdFilterString;
    }





    
}
