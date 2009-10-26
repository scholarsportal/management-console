package org.duracloud.duradav.store;

import org.duracloud.duradav.core.Collection;
import org.duracloud.duradav.core.CollectionPath;
import org.duracloud.duradav.core.Content;
import org.duracloud.duradav.core.ContentPath;
import org.duracloud.duradav.error.WebdavException;

/**
 * Adapts a particular storage implementation to be used with DuraDAV.
 */
public interface WebdavStore {

    boolean putContent(Content content);

    void createCollection(Collection collection) throws WebdavException;

    Content getContent(ContentPath path) throws WebdavException;

    Collection getCollection(CollectionPath path) throws WebdavException;

    boolean hasCollection(CollectionPath path);

    void deleteContent(ContentPath path) throws WebdavException;

    void deleteCollection(CollectionPath path) throws WebdavException;

}
