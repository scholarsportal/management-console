
package org.duraspace.ec2serviceprovider.mgmt;

import java.net.HttpURLConnection;
import java.net.URL;

import com.amazonaws.ec2.AmazonEC2;
import com.amazonaws.ec2.AmazonEC2Client;
import com.amazonaws.ec2.AmazonEC2Config;
import com.amazonaws.ec2.AmazonEC2Exception;
import com.amazonaws.ec2.model.DescribeInstancesRequest;
import com.amazonaws.ec2.model.DescribeInstancesResponse;
import com.amazonaws.ec2.model.RunInstancesRequest;
import com.amazonaws.ec2.model.RunInstancesResponse;
import com.amazonaws.ec2.model.TerminateInstancesRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.serviceprovider.mgmt.InstanceDescription;
import org.duraspace.serviceprovider.mgmt.InstanceState;
import org.duraspace.serviceprovider.mgmt.ServiceProvider;

public class EC2ServiceProvider
        implements ServiceProvider {

    private final Log log = LogFactory.getLog(this.getClass());

    private AmazonEC2 ec2;

    private String accessKeyId;

    private String secretAccessKey;

    private EC2ServiceProviderProperties props;

    /**
     * {@inheritDoc}
     */
    public String start(String imageId) throws AmazonEC2Exception {
        RunInstancesResponse response =
                getEC2().runInstances(createStartRequest());

        return getInstanceId(response);
    }

    private RunInstancesRequest createStartRequest() {
        RunInstancesRequest request = new RunInstancesRequest();
        request.setKeyName(props.getKeyname());
        request.setImageId(props.getImageId());
        request.setMinCount(props.getMinInstanceCount());
        request.setMaxCount(props.getMaxInstanceCount());

        return request;
    }

    private String getInstanceId(RunInstancesResponse response) {
        return EC2Helper.getFirstRunningInstance(response)
                .getInstanceId();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(String instanceId) throws Exception {
        getEC2().terminateInstances(new TerminateInstancesRequest()
                .withInstanceId(instanceId));
    }

    /**
     * {@inheritDoc}
     */
    public InstanceDescription describeRunningInstance(String instanceId) {
        return getDescription(instanceId);
    }

    private InstanceDescription getDescription(String instanceId) {
        InstanceDescription description = null;
        try {
            DescribeInstancesResponse response =
                    requestRunningInstanceDescription(instanceId);
            description = new EC2InstanceDescription(response, props);

        } catch (AmazonEC2Exception e) {
            log.error(e);
            description = new EC2InstanceDescription(e);
        }

        return description;
    }

    private DescribeInstancesResponse requestRunningInstanceDescription(String instanceId)
            throws AmazonEC2Exception {
        return getEC2().describeInstances(new DescribeInstancesRequest()
                .withInstanceId(instanceId));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInstanceRunning(String instanceId) throws Exception {
        InstanceDescription description = getDescription(instanceId);
        return isInstanceRunning(description);
    }

    private boolean isInstanceRunning(InstanceDescription description) {
        return (description == null ? false : InstanceState.RUNNING
                .equals(description.getState()));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isWebappRunning(String instanceId) throws Exception {
        InstanceDescription description = getDescription(instanceId);

        return (!isInstanceRunning(description) ? false : pingURL(description
                .getURL()) == HttpURLConnection.HTTP_OK);
    }

    private int pingURL(URL url) {
        int statusCode = HttpURLConnection.HTTP_NOT_FOUND;
        try {
            RestHttpHelper helper = new RestHttpHelper();
            HttpResponse resp = helper.get(url.toString());
            statusCode = resp.getStatusCode();
        } catch (Exception e) {
            log.warn(e);
        }
        log.info("ping url: " + url + ", status: " + statusCode);

        return statusCode;
    }

    public AmazonEC2 getEC2() {
        if (ec2 == null) {
            AmazonEC2Config config = convertConfigurationFrom(props);
            ec2 = new AmazonEC2Client(accessKeyId, secretAccessKey, config);
        }
        return ec2;
    }

    private AmazonEC2Config convertConfigurationFrom(EC2ServiceProviderProperties props) {
        AmazonEC2Config config = new AmazonEC2Config();
        config.setSignatureMethod(props.getSignatureMethod());
        config.setMaxAsyncThreads(props.getMaxAsyncThreads());

        return config;
    }

    public void setProps(EC2ServiceProviderProperties props) {
        this.props = props;
    }

    public void setEC2(AmazonEC2 ec2) {
        this.ec2 = ec2;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }

}
