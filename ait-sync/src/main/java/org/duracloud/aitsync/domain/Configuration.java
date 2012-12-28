package org.duracloud.aitsync.domain;

/**
 * 
 * @author Daniel Bernstein
 * @created 12/17/2012
 * 
 */
public class Configuration {
    private String duracloudHost;
    private String duracloudPort;
    private String duracloudUsername;
    private String duracloudPassword;

    public String getDuracloudHost() {
        return duracloudHost;
    }

    public void setDuracloudHost(String duracloudHost) {
        this.duracloudHost = duracloudHost;
    }

    public String getDuracloudPort() {
        return duracloudPort;
    }

    public void setDuracloudPort(String duracloudPort) {
        this.duracloudPort = duracloudPort;
    }

    public String getDuracloudUsername() {
        return duracloudUsername;
    }

    public void setDuracloudUsername(String duracloudUsername) {
        this.duracloudUsername = duracloudUsername;
    }

    public String getDuracloudPassword() {
        return duracloudPassword;
    }

    public void setDuracloudPassword(String duracloudPassword) {
        this.duracloudPassword = duracloudPassword;
    }

}
