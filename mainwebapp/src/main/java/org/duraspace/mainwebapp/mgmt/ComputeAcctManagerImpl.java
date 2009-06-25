
package org.duraspace.mainwebapp.mgmt;

import java.io.Writer;

import java.net.HttpURLConnection;

import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.EncryptionUtil;
import org.duraspace.common.util.ExceptionUtil;
import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.computeprovider.domain.ComputeProviderType;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.ComputeProvider;
import org.duraspace.mainwebapp.domain.model.StorageAcct;
import org.duraspace.mainwebapp.domain.repo.ComputeAcctRepository;
import org.duraspace.mainwebapp.domain.repo.ComputeProviderRepository;

public class ComputeAcctManagerImpl
        implements ComputeAcctManager {

    protected final Logger log = Logger.getLogger(getClass());

    private ComputeAcctRepository computeAcctRepository;

    private ComputeProviderRepository computeProviderRepository;

    private CredentialManager credentialManager;

    private DuraSpaceAcctManager duraSpaceAcctManager;

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

            int duraAcctId = getComputeAcctRepository().
                findComputeAcctById(computeAcctId).getDuraAcctId();
            String initData = getStorageAccounts(duraAcctId);

            requestInitialization(baseUrl, initData);
        } catch (Exception e) {
            log.error("Unable to initialize computeApp: " + computeAcctId);
            log.debug(ExceptionUtil.getStackTraceAsString(e));
        }
        return acct;
    }

    private String getBaseInitializeURL(ComputeAcct compAcct) throws Exception {
        String baseUrl = compAcct.getWebappURL();
        if (baseUrl == null) {
            throw new Exception("baseUrl is null.");
        }

        return baseUrl + "/stores";
    }

    /**
     * Provides an XML listing of all storage provider account subscriptions
     * for a given DuraCloud account.
     */
    protected String getStorageAccounts(int duraAcctId)
            throws Exception {
        List<StorageAcct> accts =
            duraSpaceAcctManager.findStorageAccounts(duraAcctId);

        EncryptionUtil encryptUtil = new EncryptionUtil();
        for(StorageAcct acct : accts) {
            Credential credential = acct.getStorageProviderCredential();
            if(credential != null) {
                String encUsername = encryptUtil.encrypt(credential.getUsername());
                String encPassword = encryptUtil.encrypt(credential.getPassword());
                credential.setUsername(encUsername);
                credential.setPassword(encPassword);
            }
        }

        return getXStream().toXML(accts);
    }

    private XStream getXStream() {
        XStream xstream = new XStream(new DomDriver() {
            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new CompactWriter(out);
            }
        });
        xstream.alias("storageProviderAccounts", List.class);
        xstream.alias("storageAcct", StorageAcct.class);
        xstream.omitField(Credential.class, "id");
        xstream.useAttributeFor("ownerId", String.class);
        xstream.useAttributeFor("isPrimary", int.class);
        return xstream;
    }

    private void requestInitialization(String baseUrl, String params)
            throws Exception {
        boolean formData = true;
        HttpResponse response =
                new RestHttpHelper().post(baseUrl, params, formData);

        if (response != null
                && HttpURLConnection.HTTP_OK == response.getStatusCode()) {
            StringBuilder msg = new StringBuilder();
            msg.append("Error initializing customerwebapp: ");
            msg.append(baseUrl);
            msg.append(params);
            log.error(msg.toString());
        }
    }

    public String getSpacesRequestURL(int computeAcctId) throws Exception {
        String url = new String();
        try {
            ComputeAcct acct =
                    findComputeAccountAndLoadCredential(computeAcctId);
            url = getBaseSpacesURLWithDuraAcctId(acct);
            log.info("spaces request url: " + url);
        } catch (Exception e) {
            log.error("Error getting spaces computeApp: " + computeAcctId);
            log.debug(ExceptionUtil.getStackTraceAsString(e));
        }
        return url;
    }

    private String getBaseSpacesURLWithDuraAcctId(ComputeAcct compAcct)
            throws Exception {
        String baseUrl = compAcct.getWebappURL();
        if (baseUrl == null) {
            throw new Exception("baseUrl is null.");
        }

        return baseUrl + "/spaces.htm?accountId=" + compAcct.getDuraAcctId();
    }

    public ComputeProvider findComputeProviderForComputeAcct(int computeAcctId)
            throws Exception {
        ComputeAcct acct =
                getComputeAcctRepository().findComputeAcctById(computeAcctId);
        return getComputeProviderRepository().findComputeProviderById(acct
                .getComputeProviderId());
    }

    public int findComputeProviderIdByProviderType(ComputeProviderType providerType)
            throws Exception {
        return getComputeProviderRepository()
                .findComputeProviderIdByProviderType(providerType);
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

    public boolean isComputeNamespaceTaken(String computeAcctNamespace) {
        return getComputeAcctRepository()
                .isComputeNamespaceTaken(computeAcctNamespace);
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

    public DuraSpaceAcctManager getDuraSpaceAcctManager() {
        return duraSpaceAcctManager;
    }

    public void setDuraSpaceAcctManager(DuraSpaceAcctManager duraSpaceAcctManager) {
        this.duraSpaceAcctManager = duraSpaceAcctManager;
    }

}
