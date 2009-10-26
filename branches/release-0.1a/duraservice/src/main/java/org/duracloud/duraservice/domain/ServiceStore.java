package org.duracloud.duraservice.domain;

/**
 * Contains the information necessary to connect to a DuraCloud
 * store which houses service packages
 *
 * @author Bill Branan
 */
public class ServiceStore {

    private String host;
    private String port;
    private String context;
    private String spaceId;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

}