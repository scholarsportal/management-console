package org.duracloud.duradmin.contentstore;

import java.util.List;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.DataRetrievalException;
import org.duracloud.duradmin.util.ScrollableList;
import org.duracloud.duradmin.util.SpaceUtil;


public class ContentItemList extends ScrollableList<String>{
    
    private ContentStoreProvider contentStoreProvider;
    
    private String spaceId; 
    
    private Space space;
    
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

    public Space getSpace(){
        try {
            update();
            return this.space;
        } catch (DataRetrievalException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected List<String> getData() throws DataRetrievalException {
        try {
            ContentStore contentStore = this.contentStoreProvider.getContentStore();
            space = new Space();
            org.duracloud.domain.Space cloudSpace = 
                    contentStore.getSpace(
                                          spaceId, 
                                          contentIdFilterString, 
                                          getCurrentMarker(), 
                                          getMaxResultsPerPage());

            SpaceUtil.populateSpace(space, cloudSpace);
            
            return space.getContents();
        } catch (ContentStoreException e) {
            throw new DataRetrievalException(e);
        }
    }
    
    public String getContentIdFilterString() {
        return contentIdFilterString;
    }
}
