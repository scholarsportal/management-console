package org.duracloud.ec2typicacomputeprovider.mgmt;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Calendar;
import java.util.List;

import com.xerox.amazonws.ec2.ReservationDescription;
import com.xerox.amazonws.ec2.ReservationDescription.Instance;

import org.apache.log4j.Logger;

import org.duracloud.common.util.ExceptionUtil;
import org.duracloud.computeprovider.mgmt.InstanceDescription;
import org.duracloud.computeprovider.mgmt.InstanceState;

public class EC2InstanceDescription
        extends InstanceDescription {

    protected final Logger log = Logger.getLogger(getClass());

    private EC2ComputeProviderProperties props;

    public EC2InstanceDescription(List<ReservationDescription> descResp,
                                  EC2ComputeProviderProperties props) {
        this.props = props;
        this.exception = null;
        setMembersFromDescribeResponse(descResp);
    }

    public EC2InstanceDescription(Exception e) {
        this.exception = e;
    }

    private void setMembersFromDescribeResponse(List<ReservationDescription> descResp) {
        setMembersFromInstance(EC2Helper.getFirstRunningInstance(descResp));
    }

    private void setMembersFromInstance(Instance instance) {
        this.provider = props.getProvider();

        instanceId = instance.getInstanceId();
        state = InstanceState.fromString(instance.getState());
        Calendar launchCalendar = instance.getLaunchTime();
        if (launchCalendar != null) {
            launchTime = launchCalendar.getTime();
        }
        if (instance.getDnsName() != null) {
            try {
                url =
                        new URL(props.getWebappProtocol(), instance
                                .getDnsName(), props.getWebappPort(), props
                                .getWebappName());
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
