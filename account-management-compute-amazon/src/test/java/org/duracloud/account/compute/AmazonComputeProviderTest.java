/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.compute;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.CreateInstanceProfileRequest;
import com.amazonaws.services.identitymanagement.model.CreateInstanceProfileResult;
import com.amazonaws.services.identitymanagement.model.GetInstanceProfileRequest;
import com.amazonaws.services.identitymanagement.model.InstanceProfile;
import com.amazonaws.services.identitymanagement.model.NoSuchEntityException;
import org.duracloud.account.db.model.InstanceType;
import org.duracloud.account.compute.error.DuracloudInstanceNotAvailableException;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import java.util.List;

import junit.framework.Assert;

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
        String instanceName = "instance-name";
        String imageId = "abcd-image-id";
        String securityGroup = "security-group";
        String keyname = "keyname";
        String elasticIp = "127.0.0.1";
        String iamRole = "iam-role";

        // Mock EC2 client
        AmazonEC2Client mockEC2Client =
            EasyMock.createMock(AmazonEC2Client.class);

        // Mock IAM client
        AmazonIdentityManagementClient iamClient =
            EasyMock.createMock(AmazonIdentityManagementClient.class);

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

        Capture<CreateTagsRequest> createTagsCapture =
                new Capture<CreateTagsRequest>();
        
        mockEC2Client.createTags(EasyMock.capture(createTagsCapture));
        EasyMock.expectLastCall();
        
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

        EasyMock.expect(iamClient.getInstanceProfile(
            EasyMock.isA(GetInstanceProfileRequest.class)))
                .andThrow(new NoSuchEntityException("does not exist"));

        InstanceProfile instanceProfile =
            new InstanceProfile().withArn("arn").withInstanceProfileName(iamRole);
        EasyMock.expect(iamClient.createInstanceProfile(
            EasyMock.isA(CreateInstanceProfileRequest.class)))
                .andReturn(new CreateInstanceProfileResult()
                               .withInstanceProfile(instanceProfile));

        EasyMock.replay(mockEC2Client, iamClient);

        // Run test
        AmazonComputeProvider computeProvider =
            new AmazonComputeProvider(mockEC2Client, iamClient);
        String resultInstanceId = computeProvider.doStart(imageId,
                                                          iamRole,
                                                          securityGroup,
                                                          keyname,
                                                          elasticIp,
                                                          false,
                                                          InstanceType.SMALL, 
                                                          instanceName);
        assertEquals(instanceId, resultInstanceId);

        EasyMock.verify(mockEC2Client);

        RunInstancesRequest request = requestCapture.getValue();
        assertEquals(imageId, request.getImageId());
        List<String> requestSecurityGroups = request.getSecurityGroups();
        assertEquals(1, requestSecurityGroups.size());
        assertEquals(securityGroup, requestSecurityGroups.iterator().next());
        assertEquals(keyname, request.getKeyName());
        assertEquals(iamRole, request.getIamInstanceProfile().getName());

        AssociateAddressRequest associateRequest = associateCapture.getValue();
        assertEquals(elasticIp, associateRequest.getPublicIp());

        TerminateInstancesRequest terminateRequest =
            terminateCapture.getValue();
        assertEquals(1, terminateRequest.getInstanceIds().size());
        assertEquals(instanceId, terminateRequest.getInstanceIds().get(0));
        
        CreateTagsRequest createTags = createTagsCapture.getValue();
        Tag tag = createTags.getTags().get(0);
        Assert.assertEquals("Name",tag.getKey());
        Assert.assertEquals(instanceName, tag.getValue());
        
    }

    @Test
    public void testStop() throws Exception {
        AmazonEC2Client mockEC2Client =
            EasyMock.createMock(AmazonEC2Client.class);
        AmazonIdentityManagementClient iamClient =
            EasyMock.createMock(AmazonIdentityManagementClient.class);

        Capture<TerminateInstancesRequest> requestCapture =
            new Capture<TerminateInstancesRequest>();
        EasyMock.expect(
            mockEC2Client.terminateInstances(EasyMock.capture(requestCapture)))
            .andReturn(null)
            .times(1);
        EasyMock.replay(mockEC2Client, iamClient);

        AmazonComputeProvider computeProvider =
            new AmazonComputeProvider(mockEC2Client, iamClient);
        String instanceId = "my-instance-id";
        computeProvider.stop(instanceId);

        EasyMock.verify(mockEC2Client, iamClient);
        TerminateInstancesRequest request = requestCapture.getValue();
        assertEquals(instanceId, request.getInstanceIds().get(0));
    }

    @Test
    public void testRestart() throws Exception {
        AmazonEC2Client mockEC2Client =
            EasyMock.createMock(AmazonEC2Client.class);
        AmazonIdentityManagementClient iamClient =
            EasyMock.createMock(AmazonIdentityManagementClient.class);

        Capture<RebootInstancesRequest> requestCapture =
            new Capture<RebootInstancesRequest>();
        mockEC2Client.rebootInstances(EasyMock.capture(requestCapture));
        EasyMock.expectLastCall()
            .times(1);
        EasyMock.replay(mockEC2Client, iamClient);

        AmazonComputeProvider computeProvider =
            new AmazonComputeProvider(mockEC2Client, iamClient);
        String instanceId = "my-instance-id";
        computeProvider.restart(instanceId);

        EasyMock.verify(mockEC2Client, iamClient);
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
        AmazonIdentityManagementClient iamClient =
            EasyMock.createMock(AmazonIdentityManagementClient.class);

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

        EasyMock.replay(mockEC2Client, iamClient);

        AmazonComputeProvider computeProvider =
            new AmazonComputeProvider(mockEC2Client, iamClient);
        String instanceId = "my-instance-id";
        String status = computeProvider.getStatus(instanceId);
        assertEquals(state, status);

        EasyMock.verify(mockEC2Client, iamClient);
    }

    @Test
    public void testConvertInstanceType() {
        AmazonComputeProvider computeProvider = new AmazonComputeProvider("user", "pass");
        assertEquals("m1.small",
                     computeProvider.convertDuracloudInstanceTypeToNative(InstanceType.SMALL));
        assertEquals("m3.medium",
                     computeProvider.convertDuracloudInstanceTypeToNative(InstanceType.MEDIUM));
        assertEquals("m3.large",
                     computeProvider.convertDuracloudInstanceTypeToNative(InstanceType.LARGE));
        assertEquals("m3.xlarge",
                     computeProvider.convertDuracloudInstanceTypeToNative(InstanceType.XLARGE));
    }

}
