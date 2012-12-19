package org.duracloud.aitsync.domain;


import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;


/**
 * @author Daniel Bernstein
 * @created 12/17/2012
 */
public class MappingForm {

    @Range(min=1)
    private long archiveItAccountId;
    
    @NotEmpty
    private String duracloudHost;

    @Range(min=1,max=65536)
    private Integer duracloudPort;

    @NotEmpty
    private String duracloudSpaceId;

    public long getArchiveItAccountId() {
        return archiveItAccountId;
    }

    public void setArchiveItAccountId(long archiveItAccountId) {
        this.archiveItAccountId = archiveItAccountId;
    }

    public String getDuracloudHost() {
        return duracloudHost;
    }

    public void setDuracloudHost(String duracloudHost) {
        this.duracloudHost = duracloudHost;
    }

    public Integer getDuracloudPort() {
        return duracloudPort;
    }

    public void setDuracloudPort(Integer duracloudPort) {
        this.duracloudPort = duracloudPort;
    }

    public String getDuracloudSpaceId() {
        return duracloudSpaceId;
    }

    public void setDuracloudSpaceId(String duracloudSpaceId) {
        this.duracloudSpaceId = duracloudSpaceId;
    }
    
    public Mapping toMapping(){
        return new Mapping(this.archiveItAccountId,
                           this.duracloudHost,
                           this.duracloudPort,
                           this.duracloudSpaceId);
    }

}
