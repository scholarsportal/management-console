package org.duracloud.utilities.akubra;

import java.io.IOException;
import java.io.OutputStream;

import java.net.URI;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import org.fedoracommons.akubra.Blob;
import org.fedoracommons.akubra.BlobStore;
import org.fedoracommons.akubra.BlobStoreConnection;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Akubra-DuraCloud Integration Tests.
 *
 * @author Chris Wilper
 */
public class AkubraDuraCloudIT {

    // test value for content
    private static final String VALUE = "value";

    static URI spaceURL;
    static BlobStore store;
    static BlobStoreConnection connection;

    @BeforeClass
    public static void initStore() throws IOException {
        String url = System.getProperty("spaceURL");
        if (url == null) {
            throw new Error("System property, spaceURL, is not defined");
        }
        spaceURL = URI.create(url);
        store = new DuraCloudBlobStore(spaceURL);
        connection = store.openConnection(null, null);
        clear();
    }

    @Test(expectedExceptions=IOException.class)
    public void nonExistingSpace() throws IOException {
        new DuraCloudBlobStore(URI.create(spaceURL + "-nonExistingSpace"));
    }

    @Test
    public void isEmpty() throws IOException {
        assertTrue(list(null).isEmpty());
    }

    @Test
    public void createReadDelete() throws IOException {
        Blob blob = getBlob("createReadDelete");
        putContent(blob, VALUE, true);
        assertEquals(getContent(blob), VALUE);
        blob.delete();
    }

    // gets all blobIds in the test store, filtered by prefix, if given
    private static Set<URI> list(String filterPrefix) throws IOException {
        Set<URI> set = new HashSet<URI>();
        Iterator<URI> iter = connection.listBlobIds(filterPrefix);
        while (iter.hasNext()) {
            set.add(iter.next());
        }
        return set;
    }

    // deletes all blobs in the test store, returning deletedCount
    private static int clear() throws IOException {
        int deletedCount = 0;
        for (URI blobId: list(null)) {
            getBlob(blobId).delete();
            deletedCount++;
        }
        return deletedCount;
    }

    // writes the content of a blob as the given string
    private static void putContent(Blob blob, String content, boolean overwrite)
            throws IOException {
        OutputStream out = blob.openOutputStream(-1, overwrite);
        out.write(content.getBytes("UTF-8"));
        out.close();
    }

    // reads the content of a blob as a string
    private static String getContent(Blob blob) throws IOException {
        return IOUtils.toString(blob.openInputStream());
    }

    // gets a blob from the test store, by blobId
    private static Blob getBlob(URI blobId) throws IOException {
        return connection.getBlob(blobId, null);
    }

    // gets a blob from the test store, by contentId
    private static Blob getBlob(String contentId) throws IOException {
        return getBlob(URI.create(spaceURL + "/" + contentId));
    }

}