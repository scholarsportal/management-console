package org.duracloud.client;

import java.util.Map;

/**
 * Provides facilities for connecting to a set of content stores
 *
 * @author Bill Branan
 */
public interface ContentStoreManager {


    public Map<String, ContentStore> getContentStores() throws ContentStoreException;
    
    public ContentStore getContentStore(String storeID) throws ContentStoreException;
    
    public ContentStore getPrimaryContentStore() throws ContentStoreException;
}
