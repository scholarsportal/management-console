
package org.duracloud.services.replication;

import org.duracloud.services.ComputeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReplicationService
        implements ComputeService {

    private static final Logger log =
        LoggerFactory.getLogger(ReplicationService.class);

    private String host;
    private String port;
    private String context;
    private String baseURL;

    public void start() throws Exception {
        log.info("Starting Replication Service");
        baseURL = "http://" + host + ":" + port + "/" + context + "/";
        System.out.println("The baseURL: " + baseURL);
    }

    public void stop() throws Exception {
        log.info("Stopping Replication Service");
    }

    public String describe() throws Exception {
        return "Service: " + getClass().getName();
    }


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
