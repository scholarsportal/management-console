
package org.duraspace.ec2serviceprovider.mgmt;

import com.amazonaws.ec2.AmazonEC2;
import com.amazonaws.ec2.AmazonEC2Client;
import com.amazonaws.ec2.AmazonEC2Config;

public class AmazonEC2WrapperImpl implements EC2Wrapper {

    private AmazonEC2 service = null;

    private String accessKeyId;

    private String secretAccessKey;

    private EC2ServiceProperties props;

    public AmazonEC2 getService() {
        if (service == null) {
            AmazonEC2Config config = convertConfigurationFrom(props);
            service = new AmazonEC2Client(accessKeyId, secretAccessKey, config);
        }
        return service;
    }

    private AmazonEC2Config convertConfigurationFrom(EC2ServiceProperties props) {
        AmazonEC2Config config = new AmazonEC2Config();
        config.setSignatureMethod(props.getSignatureMethod());
        config.setMaxAsyncThreads(props.getMaxAsyncThreads());

        return config;
    }


    public void setProps(EC2ServiceProperties props) {
        this.props = props;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }

}
