package org.duraspace.domain;


/**
 * Stores space metadata.
 *
 * @author Bill Branan
 */
public class SpaceMetadata {

    private String name;
    private String access;
    private String created;
    private String count;

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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

}
