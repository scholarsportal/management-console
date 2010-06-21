package org.duracloud.duraservice.domain;

/**
 * Contains the information necessary to connect to a DuraCloud
 * store in order to access user contentn
 *
 * @author Bill Branan
 */
public class UserStore extends Store {

    private String msgBrokerUrl;

    public String getMsgBrokerUrl() {
        return msgBrokerUrl;
    }

    public void setMsgBrokerUrl(String msgBrokerUrl) {
        this.msgBrokerUrl = msgBrokerUrl;
    }
}