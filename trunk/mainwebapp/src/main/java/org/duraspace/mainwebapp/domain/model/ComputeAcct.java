
package org.duraspace.mainwebapp.domain.model;

import java.net.URL;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.ExceptionUtil;
import org.duraspace.serviceprovider.mgmt.ComputeProviderFactory;
import org.duraspace.serviceprovider.mgmt.InstanceDescription;
import org.duraspace.serviceprovider.mgmt.ServiceProvider;
import org.duraspace.serviceprovider.mgmt.ServiceProviderProperties;

public class ComputeAcct {

    // THIS CLASS WILL NOT INITIALIZE THE LOGGER FOR SOME REASON ?!?
    // TODO
    //    protected final Logger log = Logger.getLogger(getClass());

    private String id;

    private String namespace;

    private String computeProviderId;

    private Credential computeCredential;

    private String instanceId;

    private ServiceProviderProperties props;

    public InstanceDescription describeRunningInstance() throws Exception {
        InstanceDescription desc = null;
        try {
            desc =
                    getServiceProvider()
                            .describeRunningInstance(computeCredential,
                                                     instanceId,
                                                     props);
        } catch (Exception e) {
            System.err.println(ExceptionUtil.getStackTraceAsString(e));
            throw e;
        }
        return desc;
    }

    public void startInstance() throws Exception {
        System.err.println("starting instance...");
        try {
            instanceId = getServiceProvider().start(computeCredential, props);
        } catch (Exception e) {
            System.err.println(ExceptionUtil.getStackTraceAsString(e));
            throw e;
        }
    }

    public void stopInstance() throws Exception {
        try {
            getServiceProvider().stop(computeCredential, instanceId, props);
            System.err.println("stopping instance:" + instanceId
                    + " ...removing ID.");
            instanceId = null;
        } catch (Exception e) {
            System.err.println(ExceptionUtil.getStackTraceAsString(e));
            throw e;
        }
    }

    public boolean isInstanceRunning() throws Exception {
        boolean flag = false;
        if (instanceId != null) {
            try {
                flag =
                        getServiceProvider()
                                .isInstanceRunning(computeCredential,
                                                   instanceId,
                                                   props);
            } catch (Exception e) {
                System.err.println(ExceptionUtil.getStackTraceAsString(e));
                throw e;
            }
        }
        return flag;
    }

    public boolean isWebappRunning() throws Exception {
        boolean flag = false;
        if (instanceId != null) {
            try {
                flag =
                        getServiceProvider().isWebappRunning(computeCredential,
                                                             instanceId,
                                                             props);
            } catch (Exception e) {
                System.err.println(ExceptionUtil.getStackTraceAsString(e));
                throw e;
            }
        }
        return flag;
    }

    public boolean isInstanceBooting() throws Exception {
        boolean flag = false;
        if (instanceId != null) {
            try {
                flag =
                        getServiceProvider()
                                .isInstanceBooting(computeCredential,
                                                   instanceId,
                                                   props);
            } catch (Exception e) {
                System.err.println(ExceptionUtil.getStackTraceAsString(e));
                throw e;
            }
        }
        return flag;
    }

    public String getWebappURL() throws Exception {
        URL url = null;
        try {
            url =
                    getServiceProvider().getWebappURL(computeCredential,
                                                      instanceId,
                                                      props);

        } catch (Exception e) {
            System.err.println(ExceptionUtil.getStackTraceAsString(e));
            throw e;
        }
        return url.toString();
    }

    public boolean authenticates(String acctId) {
        return (acctId != null && id.equals(acctId));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ComputeAcct[");
        sb.append("id:" + id);
        sb.append("|providerId:" + computeProviderId);
        sb.append("|instanceId:" + instanceId);
        sb.append("]");
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public ServiceProviderProperties getProps() {
        return props;
    }

    public void setProps(ServiceProviderProperties props) {
        this.props = props;
    }

    public String getComputeProviderId() {
        return computeProviderId;
    }

    public void setComputeProviderId(String computeProviderId) {
        this.computeProviderId = computeProviderId;
    }

    private ServiceProvider getServiceProvider() throws Exception {
        return ComputeProviderFactory.getComputeProvider(computeProviderId);
    }

    public Credential getComputeCredential() {
        return computeCredential;
    }

    public void setComputeCredential(Credential computeCredential) {
        this.computeCredential = computeCredential;
    }

}
