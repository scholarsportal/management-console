package org.duraspace.domain;

import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

/**
 * Content - a stream of bits and metadata to describe the stream.
 *
 * @author Bill Branan
 */
public class Content {
    private String id;
    private Map<String, String> metadata = new HashMap<String, String>();
    private InputStream stream = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(String name, String value) {
        metadata.put(name, value);
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }
}
