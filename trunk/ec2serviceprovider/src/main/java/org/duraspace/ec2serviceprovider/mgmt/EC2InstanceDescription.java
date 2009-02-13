
package org.duraspace.ec2serviceprovider.mgmt;

import java.net.MalformedURLException;
import java.net.URL;

import com.amazonaws.ec2.AmazonEC2Exception;
import com.amazonaws.ec2.model.DescribeInstancesResponse;
import com.amazonaws.ec2.model.RunningInstance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.duraspace.common.util.DateUtil;
import org.duraspace.common.util.ExceptionUtil;
import org.duraspace.serviceprovider.mgmt.InstanceDescription;
import org.duraspace.serviceprovider.mgmt.InstanceState;

public class EC2InstanceDescription
        extends InstanceDescription {

    private final Log log = LogFactory.getLog(this.getClass());

    private EC2ServiceProviderProperties props;

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'";

    public EC2InstanceDescription(DescribeInstancesResponse descResp,
                                  EC2ServiceProviderProperties props) {
        this.props = props;
        this.exception = null;
        setMembersFromDescribeResponse(descResp);
    }

    public EC2InstanceDescription(AmazonEC2Exception e) {
        this.exception = e;
    }

    private void setMembersFromDescribeResponse(DescribeInstancesResponse descResp) {
        setMembersFromInstance(EC2Helper
                .getFirstRunningInstance(descResp));
    }

    private void setMembersFromInstance(RunningInstance instance) {
        this.provider = props.getProvider();

        if (instance.isSetInstanceId()) {
            instanceId = instance.getInstanceId();
        }
        if (instance.isSetInstanceState()
                && instance.getInstanceState().isSetName()) {
            state =
                    InstanceState.fromString(instance.getInstanceState()
                            .getName());
        }
        if (instance.isSetLaunchTime()) {
            try {
                launchTime =
                        DateUtil.convertToDate(instance.getLaunchTime(),
                                               DATE_PATTERN);
            } catch (Exception e) {
                log.error(ExceptionUtil.getStackTraceAsString(e));
                // Do nothing.
            }
        }
        if (instance.isSetPublicDnsName()) {
            try {
                url =
                        new URL(props.getWebappProtocol(),
                                instance.getPublicDnsName(),
                                props.getWebappPort(),
                                props.getWebappName());
            } catch (MalformedURLException e) {
                log.error(ExceptionUtil.getStackTraceAsString(e));
                // Do nothing.
            }
        }
    }

    public void setProps(EC2ServiceProviderProperties props) {
        this.props = props;
    }

}
