package org.duracloud.utilities.akubra;

import java.net.URI;

import org.akubraproject.map.IdMapper;

/**
 * Provides DuraCloudBlobStore-appropriate URIs for any given URI.
 * <p>
 * This mapper takes care of escaping URI-valid characters that are illegal in
 * DuraCloud URIs: "?" will be encoded as "~3F", and "~" will be encoded as "~7E".
 * <p>
 * It also supports the use of a path prefix when generating all internal ids.
 * For example, if <em>pathPrefix</em> is given as <code>myPrefix/</code>,
 * the external URI <code>urn:test:1</code> would become something like
 * <code>http://host:port/durastore/space-id/myPrefix/urn:test:1</code>.
 *
 * @author Chris Wilper
 */
public class DuraCloudIdMapper implements IdMapper {

    private final String internalPrefix;

    /**
     * Creates an instance.
     *
     * @param spaceURL the URL of DuraCloud space.
     * @param pathPrefix the path prefix to use when constructing internal ids.
     *        If null or empty, no path prefix will be used.
     * @throws IllegalArgumentException if the spaceURL is not in the expected
     *         form.
     */
    public DuraCloudIdMapper(URI spaceURL, String pathPrefix) {
        DuraCloudBlobStore.parseSpaceURL(spaceURL);
        if (pathPrefix == null || pathPrefix.length() == 0) {
            this.internalPrefix = spaceURL + "/";
        } else {
            this.internalPrefix = spaceURL + "/" + pathPrefix;
        }
    }

    @Override
    public URI getExternalId(URI internalId) {
        String contentId = internalId.toString().substring(internalPrefix.length());
        return URI.create(decode(contentId));
    }

    @Override
    public URI getInternalId(URI externalId) {
        return URI.create(internalPrefix + encode(externalId.toString()));
    }

    @Override
    public String getInternalPrefix(String externalPrefix) {
        return internalPrefix + encode(externalPrefix);
    }

    // escape '?' as '~3F' and '~' as '~7E'
    static String encode(String externalString) {
        StringBuilder builder = new StringBuilder(externalString.length());
        for (int i = 0; i < externalString.length(); i++) {
            char c = externalString.charAt(i);
            if (c == '?') {
                builder.append("~3F");
            } else if (c == '~') {
                builder.append("~7E");
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    // reverse the encoding
    static String decode(String internalString) {
        StringBuilder builder = new StringBuilder(internalString.length());
        int i = 0;
        while (i < internalString.length()) {
            char c = internalString.charAt(i);
            if (c == '~') {
                if (i + 2 < internalString.length()) {
                    String nextTwo = internalString.substring(i + 1, i + 3);
                    if (nextTwo.equals("3F")) {
                        builder.append('?');
                    } else if (nextTwo.equals("7E")) {
                        builder.append('~');
                    } else {
                        builder.append(nextTwo);
                    }
                    i += 2;
                } else {
                    builder.append(c);
                }
            } else {
                builder.append(c);
            }
            i++;
        }
        return builder.toString();
    }

}
