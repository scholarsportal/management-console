package org.duracloud.duraservice.domain;

/**
 * Contains the information necessary to connect to a DuraCloud store.
 *
 * @author Andrew Woods
 *         Date: Mar 30, 2010
 */
public class Store {

    private String host;
    private String port;
    private String context;

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
}
