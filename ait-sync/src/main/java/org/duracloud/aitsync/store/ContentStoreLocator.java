package org.duracloud.aitsync.store;

import org.duracloud.aitsync.domain.Mapping;
import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;

/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public interface ContentStoreLocator {
    public ContentStore findContentStore(Mapping mapping)
        throws ContentStoreException;
}
