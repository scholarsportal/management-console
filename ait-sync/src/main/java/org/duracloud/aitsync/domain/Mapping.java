package org.duracloud.aitsync.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Daniel Bernstein
 * @created 12/17/2012
 */
@XStreamAlias("mapping")
public class Mapping {

    private long archiveItAccountId;
    private String duracloudHost;
    private Integer duracloudPort;
    private String duracloudSpaceId;

    public Mapping() {

    }

    public Mapping(
        long archiveItAccountId, 
        String duracloudHost, 
        Integer duracloudPort,
        String duracloudSpaceId) {
        super();
        this.archiveItAccountId = archiveItAccountId;
        this.duracloudHost = duracloudHost;
        this.duracloudPort = duracloudPort;
        this.duracloudSpaceId = duracloudSpaceId;
    }

    public long getArchiveItAccountId() {
        return archiveItAccountId;
    }

    public String getDuracloudHost() {
        return duracloudHost;
    }

    public Integer getDuracloudPort() {
        return duracloudPort;
    }

    public String getDuracloudSpaceId() {
        return duracloudSpaceId;
    }
}
