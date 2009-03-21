
package org.duraspace.mainwebapp.control;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.cmd.DuraAcctWrapper;
import org.duraspace.mainwebapp.domain.cmd.StorageCreateCmd;
import org.duraspace.mainwebapp.domain.model.StorageAcct;
import org.duraspace.mainwebapp.mgmt.DuraSpaceAcctManager;
import org.duraspace.mainwebapp.mgmt.StorageAcctManager;
import org.duraspace.storage.domain.StorageProviderType;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class StorageCreateController
        extends SimpleFormController {

    protected final Logger log = Logger.getLogger(getClass());

    private DuraSpaceAcctManager duraSpaceAcctManager;

    private StorageAcctManager storageAcctManager;

    public StorageCreateController() {
        setCommandClass(StorageCreateCmd.class);
        setCommandName("storageCreateCmd");
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request)
            throws Exception {
        StorageCreateCmd cmd =
                (StorageCreateCmd) super.formBackingObject(request);
        cmd.setStorageCred(new Credential());
        return cmd;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Map referenceData(HttpServletRequest request) throws Exception {
        Map<String, String[]> providers = new HashMap<String, String[]>();
        String[] storageProviderList =
                {StorageProviderType.AMAZON_S3.toString(),
                        StorageProviderType.MICROSOFT_AZURE.toString(),
                        StorageProviderType.SUN.toString()};

        providers.put("storageProviders", storageProviderList);
        return providers;
    }

    @Override
    protected ModelAndView onSubmit(Object argCommand,
                                    BindException bindException)
            throws Exception {
        StorageCreateCmd command = (StorageCreateCmd) argCommand;

        int duraAcctId = command.getDuraAcctId();
        Credential storageCred = command.getStorageCred();
        String storageAcctNamespace = command.getStorageAcctNamespace();
        String providerType = command.getStorageProviderType();
        String cmd = command.getCmd();
        int isPrimary = command.getIsPrimary();

        log.info("duraAcctId  : '" + duraAcctId + "'");
        log.info("storageCred : '" + storageCred + "'");
        log.info("namespace   : '" + storageAcctNamespace + "'");
        log.info("providerType:'" + providerType + "'");
        log.info("cmd         :'" + cmd + "'");
        log.info("isPrimary   :'" + isPrimary + "'");

        if ("Create".equalsIgnoreCase(cmd)) {
            log.info("Attempt create storage acct, duraAcctId: " + duraAcctId);
            validateInput(duraAcctId,
                          storageAcctNamespace,
                          storageCred,
                          providerType);

            createAndSaveStorageAcct(duraAcctId,
                                     storageAcctNamespace,
                                     providerType,
                                     storageCred,
                                     isPrimary);
        }

        DuraAcctWrapper wrapper =
                DuraAcctWrapper.buildWrapper(getDuraSpaceAcctManager(),
                                             duraAcctId);

        return new ModelAndView(getSuccessView(), "wrapper", wrapper);
    }

    private void createAndSaveStorageAcct(int duraAcctId,
                                          String storageAcctNamespace,
                                          String providerType,
                                          Credential storageCred,
                                          int isPrimary) throws Exception {

        int storageProviderId =
                getStorageAcctManager()
                        .findStorageProviderIdByProviderType(StorageProviderType
                                .fromString(providerType));

        StorageAcct storageAcct = new StorageAcct();
        storageAcct.setDuraAcctId(duraAcctId);
        storageAcct.setStorageProviderType(providerType);
        storageAcct.setStorageProviderId(storageProviderId);
        storageAcct.setNamespace(storageAcctNamespace);
        storageAcct.setPrimary(isPrimary);

        int acctId = getDuraSpaceAcctManager().saveStorageAcct(storageAcct);

        getDuraSpaceAcctManager().saveCredentialForStorageAcct(storageCred,
                                                               acctId);

    }

    private void validateInput(int duraAcctId,
                               String storageAcctNamespace,
                               Credential storageCred,
                               String providerType) throws Exception {
        if (duraAcctId < 1) {
            throw new Exception("Invalid duraAcctId: " + duraAcctId);
        }
        if (StringUtils.isBlank(storageAcctNamespace)) {
            throw new Exception("Account namespace must not be blank.");
        }
        if (getDuraSpaceAcctManager()
                .isStorageNamespaceTaken(storageAcctNamespace)) {
            throw new Exception("Namespace is taken: " + storageAcctNamespace);
        }
        if (storageCred == null
                || StringUtils.isBlank(storageCred.getUsername())
                || StringUtils.isBlank(storageCred.getPassword())) {
            throw new Exception("Invalid credential: '" + storageCred + "'");
        }
        if (StorageProviderType.UNKNOWN.equals(StorageProviderType
                .fromString(providerType))) {
            throw new Exception("Invalid storageProviderType: '" + providerType
                    + "'");
        }
    }

    public DuraSpaceAcctManager getDuraSpaceAcctManager() {
        return duraSpaceAcctManager;
    }

    public void setDuraSpaceAcctManager(DuraSpaceAcctManager duraSpaceAcctManager) {
        this.duraSpaceAcctManager = duraSpaceAcctManager;
    }

    public StorageAcctManager getStorageAcctManager() {
        return storageAcctManager;
    }

    public void setStorageAcctManager(StorageAcctManager storageAcctManager) {
        this.storageAcctManager = storageAcctManager;
    }

}