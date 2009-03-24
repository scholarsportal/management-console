package org.duraspace.duradav.store;

import org.duraspace.duradav.core.Collection;
import org.duraspace.duradav.core.CollectionPath;
import org.duraspace.duradav.core.Content;
import org.duraspace.duradav.core.ContentPath;
import org.duraspace.duradav.error.WebdavException;

/**
 * Adapts a particular storage implementation to be used with DuraDAV.
 */
public interface WebdavStore {

    Content getContent(ContentPath path) throws WebdavException;

    Collection getCollection(CollectionPath path) throws WebdavException;

    boolean hasCollection(CollectionPath path);

    boolean putContent(Content content);

    void deleteContent(ContentPath path) throws WebdavException;

    void deleteCollection(CollectionPath path) throws WebdavException;

}
