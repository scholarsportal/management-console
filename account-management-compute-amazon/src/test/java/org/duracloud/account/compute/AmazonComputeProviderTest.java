/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.compute;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import org.easymock.Capture;
import org.easymock.classextension.EasyMock;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author: Bill Branan
 * Date: Feb 8, 2011
 */
public class AmazonComputeProviderTest {

    @Test
    public void testStart() throws Exception {
        AmazonEC2Client mockEC2Client =
            EasyMock.createMock(AmazonEC2Client.class);

        String instanceId = "my-instance-id";
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
        EasyMock.replay(mockEC2Client);

        AmazonComputeProvider computeProvider =
            new AmazonComputeProvider(mockEC2Client);
        String imageId = "abcd-image-id";
        String resultInstanceId = computeProvider.start(imageId);
        assertEquals(instanceId, resultInstanceId);

        EasyMock.verify(mockEC2Client);
        RunInstancesRequest request = requestCapture.getValue();
        assertEquals(imageId, request.getImageId());
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
        AmazonEC2Client mockEC2Client =
            EasyMock.createMock(AmazonEC2Client.class);

        String state = "pending";
        DescribeInstancesResult result =
            new DescribeInstancesResult().withReservations(
                new Reservation().withInstances(
                    new Instance().withState(
                        new InstanceState().withName(state))));

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
