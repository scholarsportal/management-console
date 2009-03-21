
package org.duraspace.mainwebapp.control;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.cmd.DuraAcctWrapper;
import org.duraspace.mainwebapp.domain.model.DuraSpaceAcct;
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
        return DuraAcctWrapper.buildWrapper(mgr, duraAcctId);
    }

    public DuraSpaceAcctManager getDuraSpaceAcctManager() {
        return duraSpaceAcctManager;
    }

    public void setDuraSpaceAcctManager(DuraSpaceAcctManager duraSpaceAcctManager) {
        this.duraSpaceAcctManager = duraSpaceAcctManager;
    }

}