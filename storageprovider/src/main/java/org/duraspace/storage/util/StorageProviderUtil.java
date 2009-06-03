package org.duraspace.storage.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.duraspace.storage.domain.StorageException;
import org.duraspace.storage.provider.StorageProvider;

import static org.duraspace.common.util.IOUtil.readStringFromStream;
import static org.duraspace.common.util.SerializationUtil.deserializeMap;
import static org.duraspace.common.util.SerializationUtil.serializeMap;

/**
 * Provides utility methods for Storage Providers
 *
 * @author Bill Branan
 */
public class StorageProviderUtil {

    /**
     * Loads a stream containing metadata and populates a map
     * with the metadata name/value pairs.
     *
     * @param is
     * @return
     * @throws StorageException
     */
    public static Map<String, String> loadMetadata(InputStream is)
    throws StorageException {
        Map<String, String> metadataMap = null;
        if(is != null) {
            try {
                String metadata = readStringFromStream(is);
                metadataMap = deserializeMap(metadata);
            } catch(Exception e) {
                String err = "Could not read metadata " +
                             " due to error: " + e.getMessage();
                throw new StorageException(err, e);
            }
        }

        if(metadataMap == null) {
            metadataMap = new HashMap<String, String>();
        }

        return metadataMap;
    }

    /**
     * Converts metadata stored in a Map into a stream for storage purposes.
     *
     * @param metadataMap
     * @return
     * @throws StorageException
     */
    public static ByteArrayInputStream storeMetadata(Map<String, String> metadataMap)
    throws StorageException {
        // Pull out known computed values
        metadataMap.remove(StorageProvider.METADATA_SPACE_COUNT);
        metadataMap.remove(StorageProvider.METADATA_SPACE_ACCESS);

        // Serialize Map
        byte[] metadata = null;
        try {
            String serializedMetadata = serializeMap(metadataMap);
            metadata = serializedMetadata.getBytes("UTF-8");
        } catch (Exception e) {
            String err = "Could not store metadata" +
                         " due to error: " + e.getMessage();
            throw new StorageException(err);
        }

        ByteArrayInputStream is = new ByteArrayInputStream(metadata);
        return is;
    }

    /**
     * Determines if a String value is included in a Iterated list.
     * The iteration is only run as far as necessary to determine
     * if the value is included in the underlying list.
     *
     * @param list
     * @param value
     * @return
     */
    public static boolean contains(Iterator<String> iterator, String value) {
        if(iterator == null || value == null) {
            return false;
        }
        while(iterator.hasNext()) {
            if(value.equals(iterator.next())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines the number of elements in an iteration.
     *
     * @param list
     * @param value
     * @return
     */
    public static long count(Iterator<String> iterator) {
        if(iterator == null) {
            return 0;
        }
        long count = 0;
        while(iterator.hasNext()) {
            ++count;
            iterator.next();
        }
        return count;
    }

    /**
     * Creates a list of all of the items in an iteration.
     * Be wary of using this for Iterations of very long lists.
     *
     * @param iterator
     * @return
     */
    public static List<String> getList(Iterator<String> iterator) {
        List<String> contents = new ArrayList<String>();
        while(iterator.hasNext()) {
            contents.add(iterator.next());
        }
        return contents;
    }

}
