package org.duracloud.client;

import org.duracloud.common.model.Credential;
import org.duracloud.error.ContentStoreException;

import java.util.Map;

/**
 * Provides facilities for connecting to a set of content stores
 *
 * @author Bill Branan
 */
public interface ContentStoreManager {

    /**
     * <p>getContentStores</p>
     *
     * @return a map of content stores to content store IDs
     * @throws ContentStoreException if the content store list cannot be retrieved
     */
    public Map<String, ContentStore> getContentStores() throws ContentStoreException;
    
    /**
     * <p>getContentStore</p>
     *
     * @param storeID the ID of a particular content store
     * @return the ContentStore mapped to storeID
     * @throws ContentStoreException if the content store cannot be retrieved
     */
    public ContentStore getContentStore(String storeID) throws ContentStoreException;
    
    /**
     * <p>getPrimaryContentStore</p>
     *
     * @return the primary ContentStore
     * @throws if the content store cannot be retrieved
     */
    public ContentStore getPrimaryContentStore() throws ContentStoreException;

    /**
     * This method supplies user credentials to the application to access
     * durastore.
     * @param appCred of user
     */
    public void login(Credential appCred);

    /**
     * This method clears any previously logged-in credentials.
     */
    public void logout();
}
