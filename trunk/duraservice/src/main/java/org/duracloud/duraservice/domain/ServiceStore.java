/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duraservice.domain;

/**
 * Contains the information necessary to connect to a DuraCloud
 * store which houses service packages
 *
 * @author Bill Branan
 */
public class ServiceStore extends Store {

    private String spaceId;

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

}
