/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.ec2computeprovider.mgmt;

import java.net.MalformedURLException;
import java.net.URL;

import com.amazonaws.ec2.model.DescribeInstancesResponse;
import com.amazonaws.ec2.model.RunningInstance;

import org.apache.log4j.Logger;

import org.duracloud.common.util.DateUtil;
import org.duracloud.common.util.ExceptionUtil;
import org.duracloud.computeprovider.mgmt.InstanceDescription;
import org.duracloud.computeprovider.mgmt.InstanceState;

public class EC2InstanceDescription
        extends InstanceDescription {

    protected final Logger log = Logger.getLogger(getClass());

    private EC2ComputeProviderProperties props;

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'";

    public EC2InstanceDescription(DescribeInstancesResponse descResp,
                                  EC2ComputeProviderProperties props) {
        this.props = props;
        this.exception = null;
        setMembersFromDescribeResponse(descResp);
    }

    public EC2InstanceDescription(Exception e) {
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

    public void setProps(EC2ComputeProviderProperties props) {
        this.props = props;
    }

}
