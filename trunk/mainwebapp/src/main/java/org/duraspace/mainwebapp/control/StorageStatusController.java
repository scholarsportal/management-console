
package org.duraspace.mainwebapp.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.duraspace.mainwebapp.domain.cmd.StorageAcctWrapper;
import org.duraspace.mainwebapp.domain.cmd.StorageStatusCmd;
import org.duraspace.mainwebapp.domain.model.StorageAcct;
import org.duraspace.mainwebapp.domain.model.StorageProvider;
import org.duraspace.mainwebapp.mgmt.StorageAcctManager;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class StorageStatusController
        extends AbstractCommandController {

    protected final Logger log = Logger.getLogger(getClass());

    private StorageAcctManager storageManager;

    public StorageStatusController() {
        setCommandClass(StorageStatusCmd.class);
        setCommandName("storageCmd");
    }

    @Override
    protected ModelAndView handle(HttpServletRequest arg0,
                                  HttpServletResponse arg1,
                                  Object command,
                                  BindException arg3) throws Exception {

        StorageStatusCmd params = (StorageStatusCmd) command;

        String cmd = params.getCmd();
        int storageAcctId = params.getStorageAcctIdAsInt();

        log.info("user cmd       : " + cmd);
        log.info("storage acct id: " + storageAcctId);

        StorageAcctWrapper inputToView = executeCommand(cmd, storageAcctId);

        return new ModelAndView("acctUpdate/storageStatus",
                                "input",
                                inputToView);
    }

    private StorageAcctWrapper executeCommand(String cmd, int storageAcctId)
            throws Exception {

        StorageAcctWrapper wrapper = new StorageAcctWrapper();
        wrapper.setStorageProvider(getStorageProvider(storageAcctId));
        wrapper.setStorageAcct(getStorageAcct(storageAcctId));

        return wrapper;
    }

    private StorageProvider getStorageProvider(int storageAcctId)
            throws Exception {
        return getStorageManager()
                .findStorageProviderForStorageAcct(storageAcctId);
    }

    private StorageAcct getStorageAcct(int storageAcctId) throws Exception {
        return getStorageManager()
                .findStorageAccountAndLoadCredential(storageAcctId);
    }

    public StorageAcctManager getStorageManager() {
        return storageManager;
    }

    public void setStorageManager(StorageAcctManager storageManager) {
        this.storageManager = storageManager;
    }

}