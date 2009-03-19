
package org.duraspace.mainwebapp.mgmt;

import java.net.HttpURLConnection;

import java.util.List;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.ExceptionUtil;
import org.duraspace.common.web.NetworkUtil;
import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.mainwebapp.config.MainWebAppConfig;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.ComputeProvider;
import org.duraspace.mainwebapp.domain.repo.ComputeAcctRepository;
import org.duraspace.mainwebapp.domain.repo.ComputeProviderRepository;

public class ComputeAcctManagerImpl
        implements ComputeAcctManager {

    protected final Logger log = Logger.getLogger(getClass());

    private ComputeAcctRepository computeAcctRepository;

    private ComputeProviderRepository computeProviderRepository;

    private CredentialManager credentialManager;

    /**
     * {@inheritDoc}
     */
    //    public DuraSpaceAcct startComputeInstance(Credential cred) throws Exception {
    //        // Find customer for customer-credentials.
    //        DuraSpaceAcct duraAcct =
    //                getDuraSpaceAcctRepository().findDuraSpaceAcct(cred);
    //
    //        // Find compute-acct for customer.
    //        String computeAcctId = duraAcct.getComputeAcctId();
    //
    //        startComputeInstance(computeAcctId);
    //
    //        return duraAcct;
    //    }
    /**
     * {@inheritDoc}
     */
    public ComputeAcct startComputeInstance(int computeAcctId) throws Exception {
        ComputeAcct compAcct =
                findComputeAccountAndLoadCredential(computeAcctId);

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
    //    public DuraSpaceAcct stopComputeInstance(Credential cred) throws Exception {
    //        // Find customer for customer-credentials.
    //        DuraSpaceAcct custAcct =
    //                getDuraSpaceAcctRepository().findDuraSpaceAcct(cred);
    //
    //        // Find compute-acct for customer.
    //        String computeAcctId = custAcct.getComputeAcctId();
    //
    //        stopComputeInstance(computeAcctId);
    //
    //        return custAcct;
    //    }
    /**
     * {@inheritDoc}
     */
    public ComputeAcct stopComputeInstance(int computeAcctId) throws Exception {
        ComputeAcct compAcct =
                findComputeAccountAndLoadCredential(computeAcctId);

        // Stop instance.
        compAcct.stopInstance();

        // Remove instance-id from compute-acct.
        getComputeAcctRepository().saveComputeAcct(compAcct);

        return compAcct;
    }

    /**
     * {@inheritDoc}
     */
    public List<ComputeAcct> findComputeAccountsByDuraAcctId(int duraAcctId)
            throws Exception {
        return getComputeAcctRepository()
                .findComputeAcctsByDuraAcctId(duraAcctId);
    }

    /**
     * {@inheritDoc}
     */
    //    public ComputeAcct findComputeAccount(Credential duraCred) throws Exception {
    //        // Find customer for customer-credentials.
    //        DuraSpaceAcct duraAcct =
    //                getDuraSpaceAcctRepository().findDuraSpaceAcct(duraCred);
    //
    //        // Find compute-acct for customer.
    //        String computeAcctId = duraAcct.getComputeAcctId();
    //        ComputeAcct compAcct = findComputeAccount(computeAcctId);
    //
    //        return compAcct;
    //    }
    /**
     * {@inheritDoc}
     */
    public ComputeAcct findComputeAccountAndLoadCredential(int computeAcctId)
            throws Exception {
        ComputeAcct computeAcct =
                getComputeAcctRepository().findComputeAcctById(computeAcctId);
        Credential computeCred =
                getCredentialManager().findCredentialById(computeAcct
                        .getComputeCredentialId());

        computeAcct.setComputeCredential(computeCred);

        return computeAcct;
    }

    /**
     * {@inheritDoc}
     */
    public ComputeAcct initializeComputeApp(int computeAcctId) {
        ComputeAcct acct = null;
        try {
            acct = findComputeAccountAndLoadCredential(computeAcctId);

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
        String port = MainWebAppConfig.getPort();

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

    public ComputeProvider findComputeProviderForComputeAcct(int computeAcctId)
            throws Exception {
        ComputeAcct acct =
                getComputeAcctRepository().findComputeAcctById(computeAcctId);
        return getComputeProviderRepository().findComputeProviderById(acct
                .getComputeProviderId());
    }

    public int saveComputeAcct(ComputeAcct computeAcct) throws Exception {
        return getComputeAcctRepository().saveComputeAcct(computeAcct);
    }

    public int saveCredentialForComputeAcct(Credential computeAcctCred,
                                            int computeAcctId) throws Exception {
        int credId = getCredentialManager().saveCredential(computeAcctCred);
        ComputeAcct computeAcct =
                getComputeAcctRepository().findComputeAcctById(computeAcctId);
        computeAcct.setComputeCredentialId(credId);
        saveComputeAcct(computeAcct);
        return credId;

    }

    public ComputeAcctRepository getComputeAcctRepository() {
        return computeAcctRepository;
    }

    public void setComputeAcctRepository(ComputeAcctRepository computeAcctRepository) {
        this.computeAcctRepository = computeAcctRepository;
    }

    public ComputeProviderRepository getComputeProviderRepository() {
        return computeProviderRepository;
    }

    public void setComputeProviderRepository(ComputeProviderRepository computeProviderRepository) {
        this.computeProviderRepository = computeProviderRepository;
    }

    public CredentialManager getCredentialManager() {
        return credentialManager;
    }

    public void setCredentialManager(CredentialManager credentialManager) {
        this.credentialManager = credentialManager;
    }

}
