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

    private AmazonEC2Client ec2Client;

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
        RunInstancesRequest request =
            new RunInstancesRequest(providerImageId, 1, 1);

        Set<String> securityGroups = new HashSet<String>();
        securityGroups.add(securityGroup);
        request.setSecurityGroups(securityGroups);
        request.setKeyName(keyname);

        RunInstancesResult result = ec2Client.runInstances(request);
        String instanceId = result.getReservation().getInstances()
                                  .iterator().next().getInstanceId();

        AssociateAddressRequest associateRequest =
            new AssociateAddressRequest(instanceId, elasticIp);
        ec2Client.associateAddress(associateRequest);

        return instanceId;
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
