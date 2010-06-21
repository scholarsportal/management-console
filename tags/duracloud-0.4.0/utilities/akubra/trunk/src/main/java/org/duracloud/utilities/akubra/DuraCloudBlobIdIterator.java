package org.duracloud.utilities.akubra;

import java.net.URI;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

/**
 * Iterates over all DuraCloud content ids in the given iterator, translating
 * them to URIs on the way out.
 *
 * @author Chris Wilper
 */
class DuraCloudBlobIdIterator
        extends AbstractIterator<URI> {

    private final String uriPrefix;

    private final Iterator<String> ids;

    DuraCloudBlobIdIterator(String uriPrefix,
                            Iterator<String> ids) {
        this.uriPrefix = uriPrefix;
        this.ids = ids;
    }

    @Override
    protected URI computeNext() {
        while (ids.hasNext()) {
            return URI.create(uriPrefix + ids.next());
        }
        return endOfData();
    }
}
