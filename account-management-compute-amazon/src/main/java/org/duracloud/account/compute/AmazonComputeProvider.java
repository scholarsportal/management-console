/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.compute;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AssociateAddressRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import org.duracloud.account.compute.error.InstanceStartupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Bill Branan
 *         Date: Feb 7, 2011
 */
public class AmazonComputeProvider implements DuracloudComputeProvider {

	private Logger log = LoggerFactory.getLogger(AmazonComputeProvider.class);

    private static final int STARTUP_WAIT_TIME = 300000; // 5 min
    private static final int SLEEP_TIME = 5000;

    private AmazonEC2Client ec2Client;

    private enum InstanceState {
        PENDING("pending"),
        RUNNING("running"),
        SHUTTING_DOWN("shutting-down"),
        TERMINATED("terminated");

        private String value;

        private InstanceState(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public AmazonComputeProvider(String accessKey, String secretKey) {
        this.ec2Client =
            AmazonComputeConnector.getAmazonEC2Client(accessKey, secretKey);
    }

    protected AmazonComputeProvider(AmazonEC2Client ec2Client) {
        this.ec2Client = ec2Client;
    }

        @Override
    public String start(String providerImageId,
                        String securityGroup,
                        String keyname,
                        String elasticIp) {
        return doStart(providerImageId, securityGroup, keyname, elasticIp, true);
    }

    protected String doStart(String providerImageId,
                             String securityGroup,
                             String keyname,
                             String elasticIp,
                             boolean wait) {
        RunInstancesRequest request =
            new RunInstancesRequest(providerImageId, 1, 1);

        Set<String> securityGroups = new HashSet<String>();
        securityGroups.add(securityGroup);
        request.setSecurityGroups(securityGroups);
        request.setKeyName(keyname);

        RunInstancesResult result = ec2Client.runInstances(request);
        String instanceId = result.getReservation().getInstances()
                                  .iterator().next().getInstanceId();

        if(wait) {
            // Two step verification, to ensure that the
            // instance is known to be running.
            if(waitInstanceRunning(instanceId, STARTUP_WAIT_TIME)) {
                if(!waitInstanceRunning(instanceId, STARTUP_WAIT_TIME)) {
                    startError(instanceId);
                }
            } else {
                startError(instanceId);
            }
        }

        AssociateAddressRequest associateRequest =
            new AssociateAddressRequest(instanceId, elasticIp);
        ec2Client.associateAddress(associateRequest);

        return instanceId;
    }

    private void startError(String instanceId) {
        stop(instanceId);
        String err = "Instance with ID " + instanceId +
            " did not start within " + STARTUP_WAIT_TIME/60000 +
            " minutes. The instance has been shut down.";
        throw new InstanceStartupException(err);
    }

    public boolean waitInstanceRunning(String instanceId, long timeout) {
        long start = System.currentTimeMillis();
        while(!InstanceState.RUNNING.getValue().equals(getStatus(instanceId))) {
            long now = System.currentTimeMillis();
            if(now - start > timeout) {
                log.warn("EC2 instance with ID " + instanceId +
                   " was not available prior to wait timeout of " +
                   timeout + " milliseconds.");
                return false;
            } else {
                sleep(SLEEP_TIME);
            }
        }
        return true;
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch(InterruptedException e) {
        }
    }

    @Override
    public void stop(String providerInstanceId) {
        List<String> instanceIds = getIdList(providerInstanceId);
        TerminateInstancesRequest request = new TerminateInstancesRequest();
        request.setInstanceIds(instanceIds);
        ec2Client.terminateInstances(request);
    }

    @Override
    public void restart(String providerInstanceId) {
        List<String> instanceIds = getIdList(providerInstanceId);
        RebootInstancesRequest request = new RebootInstancesRequest();
        request.setInstanceIds(instanceIds);
        ec2Client.rebootInstances(request);
    }

    @Override
    public String getStatus(String providerInstanceId) {
        List<String> instanceIds = getIdList(providerInstanceId);
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.setInstanceIds(instanceIds);
        DescribeInstancesResult result = ec2Client.describeInstances(request);

        try {
            // Allowed Values: pending, running, shutting-down, terminated
            return result.getReservations().iterator().next().getInstances()
                         .iterator().next().getState().getName();
        } catch(Exception e) {
            log.error("Unable to get status for EC2 instance with ID " +
                      providerInstanceId + " due to error: " + e.getMessage(),
                      e);
            return "unknown";
        }
    }

    private List<String> getIdList(String id) {
        List<String> ids = new ArrayList<String>();
        ids.add(id);
        return ids;
    }
}
