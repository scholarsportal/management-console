package org.duraspace.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Space - the container in which content is stored.
 *
 * @author Bill Branan
 */
public class Space {
    private String id;
    private Map<String, String> metadata = new HashMap<String, String>();
    private List<String> contentIds = new ArrayList<String>();

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

    public List<String> getContentIds() {
        return contentIds;
    }

    public void setContentIds(List<String> contentIds) {
        this.contentIds = contentIds;
    }

    public void addContentId(String contentId) {
        contentIds.add(contentId);
    }

    public boolean equals(Space space) {
        boolean equals = false;
        if(getId().equals(space.getId()) &&
           getMetadata().equals(space.getMetadata()) &&
           getContentIds().equals(space.getContentIds())) {
            equals = true;
        }
        return equals;
    }
}
