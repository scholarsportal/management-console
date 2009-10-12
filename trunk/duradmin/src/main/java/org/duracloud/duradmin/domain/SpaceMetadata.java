
package org.duracloud.duradmin.domain;

import java.io.Serializable;

/**
 * Stores space metadata.
 * 
 * @author Bill Branan
 */
public class SpaceMetadata implements Serializable{

    private String access;

    private String created;

    private String count;

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
