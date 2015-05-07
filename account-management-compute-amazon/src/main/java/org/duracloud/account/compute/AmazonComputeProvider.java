/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.compute;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.AssociateAddressRequest;
import com.amazonaws.services.ec2.model.AssociateAddressResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeAddressesRequest;
import com.amazonaws.services.ec2.model.DescribeAddressesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.IamInstanceProfileSpecification;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.CreateInstanceProfileRequest;
import com.amazonaws.services.identitymanagement.model.CreateInstanceProfileResult;
import com.amazonaws.services.identitymanagement.model.GetInstanceProfileRequest;
import com.amazonaws.services.identitymanagement.model.GetInstanceProfileResult;
import com.amazonaws.services.identitymanagement.model.InstanceProfile;
import com.amazonaws.services.identitymanagement.model.NoSuchEntityException;
import org.duracloud.account.compute.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.compute.error.InstanceStartupException;
import org.duracloud.account.db.model.DuracloudInstance;
import org.duracloud.account.db.model.InstanceType;
import org.duracloud.common.error.DuraCloudRuntimeException;
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
    private AmazonIdentityManagementClient iamClient;

    private enum InstanceState {
        PENDING("pending"),
        RUNNING("running"),
        SHUTTING_DOWN("shutting-down"),
        TERMINATED("terminated"),
        STARTING("starting");

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
        this.iamClient = new AmazonIdentityManagementClient(
            new BasicAWSCredentials(accessKey, secretKey));
    }

    protected AmazonComputeProvider(AmazonEC2Client ec2Client,
                                    AmazonIdentityManagementClient iamClient) {
        this.ec2Client = ec2Client;
        this.iamClient = iamClient;
    }

    @Override
    public String start(String providerImageId,
                        String iamRole,
                        String securityGroup,
                        String keyname,
                        String elasticIp,
                        InstanceType instanceType,
                        String instanceName) {
        return doStart(providerImageId, iamRole, securityGroup, keyname,
                       elasticIp, true, instanceType, instanceName);
    }

    protected String doStart(String providerImageId,
                             String iamRole,
                             String securityGroup,
                             String keyname,
                             String elasticIp,
                             boolean wait,
                             InstanceType instanceType,
                             String instanceName) {
        stopExistingInstances(elasticIp);

        // FIXME: Availability Zone is temporarily hard coded (DURACLOUDPRIV-98)
        String zone = "us-east-1d";

        RunInstancesResult result;
        try {
            result = doRun(providerImageId, iamRole, securityGroup, keyname, zone, true,
                           instanceType, instanceName);
        } catch(AmazonServiceException e) {
            log.warn("Error attempting to start instance: {}. " +
                     "Attempting again with no zone setting.", e.getMessage());
            // Try again with no set zone.
			result = doRun(providerImageId, iamRole, securityGroup, keyname, null,
					       false, instanceType,	instanceName);
        }
        String instanceId = result.getReservation().getInstances()
                                  .iterator().next().getInstanceId();

        if(wait) {
            // Two step verification, to ensure that the
            // instance is known to be running.
            try {
                if(waitInstanceRunning(instanceId, STARTUP_WAIT_TIME)) {
                    if(!waitInstanceRunning(instanceId, STARTUP_WAIT_TIME)) {
                        startError(instanceId);
                    }
                } else {
                    startError(instanceId);
                }
            } catch (DuracloudInstanceNotAvailableException e) {
                startError(instanceId);
            }
        }

        associateAddress(elasticIp, instanceId);

        log.info("Instance started successfully and IP {} attached", elasticIp);
        return instanceId;
    }

    private RunInstancesResult doRun(String providerImageId,
                                     String iamRole,
                                     String securityGroup,
                                     String keyname,
                                     String availabilityZone,
                                     boolean includeZone, 
                                     InstanceType instanceType, 
                                     String instanceName) {
        RunInstancesRequest request =
            new RunInstancesRequest(providerImageId, 1, 1);

        Set<String> securityGroups = new HashSet<String>();
        securityGroups.add(securityGroup);
        request.setSecurityGroups(securityGroups);
        request.setKeyName(keyname);
        request.setInstanceType(convertDuracloudInstanceTypeToNativeM1(instanceType));

        IamInstanceProfileSpecification iamProfileSpec = getIamProfileSpec(iamRole);
        if(null != iamProfileSpec) {
            request.setIamInstanceProfile(iamProfileSpec);
        }

        if(includeZone) {
            request.setPlacement(new Placement(availabilityZone));
        }

        RunInstancesResult result = ec2Client.runInstances(request);
        
        if(instanceName != null){
            String instanceId = result.getReservation().getInstances().get(0)
                    .getInstanceId();
    
            CreateTagsRequest createTagsRequest = new CreateTagsRequest();
            createTagsRequest.withResources(instanceId)
                             .withTags(new Tag("Name", instanceName));
            ec2Client.createTags(createTagsRequest);
        }
        return result;
    }

    /**
     * Retrieves an IAM profile spec for a given IAM role. Note that the role
     * needs to already exist. The IAM profile is created if it does not already exist.
     *
     * @param iamRole name of the IAM role to be retrieved in profile form
     * @return IAM instance profile or null if iamRole is null
     */
    private IamInstanceProfileSpecification getIamProfileSpec(String iamRole) {
        if(null != iamRole) {
            InstanceProfile instanceProfile;
            try {
                GetInstanceProfileResult getProfileResult =
                    iamClient.getInstanceProfile(new GetInstanceProfileRequest()
                                                     .withInstanceProfileName(iamRole));
                instanceProfile = getProfileResult.getInstanceProfile();
            } catch (NoSuchEntityException e) { // No existing entity, create one
                CreateInstanceProfileResult createProfileResult =
                    iamClient.createInstanceProfile(new CreateInstanceProfileRequest()
                                                        .withInstanceProfileName(
                                                            iamRole));
                instanceProfile = createProfileResult.getInstanceProfile();
            }

            if (null != instanceProfile) {
                return new IamInstanceProfileSpecification()
                    .withArn(instanceProfile.getArn())
                    .withName(instanceProfile.getInstanceProfileName());
            }
        }
        return null;
    }

    private void stopExistingInstances(String elasticIp) {
        DescribeAddressesRequest describeAddressesRequest =
            new DescribeAddressesRequest().withPublicIps(elasticIp);

        try {
            DescribeAddressesResult result =
                ec2Client.describeAddresses(describeAddressesRequest);
            if(null != result) {
                List<Address> addresses = result.getAddresses();
                for(Address address : addresses) {
                    String instanceId = address.getInstanceId();
                    if(null != instanceId && !instanceId.isEmpty()) {
                        log.warn("Shutting down instance with ID {} because " +
                                 "it was associated with elastic IP {}",
                                 instanceId,
                                 elasticIp);
                        stop(instanceId);
                    }
                }
            }
        } catch(Exception e) {
            log.error("Error attempting to stop instance associated with " +
                      "elastic IP {}: {}", elasticIp, e.getMessage());
        }
    }

    private void associateAddress(String elasticIp, String instanceId) {
        log.debug("Associating elastic IP {} with instance {}",
                  elasticIp,
                  instanceId);
        AssociateAddressRequest associateRequest =
            new AssociateAddressRequest(instanceId, elasticIp);

        final int maxTries = 5;
        int tries = 0;
        Exception e = null;
        AssociateAddressResult result = null;
        while (tries++ < maxTries && null == result) {
            try {
                result = ec2Client.associateAddress(associateRequest);
                
            } catch (Exception ex) {
                e = ex;
                sleep(SLEEP_TIME * tries);
            }
        }

        if (null == result && null != e) {
            StringBuilder error = new StringBuilder();
            error.append("Error associating ip address: " + associateRequest);
            log.error(error.toString());
            throw new InstanceStartupException(error.toString(), e);
        }
    }

    private void startError(String instanceId) {
        stop(instanceId);
        String err = "Instance with ID " + instanceId +
            " did not start within " + STARTUP_WAIT_TIME/60000 +
            " minutes. The instance has been shut down.";
        throw new InstanceStartupException(err);
    }

    public boolean waitInstanceRunning(String instanceId, long timeout)
        throws DuracloudInstanceNotAvailableException {
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
    public String getStatus(String providerInstanceId)
        throws DuracloudInstanceNotAvailableException {
        if(providerInstanceId.equals(DuracloudInstance.PLACEHOLDER_PROVIDER_ID)) {
            return InstanceState.STARTING.getValue();
        }
        
        DescribeInstancesResult result = describeInstance(providerInstanceId);
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

    private DescribeInstancesResult describeInstance(String providerInstanceId)
        throws DuracloudInstanceNotAvailableException {
        List<String> instanceIds = getIdList(providerInstanceId);
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.setInstanceIds(instanceIds);

        final int maxTries = 5;
        int tries = 0;
        Exception e = null;
        DescribeInstancesResult result = null;
        while (tries++ < maxTries && null == result) {
            try {
                result = ec2Client.describeInstances(request);

            } catch (Exception ex) {
                e = ex;
                sleep(SLEEP_TIME * tries);
            }
        }

        if (null == result && null != e) {
            StringBuilder error = new StringBuilder();
            error.append("Error describing instance: " + providerInstanceId);
            error.append(e);
            log.error(error.toString());
            throw new DuracloudInstanceNotAvailableException(error.toString(), e);
        }

        return result;
    }

    private List<String> getIdList(String id) {
        List<String> ids = new ArrayList<String>();
        ids.add(id);
        return ids;
    }

    @Override
    public InstanceType getInstanceType(String providerInstanceId)
        throws DuracloudInstanceNotAvailableException {
        
        DescribeInstancesResult result = describeInstance(providerInstanceId);
        try {
            // Allowed Values: pending, running, shutting-down, terminated
            String instanceType =  result.getReservations().iterator().next().getInstances()
                         .iterator().next().getInstanceType();
            
            for (InstanceType it : InstanceType.values()) {
                if (convertDuracloudInstanceTypeToNativeM1(it).equals(instanceType)) {
                    return it;
                }
                if (convertDuracloudInstanceTypeToNativeM3(it).equals(instanceType)) {
                    return it;
                }
            }

            String message = "Unable to get instance type for EC2 instance with ID " +
                providerInstanceId + ": amazon instance type '" + instanceType + "' unknown to DuraCloud MC.";
            throw new DuraCloudRuntimeException(message);

        } catch(Exception e) {
            String message = "Unable to get instance type for EC2 instance with ID " +
                providerInstanceId + " due to error: " + e.getMessage();
            log.error(message, e);
            throw new DuraCloudRuntimeException(message, e);
        }

    }

    protected String convertDuracloudInstanceTypeToNativeM1(InstanceType it) {
        return "m1." + it.name().toLowerCase();
    }

    protected String convertDuracloudInstanceTypeToNativeM3(InstanceType it) {
        return "m3." + it.name().toLowerCase();
    }
}
