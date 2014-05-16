/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.compute;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;

import org.duracloud.account.db.model.InstanceType;
import org.duracloud.account.compute.error.DuracloudInstanceNotAvailableException;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author: Bill Branan
 * Date: Feb 8, 2011
 */
public class AmazonComputeProviderTest {

    @Test
    public void testStart() throws Exception {
        doTestStart(true);
    }

    @Test
    public void testStartError() throws Exception {
        doTestStart(false);
    }

    private void doTestStart(boolean valid) throws Exception {
        String instanceId = "my-instance-id";
        String imageId = "abcd-image-id";
        String securityGroup = "security-group";
        String keyname = "keyname";
        String elasticIp = "127.0.0.1";

        // Set up mock
        AmazonEC2Client mockEC2Client =
            EasyMock.createMock(AmazonEC2Client.class);

        DescribeAddressesResult describeAddressesResult =
            new DescribeAddressesResult().withAddresses(
                new Address().withInstanceId(instanceId));

        EasyMock.expect(mockEC2Client.describeAddresses(
            EasyMock.isA(DescribeAddressesRequest.class)))
            .andReturn(describeAddressesResult)
            .times(1);

        Capture<TerminateInstancesRequest> terminateCapture =
            new Capture<TerminateInstancesRequest>();
        EasyMock.expect(mockEC2Client.terminateInstances(
            EasyMock.capture(terminateCapture)))
            .andReturn(null)
            .times(1);

        RunInstancesResult result =
            new RunInstancesResult().withReservation(
                new Reservation().withInstances(
                    new Instance().withInstanceId(instanceId)));

        Capture<RunInstancesRequest> requestCapture =
            new Capture<RunInstancesRequest>();
        EasyMock.expect(
            mockEC2Client.runInstances(EasyMock.capture(requestCapture)))
            .andReturn(result)
            .times(1);

        Capture<AssociateAddressRequest> associateCapture =
            new Capture<AssociateAddressRequest>();

        if (!valid) {
            EasyMock.expect(mockEC2Client.associateAddress(EasyMock.capture(
                associateCapture)))
                .andThrow(new RuntimeException("canned-error"));
        }

        EasyMock.expect(
            mockEC2Client.associateAddress(EasyMock.capture(associateCapture)))
            .andReturn(new AssociateAddressResult());

        EasyMock.replay(mockEC2Client);

        // Run test
        AmazonComputeProvider computeProvider =
            new AmazonComputeProvider(mockEC2Client);
        String resultInstanceId = computeProvider.doStart(imageId,
                                                          securityGroup,
                                                          keyname,
                                                          elasticIp,
                                                          false,
                                                          InstanceType.SMALL);
        assertEquals(instanceId, resultInstanceId);

        EasyMock.verify(mockEC2Client);
        RunInstancesRequest request = requestCapture.getValue();
        assertEquals(imageId, request.getImageId());
        List<String> requestSecurityGroups = request.getSecurityGroups();
        assertEquals(1, requestSecurityGroups.size());
        assertEquals(securityGroup, requestSecurityGroups.iterator().next());
        assertEquals(keyname, request.getKeyName());

        AssociateAddressRequest associateRequest = associateCapture.getValue();
        assertEquals(elasticIp, associateRequest.getPublicIp());

        TerminateInstancesRequest terminateRequest =
            terminateCapture.getValue();
        assertEquals(1, terminateRequest.getInstanceIds().size());
        assertEquals(instanceId, terminateRequest.getInstanceIds().get(0));
    }

    @Test
    public void testStop() throws Exception {
        AmazonEC2Client mockEC2Client =
            EasyMock.createMock(AmazonEC2Client.class);
        Capture<TerminateInstancesRequest> requestCapture =
            new Capture<TerminateInstancesRequest>();
        EasyMock.expect(
            mockEC2Client.terminateInstances(EasyMock.capture(requestCapture)))
            .andReturn(null)
            .times(1);
        EasyMock.replay(mockEC2Client);

        AmazonComputeProvider computeProvider =
            new AmazonComputeProvider(mockEC2Client);
        String instanceId = "my-instance-id";
        computeProvider.stop(instanceId);

        EasyMock.verify(mockEC2Client);
        TerminateInstancesRequest request = requestCapture.getValue();
        assertEquals(instanceId, request.getInstanceIds().get(0));
    }

    @Test
    public void testRestart() throws Exception {
        AmazonEC2Client mockEC2Client =
            EasyMock.createMock(AmazonEC2Client.class);
        Capture<RebootInstancesRequest> requestCapture =
            new Capture<RebootInstancesRequest>();
        mockEC2Client.rebootInstances(EasyMock.capture(requestCapture));
        EasyMock.expectLastCall()
            .times(1);
        EasyMock.replay(mockEC2Client);

        AmazonComputeProvider computeProvider =
            new AmazonComputeProvider(mockEC2Client);
        String instanceId = "my-instance-id";
        computeProvider.restart(instanceId);

        EasyMock.verify(mockEC2Client);
        RebootInstancesRequest request = requestCapture.getValue();
        assertEquals(instanceId, request.getInstanceIds().get(0));
    }

    @Test
    public void testGetStatus() throws Exception {
        doTestGetStatus(true);
    }

    @Test
    public void testGetStatusError() throws Exception {
        doTestGetStatus(false);
    }

    private void doTestGetStatus(boolean valid)
        throws DuracloudInstanceNotAvailableException {
        AmazonEC2Client mockEC2Client =
            EasyMock.createMock(AmazonEC2Client.class);

        String state = "pending";
        DescribeInstancesResult result =
            new DescribeInstancesResult().withReservations(
                new Reservation().withInstances(
                    new Instance().withState(
                        new InstanceState().withName(state))));

        if (!valid) {
            EasyMock.expect(mockEC2Client.describeInstances(EasyMock.isA(
                DescribeInstancesRequest.class))).andThrow(new RuntimeException(
                "canned-exception"));
        }

        EasyMock.expect(
            mockEC2Client.describeInstances(EasyMock.isA(
                DescribeInstancesRequest.class)))
            .andReturn(result)
            .times(1);
        EasyMock.replay(mockEC2Client);

        AmazonComputeProvider computeProvider =
            new AmazonComputeProvider(mockEC2Client);
        String instanceId = "my-instance-id";
        String status = computeProvider.getStatus(instanceId);
        assertEquals(state, status);

        EasyMock.verify(mockEC2Client);
    }

}
