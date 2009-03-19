
package org.duraspace.mainwebapp.control;

import java.util.List;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.cmd.DuraAcctWrapper;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.DuraSpaceAcct;
import org.duraspace.mainwebapp.domain.model.StorageAcct;
import org.duraspace.mainwebapp.domain.model.User;
import org.duraspace.mainwebapp.mgmt.DuraSpaceAcctManager;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class LoginController
        extends SimpleFormController {

    protected final Logger log = Logger.getLogger(getClass());

    private DuraSpaceAcctManager duraSpaceAcctManager;

    public LoginController() {
        setCommandClass(Credential.class);
        setCommandName("credential");
    }

    @Override
    protected ModelAndView onSubmit(Object command, BindException bindException)
            throws Exception {
        Credential cred = (Credential) command;
        getDuraSpaceAcctManager().verifyCredential(cred);

        DuraAcctWrapper wrapper = buildDuraAcctDetails(cred);

        log.info("submitting acct: " + wrapper.getDuraAcct().getAccountName());

        return new ModelAndView(getSuccessView(), "wrapper", wrapper);
    }

    private DuraAcctWrapper buildDuraAcctDetails(Credential duraCred)
            throws Exception {
        DuraSpaceAcctManager mgr = getDuraSpaceAcctManager();

        DuraSpaceAcct duraAcct = mgr.findDuraSpaceAccount(duraCred);

        int duraAcctId = duraAcct.getId();
        List<User> users = mgr.findUsers(duraAcctId);
        List<ComputeAcct> computeAccts = mgr.findComputeAccounts(duraAcctId);
        List<StorageAcct> storageAccts = mgr.findStorageAccounts(duraAcctId);

        DuraAcctWrapper wrapper = new DuraAcctWrapper();
        wrapper.setDuraAcct(duraAcct);
        wrapper.setUsers(users);
        wrapper.setComputeAccts(computeAccts);
        wrapper.setStorageAccts(storageAccts);
        return wrapper;
    }

    public DuraSpaceAcctManager getDuraSpaceAcctManager() {
        return duraSpaceAcctManager;
    }

    public void setDuraSpaceAcctManager(DuraSpaceAcctManager duraSpaceAcctManager) {
        this.duraSpaceAcctManager = duraSpaceAcctManager;
    }

}