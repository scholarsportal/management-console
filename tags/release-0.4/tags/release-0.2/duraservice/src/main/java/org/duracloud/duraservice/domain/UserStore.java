package org.duracloud.duraservice.domain;

/**
 * Contains the information necessary to connect to a DuraCloud
 * store in order to access user contentn
 *
 * @author Bill Branan
 */
public class UserStore {

    private String host;
    private String port;
    private String context;
    private String msgBrokerUrl;

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

    public String getMsgBrokerUrl() {
        return msgBrokerUrl;
    }

    public void setMsgBrokerUrl(String msgBrokerUrl) {
        this.msgBrokerUrl = msgBrokerUrl;
    }
}