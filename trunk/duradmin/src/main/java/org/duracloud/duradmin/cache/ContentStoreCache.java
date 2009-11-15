package org.duracloud.duradmin.cache;

import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.domain.Content;
import org.duracloud.domain.Space;


public class ContentStoreCache implements ContentStore{

    private ContentStore backendStore;
    
    private Set<String> spaceIdCache;
    
    public void setContentStore(ContentStore contentStore) {
        this.backendStore = contentStore;
    }

    public ContentStore getContentStore() {
        return this.backendStore;
    }

   public String getBaseURL(){
       return this.backendStore.getBaseURL();
   }
    
    public String getStoreId(){
        return this.backendStore.getStoreId();
    }

    public String getStorageProviderType(){
        return this.backendStore.getStorageProviderType();
    }

    /**
     * Provides a listing of all spaces. Spaces in the list include metadata but
     * not a listing of content.
     *
     * @return Iterator listing spaceIds
     * @throws ContentStoreException
     */
    public List<String> getSpaces() throws ContentStoreException{
        if(spaceIdCache == null){
            spaceIdCache = new HashSet<String>();
            List<String> spaces = this.backendStore.getSpaces();
            for(String space : spaces){
                spaceIdCache.add(space);
            }
        }
        
        return new LinkedList<String>(spaceIdCache);
    }

    /**
     * Provides a Space, including a listing of all of the content files within
     * a space and the metadata associated with the space.
     *
     * @return Space
     * @throws ContentStoreException
     */
    public Space getSpace(String spaceId) throws ContentStoreException{
        return this.backendStore.getSpace(spaceId);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public Space getSpace(String spaceId, String prefix, String marker, Integer maxResults)
            throws ContentStoreException {
        return this.backendStore.getSpace(spaceId, prefix, marker, maxResults);
    }
    
    /**
     * Creates a new space. Depending on the storage implementation, the spaceId
     * may be changed somewhat to comply with the naming rules of the underlying
     * storage provider. The same spaceId value used here can be used in all
     * other methods, as the conversion will be applied internally, however a
     * call to getSpaces() may not include a space with exactly this same name.
     *
     * @param spaceId
     * @throws ContentStoreException
     */
    public void createSpace(String spaceId, Map<String, String> spaceMetadata)
            throws ContentStoreException{
                //TODO implement
    }
          
    /**
     * Deletes a space.
     *
     * @param spaceId
     * @throws ContentStoreException
     */
    public void deleteSpace(String spaceId) throws ContentStoreException{
        //TODO implement
    }
  
    
    /**
     * Retrieves the metadata associated with a space.
     *
     * @param spaceId
     * @return Map of space metadata or null if no metadata exists
     * @throws ContentStoreException
     */
    public Map<String, String> getSpaceMetadata(String spaceId)
            throws ContentStoreException{
                //TODO implement
        return null;
       }
          
    
    /**
     * Sets the metadata associated with a space. Only values included
     * in the  metadata map will be updated, others will remain unchanged.
     *
     * @param spaceId
     * @param spaceMetadata
     * @throws ContentStoreException
     */
    public void setSpaceMetadata(String spaceId,
                                 Map<String, String> spaceMetadata)
            throws ContentStoreException{
                //TODO implement
            }
          
    
    /**
     * Gets the access setting of the space, either OPEN or CLOSED. An OPEN
     * space is available for public viewing. A CLOSED space requires
     * authentication prior to viewing any of the contents.
     *
     * @param spaceId
     * @return
     * @throws ContentStoreException
     */
    public AccessType getSpaceAccess(String spaceId) throws ContentStoreException{
        //TODO implement
        return null;
    }
  
    
    /**
     * Sets the accessibility of a space to either OPEN or CLOSED.
     *
     * @param spaceId
     * @param access
     * @throws ContentStoreException
     */
    public void setSpaceAccess(String spaceId, AccessType spaceAccess)
            throws ContentStoreException{
                //TODO implement
            }
          
    
    /**
     * Adds content to a space.
     * Returns the checksum of the content as computed by the
     * underlying storage provider to facilitate comparison
     *
     * @param spaceId
     * @param contentId
     * @param content
     * @param contentMimeType
     * @param contentSize
     * @param contentMetadata
     * @return
     * @throws ContentStoreException
     */
    public String addContent(String spaceId,
                             String contentId,
                             InputStream content,
                             long contentSize,
                             String contentMimeType,
                             Map<String, String> contentMetadata)
            throws ContentStoreException{
                //TODO implement
        return null;
        
     }
          
    /**
     * Gets content from a space.
     *
     * @param spaceId
     * @param contentId
     * @return the content stream or null if the content does not exist
     * @throws ContentStoreException
     */
    public Content getContent(String spaceId, String contentId)
            throws ContentStoreException{
                //TODO implement
        return null;
     }
    

    /**
     * Removes content from a space.
     *
     * @param spaceId
     * @param contentId
     * @throws ContentStoreException
     */
    public void deleteContent(String spaceId, String contentId)
            throws ContentStoreException{
        //TODO implement
    }

    /**
     * Sets the metadata associated with content. This effectively removes all
     * of the current content metadata and adds a new set of metadata. Some
     * metadata, such as system metadata provided by the underlying storage
     * system, cannot be updated or removed. Some of the values which cannot be
     * updated or removed: content-checksum content-modified content-size
     *
     * @param spaceId
     * @param contentId
     * @param contentMetadata
     * @throws ContentStoreException
     */
    public void setContentMetadata(String spaceId,
                                   String contentId,
                                   Map<String, String> contentMetadata)
            throws ContentStoreException{
        //TODO implement 
    }
    

    /**
     * Retrieves the metadata associated with content. This includes both
     * metadata generated by the underlying storage system as well as
     *
     * @param spaceId
     * @param contentId
     * @return
     * @throws ContentStoreException
     */
    public Map<String, String> getContentMetadata(String spaceId,
                                                  String contentId)
            throws ContentStoreException{
        return null;
    }
}
