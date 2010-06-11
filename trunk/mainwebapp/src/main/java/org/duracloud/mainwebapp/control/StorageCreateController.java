package org.duracloud.mainwebapp.control;

import org.apache.commons.lang.StringUtils;
import org.duracloud.common.model.Credential;
import org.duracloud.mainwebapp.domain.cmd.DuraAcctWrapper;
import org.duracloud.mainwebapp.domain.cmd.StorageCreateCmd;
import org.duracloud.mainwebapp.domain.model.StorageAcct;
import org.duracloud.mainwebapp.mgmt.DuraCloudAcctManager;
import org.duracloud.mainwebapp.mgmt.StorageAcctManager;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class StorageCreateController
        extends SimpleFormController {

    protected final Logger log = LoggerFactory.getLogger(StorageCreateController.class);

    private DuraCloudAcctManager duraCloudAcctManager;

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
                        StorageProviderType.RACKSPACE.toString()};

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
                DuraAcctWrapper.buildWrapper(getDuraCloudAcctManager(),
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

        int acctId = getDuraCloudAcctManager().saveStorageAcct(storageAcct);

        getDuraCloudAcctManager().saveCredentialForStorageAcct(storageCred,
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
        if (getDuraCloudAcctManager()
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

    public DuraCloudAcctManager getDuraCloudAcctManager() {
        return duraCloudAcctManager;
    }

    public void setDuraCloudAcctManager(DuraCloudAcctManager duraCloudAcctManager) {
        this.duraCloudAcctManager = duraCloudAcctManager;
    }

    public StorageAcctManager getStorageAcctManager() {
        return storageAcctManager;
    }

    public void setStorageAcctManager(StorageAcctManager storageAcctManager) {
        this.storageAcctManager = storageAcctManager;
    }

}