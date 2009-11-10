package org.duracloud.utilities.akubra;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

/**
 * Iterates over all DuraCloud content ids in the given iterator, translating
 * them to URIs on the way out, and respecting filterPrefix if provided.
 *
 * @author Chris Wilper
 */
class DuraCloudBlobIdIterator
        extends AbstractIterator<URI> {

    private final String uriPrefix;

    private final Iterator<String> ids;

    private final String filterPrefix;

    DuraCloudBlobIdIterator(String uriPrefix,
                            Iterator<String> ids,
                            String filterPrefix) {
        this.uriPrefix = uriPrefix;
        this.ids = ids;
        this.filterPrefix = filterPrefix;
    }

    @Override
    protected URI computeNext() {
        while (ids.hasNext()) {
            try {
                URI uri = new URI(uriPrefix + ids.next());
                if (filterPrefix == null
                        || uri.toString().startsWith(filterPrefix)) {
                    return uri;
                }
            } catch (URISyntaxException wontHappen) {
                throw new Error(wontHappen);
            }
        }
        return endOfData();
    }

}
