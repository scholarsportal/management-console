
package org.duracloud.service.replication;

import org.duracloud.computeservice.ComputeService;

public class ReplicationService
        implements ComputeService {

    public void start() throws Exception {
        System.out.println("Starting Replication Service...");

        System.out.println("Replication Service Started");
    }

    public void stop() throws Exception {
        System.out.println("Stopping Replication Service...");

        System.out.println("Replication Service Stopped");
    }

    public String describe() throws Exception {
        return "Service: " + getClass().getName();
    }

}
