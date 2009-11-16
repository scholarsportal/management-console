package org.duracloud.utilities.akubra;

import java.io.IOException;

import java.net.URI;

import org.akubraproject.BlobStore;
import org.akubraproject.tck.TCKTestSuite;
import org.duracloud.client.ContentStoreException;

/**
 * Akubra-DuraCloud TCK Integration Tests.
 *
 * @author Chris Wilper
 */
public class AkubraDuraCloudTCKIT extends TCKTestSuite {

    static URI spaceURL;

    public AkubraDuraCloudTCKIT() throws IOException, ContentStoreException {
        super(getStore(), getSpaceURL(), false, false);
    }

    private static BlobStore getStore()
            throws IOException {
        return new DuraCloudBlobStore(getSpaceURL());
    }

    @Override
    protected URI createId(String name) {
      return URI.create(getSpaceURL() + "/" + name);
    }

    @Override
    protected String getPrefixFor(String name) {
      return getSpaceURL() + "/" + name;
    }

    @Override
    protected URI getInvalidId() {
      return URI.create("urn:invalidId");
    }

    @Override
    protected URI[] getAliases(URI uri) {
      return new URI[] { uri };
    }

    private static URI getSpaceURL() {
        if (spaceURL == null) {
            String url = System.getProperty("spaceURL");
            if (url == null || url.length() == 0) {
                throw new Error("System property, spaceURL, is not defined");
            }
            spaceURL = URI.create(url);
        }
        return spaceURL;
    }

}
