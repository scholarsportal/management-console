package org.duracloud.customerwebapp.domain;

import java.io.Serializable;

import java.util.List;

public class Space implements Serializable {

    private static final long serialVersionUID = 3008516494814826947L;

    private String action;
    private String spaceId;
    private String name;
    private String access;
    private SpaceMetadata metadata;
    private List<String> contents;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
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
