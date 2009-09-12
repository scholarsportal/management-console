
package org.duracloud.durastore.storage;

import org.duracloud.storage.domain.StorageException;
import org.duracloud.storage.provider.StorageProvider;

/**
 * This interface is to be implemented by AOP-like proxies of the
 * StorageProvidersTest.
 *
 * @author Andrew Woods
 */
public interface StorageProvidersTestInterface {

    public void testGetSpaces(StorageProvider provider,
                              String spaceId0,
                              String spaceId1) throws StorageException;

    public void testGetSpaceContents(StorageProvider provider,
                                     String spaceId0,
                                     String contentId0,
                                     String contentId1) throws StorageException;

    public void testCreateSpace(StorageProvider provider, String spaceId0)
            throws StorageException;

    public void testDeleteSpace(StorageProvider provider,
                                String spaceId0,
                                String spaceId1) throws StorageException;

    public void testGetSpaceMetadata(StorageProvider provider, String spaceId0)
            throws StorageException;

    public void testSetSpaceMetadata(StorageProvider provider, String spaceId0)
            throws StorageException;

    public void testGetSpaceAccess(StorageProvider provider, String spaceId0)
            throws StorageException;

    public void testAddAndGetContent(StorageProvider provider,
                                     String spaceId0,
                                     String contentId0,
                                     String contentId1,
                                     String contentId2) throws Exception;

    public void testAddAndGetContentOverwrite(StorageProvider provider,
                                              String spaceId0,
                                              String contentId0,
                                              String contentId1)
            throws Exception;

    public void testAddContentLarge(StorageProvider provider,
                                    String spaceId0,
                                    String contentId0,
                                    String contentId1) throws Exception;

    public void testDeleteContent(StorageProvider provider,
                                  String spaceId0,
                                  String contentId0,
                                  String contentId1) throws StorageException;

    public void testSetContentMetadata(StorageProvider provider,
                                       String spaceId0,
                                       String spaceId1,
                                       String contentId0)
            throws StorageException;

    public void testGetContentMetadata(StorageProvider provider,
                                       String spaceId0,
                                       String contentId0)
            throws StorageException;

    public void close();

}