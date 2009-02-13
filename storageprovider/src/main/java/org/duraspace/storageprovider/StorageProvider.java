
package org.duraspace.storageprovider;

import java.io.InputStream;

import java.util.List;
import java.util.Properties;

/**
 * A Storage Provider provides services which allow content to be
 * stored in and retrieved from spaces.
 *
 * @author Bill Branan
 */
public interface StorageProvider {

    public enum AccessType {OPEN, CLOSED};

    /**
     * Provides a listing of all spaces owned by a customer.
     *
     * @param customerId
     * @return List of spaceIds
     * @throws Exception
     */
    public List<String> getSpaces(String customerId)
    throws Exception;

    /**
     * Provides a listing of all of the content files within a space.
     *
     * @param customerId
     * @return List of contentIds
     * @throws Exception
     */
    public List<String> getSpaceContents(String customerId,
                                         String spaceId)
    throws Exception;

    /**
     * Creates a new space.
     *
     * @param customerId
     * @param spaceId
     * @throws Exception
     */
    public void createSpace(String customerId,
                            String spaceId)
    throws Exception;

    /**
     * Deletes a space.
     *
     * @param customerId
     * @param spaceId
     * @throws Exception
     */
    public void deleteSpace(String customerId,
                            String spaceId)
    throws Exception;

    /**
     * Retrieves the metadata associated with a space.
     *
     * @param customerId
     * @param spaceId
     * @return Properties list of space metadata
     * @throws Exception
     */
    public Properties getSpaceMetadata(String customerId,
                                       String spaceId)
    throws Exception;

    /**
     * Sets the metadata associated with a space.
     *
     * @param customerId
     * @param spaceId
     * @param spaceMetadata
     * @throws Exception
     */
    public void setSpaceMetadata(String customerId,
                                 String spaceId,
                                 Properties spaceMetadata)
    throws Exception;

    /**
     * Gets the access setting of the space, either OPEN or CLOSED. An OPEN space is
     * available for public viewing. A CLOSED space requires authentication prior to
     * viewing any of the contents.
     *
     * @param customerId
     * @param spaceId
     * @return
     * @throws Exception
     */
    public AccessType getSpaceAccess(String customerId,
                                     String spaceId)
    throws Exception;

    /**
     * Sets the accessibility of a space to either OPEN or CLOSED.
     *
     * @param customerId
     * @param spaceId
     * @param access
     * @throws Exception
     */
    public void setSpaceAccess(String customerId,
                               String spaceId,
                               AccessType access)
    throws Exception;

    /**
     * Adds content to a space.
     *
     * @param customerId
     * @param spaceId
     * @param contentId
     * @param content
     * @throws Exception
     */
    public void addContent(String customerId,
                           String spaceId,
                           String contentId,
                           InputStream content)
    throws Exception;

    /**
     * Gets content from a space.
     *
     * @param customerId
     * @param spaceId
     * @param contentId
     * @return
     * @throws Exception
     */
    public InputStream getContent(String customerId,
                                  String spaceId,
                                  String contentId)
    throws Exception;

    /**
     * Removes content from a space.
     *
     * @param customerId
     * @param spaceId
     * @param contentId
     * @throws Exception
     */
    public void deleteContent(String customerId,
                              String spaceId,
                              String contentId)
    throws Exception;

    /**
     * Sets the metadata associated with content.
     *
     * @param customerId
     * @param spaceId
     * @param contentId
     * @param contentMetadata
     * @throws Exception
     */
    public void setContentMetadata(String customerId,
                                   String spaceId,
                                   String contentId,
                                   Properties contentMetadata)
    throws Exception;

    /**
     * Retrieves the metadata associated with content
     *
     * @param customerId
     * @param spaceId
     * @param contentId
     * @return
     * @throws Exception
     */
    public Properties getContentMetadata(String customerId,
                                         String spaceId,
                                         String contentId)
    throws Exception;

}
