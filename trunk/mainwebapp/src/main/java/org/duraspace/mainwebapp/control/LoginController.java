
package org.duraspace.mainwebapp.control;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
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
        if (!credentialsValid(cred)) {
            log.error("credentials are invalid for: " + cred);
            throw new Exception("Invalid username/password.");
        }

        DuraSpaceAcct duraAcct = getDuraSpaceAcctManager().findDuraSpaceAccount(cred);
        log.info("submitting acct: " + duraAcct.toString());

        return new ModelAndView(getSuccessView(), "duraAcct", duraAcct);
    }

    private boolean credentialsValid(Credential cred) {
        log.info("checking credential: " + cred.toString());
        return true;
    }

    public DuraSpaceAcctManager getDuraSpaceAcctManager() {
        return duraSpaceAcctManager;
    }

    public void setDuraSpaceAcctManager(DuraSpaceAcctManager duraSpaceAcctManager) {
        this.duraSpaceAcctManager = duraSpaceAcctManager;
    }

}