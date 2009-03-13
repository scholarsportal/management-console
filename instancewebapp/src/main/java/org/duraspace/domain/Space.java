
package org.duraspace.domain;

import java.io.Serializable;

import java.util.List;

public class Space implements Serializable {

    private static final long serialVersionUID = 3008516494814826947L;

    private String customerId;
    private String spaceId;
    private SpaceMetadata metadata;
    private List<String> contents;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public SpaceMetadata getMetadata() {
        return metadata;
    }


    public void setMetadata(SpaceMetadata metadata) {
        this.metadata = metadata;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

}
