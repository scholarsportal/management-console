
package org.duraspace.mainwebapp.domain.model;

import java.net.URL;

import java.util.Properties;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.ApplicationConfig;
import org.duraspace.common.util.ExceptionUtil;
import org.duraspace.computeprovider.domain.ComputeProviderType;
import org.duraspace.computeprovider.mgmt.ComputeProviderFactory;
import org.duraspace.computeprovider.mgmt.InstanceDescription;
import org.duraspace.computeprovider.mgmt.ComputeProvider;

public class ComputeAcct {

    // THIS CLASS WILL NOT INITIALIZE THE LOGGER FOR SOME REASON ?!?
    // TODO
    //    protected final Logger log = Logger.getLogger(getClass());

    private int id;

    private String namespace;

    private String instanceId;

    private String xmlProps;

    private ComputeProviderType computeProviderType;

    private int computeProviderId;

    private int computeCredentialId;

    private int duraAcctId;

    // This member is loaded by a manager class before functionality available.
    private Credential computeCredential;

    public boolean hasId() {
        return id > 0;
    }

    public InstanceDescription describeRunningInstance() throws Exception {
        InstanceDescription desc = null;
        try {
            desc =
                    getServiceProvider()
                            .describeRunningInstance(computeCredential,
                                                     instanceId,
                                                     xmlProps);
        } catch (Exception e) {
            System.err.println(ExceptionUtil.getStackTraceAsString(e));
            throw e;
        }
        return desc;
    }

    public void startInstance() throws Exception {
        System.err.println("starting instance...");
        try {
            instanceId =
                    getServiceProvider().start(computeCredential, xmlProps);
        } catch (Exception e) {
            System.err.println(ExceptionUtil.getStackTraceAsString(e));
            throw e;
        }
    }

    public void stopInstance() throws Exception {
        try {
            getServiceProvider().stop(computeCredential, instanceId, xmlProps);
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
                                                   xmlProps);
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
                                                             xmlProps);
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
                                                   xmlProps);
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
                                                      xmlProps);

        } catch (Exception e) {
            System.err.println(ExceptionUtil.getStackTraceAsString(e));
            throw e;
        }
        return url.toString();
    }

    public boolean authenticates(int acctId) {
        return id == acctId;
    }

    public boolean hasComputeProviderId() {
        return computeProviderId > 0;
    }

    public boolean hasComputeCredentialId() {
        return computeCredentialId > 0;
    }

    public boolean hasDuraAcctId() {
        return duraAcctId > 0;
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

    public Properties getProperties() {
        Properties p = new Properties();
        try {
            p = ApplicationConfig.getPropsFromXml(getXmlProps());
        } catch (Exception e) {
            System.err.println("Unable to create props from: '" + getXmlProps()
                    + "'");
        }
        return p;
    }

    public String getXmlProps() {
        return xmlProps;
    }

    public void setXmlProps(String xmlProps) {
        this.xmlProps = xmlProps;
    }

    private ComputeProvider getServiceProvider() throws Exception {
        return ComputeProviderFactory.getComputeProvider(computeProviderType);
    }

    public Credential getComputeCredential() {
        return computeCredential;
    }

    public void setComputeCredential(Credential computeCredential) {
        this.computeCredential = computeCredential;
    }

    public ComputeProviderType getComputeProviderType() {
        return computeProviderType;
    }

    public void setComputeProviderType(ComputeProviderType computeProviderType) {
        this.computeProviderType = computeProviderType;
    }

    public void setComputeProviderType(String computeProviderType) {
        this.computeProviderType =
                ComputeProviderType.fromString(computeProviderType);
    }

    public int getComputeProviderId() {
        return computeProviderId;
    }

    public void setComputeProviderId(int computeProviderId) {
        this.computeProviderId = computeProviderId;
    }

    public int getComputeCredentialId() {
        return computeCredentialId;
    }

    public void setComputeCredentialId(int computeCredentialId) {
        this.computeCredentialId = computeCredentialId;
    }

    public int getDuraAcctId() {
        return duraAcctId;
    }

    public void setDuraAcctId(int duraAcctId) {
        this.duraAcctId = duraAcctId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
