/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.mainwebapp.domain.cmd.StorageAcctWrapper;
import org.duracloud.mainwebapp.domain.cmd.StorageStatusCmd;
import org.duracloud.mainwebapp.domain.model.StorageAcct;
import org.duracloud.mainwebapp.domain.model.StorageProvider;
import org.duracloud.mainwebapp.mgmt.StorageAcctManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class StorageStatusController
        extends AbstractCommandController {

    protected final Logger log = LoggerFactory.getLogger(StorageStatusController.class);

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