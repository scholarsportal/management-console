package org.duracloud.mainwebapp.control;

import org.duracloud.common.model.Credential;
import org.duracloud.mainwebapp.domain.cmd.DuraAcctWrapper;
import org.duracloud.mainwebapp.domain.model.DuraCloudAcct;
import org.duracloud.mainwebapp.mgmt.DuraCloudAcctManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class LoginController
        extends SimpleFormController {

    protected final Logger log = LoggerFactory.getLogger(LoginController.class);

    private DuraCloudAcctManager duraCloudAcctManager;

    public LoginController() {
        setCommandClass(Credential.class);
        setCommandName("credential");
    }

    @Override
    protected ModelAndView onSubmit(Object command, BindException bindException)
            throws Exception {
        Credential cred = (Credential) command;
        getDuraCloudAcctManager().verifyCredential(cred);

        DuraAcctWrapper wrapper = buildDuraAcctDetails(cred);

        log.info("submitting acct: " + wrapper.getDuraAcct().getAccountName());

        return new ModelAndView(getSuccessView(), "wrapper", wrapper);
    }

    private DuraAcctWrapper buildDuraAcctDetails(Credential duraCred)
            throws Exception {

        DuraCloudAcctManager mgr = getDuraCloudAcctManager();
        DuraCloudAcct duraAcct = mgr.findDuraCloudAccount(duraCred);
        int duraAcctId = duraAcct.getId();
        return DuraAcctWrapper.buildWrapper(mgr, duraAcctId);
    }

    public DuraCloudAcctManager getDuraCloudAcctManager() {
        return duraCloudAcctManager;
    }

    public void setDuraCloudAcctManager(DuraCloudAcctManager duraCloudAcctManager) {
        this.duraCloudAcctManager = duraCloudAcctManager;
    }

}