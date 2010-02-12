
package org.duracloud.duradmin.contentstore;

import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.DataRetrievalException;
import org.duracloud.duradmin.util.ScrollableList;
import org.duracloud.duradmin.util.SpaceUtil;
import org.duracloud.error.ContentStoreException;

import java.util.ArrayList;
import java.util.List;

public class ContentItemList
        extends ScrollableList<String> {

    private ContentStore contentStore;

    private String spaceId;

    private Space space;

    private String contentIdFilterString = null;

    public ContentItemList(String spaceId,
                           ContentStore contentStore) {
        if ( contentStore == null) {
            throw new NullPointerException("content store must be non-null");
        }

        if (spaceId == null) {
            throw new NullPointerException("spaceId must be non-null");
        }

        this.contentStore = contentStore;
        this.spaceId = spaceId;
    }

    public Space getSpace() {
        try {
            update();
            return this.space;
        } catch (DataRetrievalException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<String> getData(String currentMarker) throws DataRetrievalException {
        try {
            this.space = new Space();
            org.duracloud.domain.Space cloudSpace =
                    contentStore.getSpace(spaceId,
                                          contentIdFilterString,
                                          getMaxResultsPerPage(),
                                          currentMarker);

            SpaceUtil.populateSpace(space, cloudSpace);

            return space.getContents();
        } catch (ContentStoreException e) {
            throw new DataRetrievalException(e);
        }
    }

    public String getContentIdFilterString() {
        return contentIdFilterString;
    }

    public List<ContentItem> getContentItemList() {
        List<String> resultList = super.getResultList();
        List<ContentItem> contentItemList = new ArrayList<ContentItem>();
        for(String result : resultList) {
            ContentItem contentItem = new ContentItem();
            contentItem.setSpaceId(spaceId);
            contentItem.setContentId(result);
            contentItemList.add(contentItem);
        }
        return contentItemList;
    }

}