
package org.duraspace.ec2serviceprovider.mgmt;

import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import com.amazonaws.ec2.AmazonEC2;
import com.amazonaws.ec2.AmazonEC2Client;
import com.amazonaws.ec2.AmazonEC2Config;
import com.amazonaws.ec2.model.DescribeInstancesRequest;
import com.amazonaws.ec2.model.DescribeInstancesResponse;
import com.amazonaws.ec2.model.RunInstancesRequest;
import com.amazonaws.ec2.model.RunInstancesResponse;
import com.amazonaws.ec2.model.TerminateInstancesRequest;

import org.apache.log4j.Logger;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.serviceprovider.mgmt.InstanceDescription;
import org.duraspace.serviceprovider.mgmt.InstanceState;
import org.duraspace.serviceprovider.mgmt.ServiceProvider;
import org.duraspace.serviceprovider.mgmt.ServiceProviderProperties;

public class EC2ServiceProvider
        implements ServiceProvider {

    protected final Logger log = Logger.getLogger(getClass());

    private AmazonEC2 ec2;

    private String accessKeyId = "REPLACE-ME";

    private String secretAccessKey = "REPLACE-ME";

    private EC2ServiceProviderProperties props;

    /**
     * {@inheritDoc}
     *
     * @throws Exception
     */
    public String start(ServiceProviderProperties argProps) throws Exception {
        log.info("start(" + argProps + ")");
        setProps((EC2ServiceProviderProperties) argProps);

        RunInstancesResponse response =
                getEC2().runInstances(createStartRequest());

        return getInstanceId(response);
    }

    private RunInstancesRequest createStartRequest() throws Exception {
        RunInstancesRequest request = new RunInstancesRequest();
        request.setKeyName(getProps().getKeyname());
        request.setImageId(getProps().getImageId());
        request.setMinCount(getProps().getMinInstanceCount());
        request.setMaxCount(getProps().getMaxInstanceCount());

        return request;
    }

    private String getInstanceId(RunInstancesResponse response) {
        return EC2Helper.getFirstRunningInstance(response).getInstanceId();
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
     *
     * @throws Exception
     */
    public InstanceDescription describeRunningInstance(String instanceId) {
        return getDescription(instanceId);
    }

    private InstanceDescription getDescription(String instanceId) {
        InstanceDescription description = null;
        try {
            DescribeInstancesResponse response =
                    requestRunningInstanceDescription(instanceId);
            description = new EC2InstanceDescription(response, getProps());

        } catch (Exception e) {
            log.error(e);
            description = new EC2InstanceDescription(e);
        }

        return description;
    }

    private DescribeInstancesResponse requestRunningInstanceDescription(String instanceId)
            throws Exception {
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
        return (description != null && InstanceState.RUNNING.equals(description
                .getState()));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isWebappRunning(String instanceId) throws Exception {
        InstanceDescription description = getDescription(instanceId);
        return isWebappRunning(description);
    }

    private boolean isWebappRunning(InstanceDescription description) {
        return (isInstanceRunning(description) && pingURL(description.getURL()) == HttpURLConnection.HTTP_OK);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInstanceBooting(String instanceId) throws Exception {
        InstanceDescription description = getDescription(instanceId);
        return isInstanceBooting(description);
    }

    private boolean isInstanceBooting(InstanceDescription description) {
        if (description == null) return false;

        return (InstanceState.PENDING.equals(description.getState()) || (!isWebappRunning(description) && isInstanceRunning(description)));
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

    public URL getWebappURL(String instanceId) throws Exception {
        InstanceDescription description = getDescription(instanceId);

        if (description == null) {
            throw new Exception("No URL for instance: '" + instanceId + "'");
        }
        return description.getURL();
    }

    public AmazonEC2 getEC2() throws Exception {
        if (ec2 == null) {
            log.info("initializing ec2 from props.");
            AmazonEC2Config config = convertConfigurationFrom(getProps());
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

    public EC2ServiceProviderProperties getProps() throws Exception {
        if (this.props == null) {
            InputStream in =
                    this.getClass().getClassLoader()
                            .getResourceAsStream("computeProviderProps.xml");
            if (in == null) {
                throw new Exception("Unable to load resource: 'computeProviderProps.xml'");
            }
            props = new EC2ServiceProviderProperties();
            props.load(in);
        }
        return props;
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
