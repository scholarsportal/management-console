
package org.duraspace.mainwebapp.mgmt;

import java.net.HttpURLConnection;

import java.util.Properties;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.ApplicationConfig;
import org.duraspace.common.util.ExceptionUtil;
import org.duraspace.common.web.NetworkUtil;
import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.CustomerAcct;
import org.duraspace.mainwebapp.domain.repo.ComputeAcctRepository;
import org.duraspace.mainwebapp.domain.repo.CustomerAcctRepository;

public class ComputeManagerImpl
        implements ComputeManager {

    protected final Logger log = Logger.getLogger(getClass());

    private CustomerAcctRepository customerAcctRepository;

    private ComputeAcctRepository computeAcctRepository;

    /**
     * {@inheritDoc}
     */
    public CustomerAcct startComputeInstance(Credential cred) throws Exception {
        // Find customer for customer-credentials.
        CustomerAcct custAcct =
                getCustomerAcctRepository().findCustomerAcct(cred);

        // Find compute-acct for customer.
        String computeAcctId = custAcct.getComputeAcctId();

        startComputeInstance(computeAcctId);

        return custAcct;
    }

    /**
     * {@inheritDoc}
     */
    public ComputeAcct startComputeInstance(String computeAcctId)
            throws Exception {
        ComputeAcct compAcct = findComputeAccount(computeAcctId);

        // Make sure instance is not already running.
        if (compAcct.isInstanceRunning() || compAcct.isInstanceBooting()) {
            throw new Exception("Instance for compute-acct: '" + computeAcctId
                    + "' is already running");
        }

        // Start instance if ok.
        compAcct.startInstance();

        // Persist instance-id to compute-acct.
        getComputeAcctRepository().saveComputeAcct(compAcct);

        return compAcct;
    }

    /**
     * {@inheritDoc}
     */
    public CustomerAcct stopComputeInstance(Credential cred) throws Exception {
        // Find customer for customer-credentials.
        CustomerAcct custAcct =
                getCustomerAcctRepository().findCustomerAcct(cred);

        // Find compute-acct for customer.
        String computeAcctId = custAcct.getComputeAcctId();

        stopComputeInstance(computeAcctId);

        return custAcct;
    }

    /**
     * {@inheritDoc}
     */
    public ComputeAcct stopComputeInstance(String computeAcctId)
            throws Exception {
        ComputeAcct compAcct = findComputeAccount(computeAcctId);

        // Stop instance.
        compAcct.stopInstance();

        // Remove instance-id from compute-acct.
        getComputeAcctRepository().saveComputeAcct(compAcct);

        return compAcct;
    }

    /**
     * {@inheritDoc}
     */
    public ComputeAcct findComputeAccount(Credential cred) throws Exception {
        // Find customer for customer-credentials.
        CustomerAcct custAcct =
                getCustomerAcctRepository().findCustomerAcct(cred);

        // Find compute-acct for customer.
        String computeAcctId = custAcct.getComputeAcctId();
        ComputeAcct compAcct = findComputeAccount(computeAcctId);

        return compAcct;
    }

    /**
     * {@inheritDoc}
     */
    public ComputeAcct findComputeAccount(String computeAcctId)
            throws Exception {
        ComputeAcct compAcct =
                getComputeAcctRepository().findComputeAcct(computeAcctId);
        return compAcct;
    }

    /**
     * {@inheritDoc}
     */
    public ComputeAcct initializeComputeApp(String computeAcctId) {
        ComputeAcct acct = null;
        try {
            acct = findComputeAccount(computeAcctId);

            String baseUrl = getBaseInitializeURL(acct);
            String params = getLocalHostAndPortParams();

            requestInitialization(baseUrl, params);

        } catch (Exception e) {
            log.error("Unable to initialize computeApp: " + computeAcctId);
            log.error(ExceptionUtil.getStackTraceAsString(e));
        }
        return acct;
    }

    private String getBaseInitializeURL(ComputeAcct compAcct) throws Exception {
        String baseUrl = compAcct.getWebappURL();
        if (baseUrl == null) {
            throw new Exception("baseUrl is null.");
        }

        return baseUrl + "/initialize";
    }

    /**
     * @return param string of form: 'host=<host-ip>&port=<port-num>'
     * @throws Exception
     */
    private String getLocalHostAndPortParams() throws Exception {
        Properties props = ApplicationConfig.getMainWebAppProps();
        String port = (String) props.get("port");

        StringBuilder formParams = new StringBuilder("host=");
        formParams.append(NetworkUtil.getCurrentEnvironmentNetworkIp());
        formParams.append("&port=");
        formParams.append(port);

        return formParams.toString();
    }

    private void requestInitialization(String baseUrl, String params)
            throws Exception {

        HttpResponse response =
                new RestHttpHelper().post(baseUrl, params, true);

        if (response != null
                && HttpURLConnection.HTTP_OK == response.getStatusCode()) {
            StringBuilder msg = new StringBuilder();
            msg.append("Error initializing instancewebapp: ");
            msg.append(baseUrl);
            msg.append(params);
            log.error(msg.toString());
        }
    }

    public CustomerAcctRepository getCustomerAcctRepository() {
        return customerAcctRepository;
    }

    public void setCustomerAcctRepository(CustomerAcctRepository customerAcctRepo) {
        this.customerAcctRepository = customerAcctRepo;
    }

    public ComputeAcctRepository getComputeAcctRepository() {
        return computeAcctRepository;
    }

    public void setComputeAcctRepository(ComputeAcctRepository computeAcctRepository) {
        this.computeAcctRepository = computeAcctRepository;
    }

}
