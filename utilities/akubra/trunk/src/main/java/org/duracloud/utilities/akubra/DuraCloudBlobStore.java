package org.duracloud.utilities.akubra;

import java.io.IOException;

import java.net.URI;

import java.util.Map;

import javax.transaction.Transaction;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.client.ContentStoreManagerImpl;
import org.fedoracommons.akubra.Blob;
import org.fedoracommons.akubra.BlobStoreConnection;
import org.fedoracommons.akubra.impl.AbstractBlobStore;
import org.fedoracommons.akubra.impl.StreamManager;

/**
 * DuraCloud-backed Akubra implementation.
 * <p>
 * This is a <em>non-transactional</em> store that provides read/write access
 * to content stored within a given space in a given DuraCloud content store.
 * <p>
 * <h2>Blob Ids</h2>
 * This store supports DuraCloud content URLs as blob ids.  These are of the
 * form http://<em>host:port</em>/durastore/<em>spaceID</em>/<em>contentID</em>
 * <p>
 * <h2>Id Generation</h2>
 * This store does not support id generation.
 * <p>
 * <h2>Canonical Ids</h2>
 * This store does not attempt to canonicalize ids; calls to
 * {@link Blob#getCanonicalId} will always return null.
 *
 * @author Chris Wilper
 */
public class DuraCloudBlobStore extends AbstractBlobStore {

    private final StreamManager streamManager = new StreamManager();

    private final ContentStore contentStore;

    private final String spaceId;

    /**
     * Creates an instance for the given DuraCloud space URL.
     * <p>
     * The url will be used as the id for this BlobStore, and must be
     * of the form: <code>http://host[:port]/context/spaceId</code>.
     *
     * @param spaceURL the url of the space.
     * @throws IOException if the space does not exist or there is an error
     *         connecting to the content store.
     * @throws IllegalArgumentException if the spaceURL is not in the
     *         expected form.
     */
    public DuraCloudBlobStore(URI spaceURL)
            throws IOException, IllegalArgumentException {
        super(spaceURL);
        try {
            String[] p = parseSpaceURL(spaceURL);
            this.contentStore = new ContentStoreManagerImpl(p[0], p[1], p[2])
                    .getPrimaryContentStore();
            this.spaceId = p[3];
            contentStore.getSpace(spaceId);
        } catch (ContentStoreException e) {
            IOException ioe = new IOException(
                    "Error initializing ContentStore");
            ioe.initCause(e);
            throw ioe;
        }
    }

    // Package-visible constructor for testing.
    DuraCloudBlobStore(ContentStore contentStore,
                       String spaceId) {
        super(URI.create(contentStore.getBaseURL() + "/" + spaceId));
        this.contentStore = contentStore;
        this.spaceId = spaceId;
    }

    // Validates spaceURL and returns { host, port, context, spaceId }.
    static String[] parseSpaceURL(URI spaceURL)
            throws IllegalArgumentException {
        final String ePfx = "Illegal spaceURL: " + spaceURL + " - ";
        if (!spaceURL.getScheme().equals("http")) {
            throw new IllegalArgumentException(ePfx + "not an http URI");
        }
        String port;
        if (spaceURL.getPort() == -1) {
            port = "80";
        } else {
            port = "" + spaceURL.getPort();
        }
        String path = spaceURL.getRawPath();
        if (path.endsWith("/")) {
            throw new IllegalArgumentException(ePfx + "cannot end with /");
        }
        String[] p = spaceURL.getRawPath().split("/");
        if (p.length < 3) {
            throw new IllegalArgumentException(ePfx + "too few path segments");
        } else if (p.length > 3) {
            throw new IllegalArgumentException(ePfx + "too many path segments");
        }
        return new String[] { spaceURL.getHost(), port, p[1], p[2] };
    }

    //@Override
    public BlobStoreConnection openConnection(Transaction tx,
                                              Map<String, String> hints)
            throws UnsupportedOperationException, IOException {
        if (tx != null) {
            throw new UnsupportedOperationException();
        }
        return new DuraCloudBlobStoreConnection(this, streamManager,
                                                contentStore, spaceId);
    }

}
