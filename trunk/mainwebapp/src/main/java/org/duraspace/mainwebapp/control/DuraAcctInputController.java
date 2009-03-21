
package org.duraspace.mainwebapp.control;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.cmd.flow.DuraAcctCreateWrapper;
import org.duraspace.mainwebapp.domain.model.DuraSpaceAcct;
import org.duraspace.mainwebapp.mgmt.CredentialManager;
import org.duraspace.mainwebapp.mgmt.DuraSpaceAcctManager;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class DuraAcctInputController
        implements Action {

    protected final Logger log = Logger.getLogger(getClass());

    private DuraSpaceAcctManager duraSpaceAcctManager;

    private CredentialManager credentialManager;

    public Event execute(RequestContext context) throws Exception {

        String acctName = context.getRequestParameters().get("acctName");
        String username = context.getRequestParameters().get("username");
        String password = context.getRequestParameters().get("password");

        log.info("acctName: " + acctName);
        log.info("username: " + username);
        log.info("password: " + password);

        validateInput(acctName, username, password);
        validateUsername(username);
        validateAccountName(acctName);

        Credential duraCred = new Credential(username, password);

        DuraAcctCreateWrapper wrapper =
                (DuraAcctCreateWrapper) context.getFlowScope().get("wrapper");

        wrapper.setDuraCred(duraCred);
        wrapper.setDuraAcctName(acctName);

        return new Event(this, "success");
    }

    private void validateInput(String acctName, String username, String password)
            throws Exception {
        StringBuilder msg = new StringBuilder();
        if (StringUtils.isBlank(acctName)) {
            msg.append("Account name can not be blank.\n");
        }
        if (StringUtils.isBlank(username)) {
            msg.append("User name can not be blank.\n");
        }
        if (StringUtils.isBlank(password)) {
            msg.append("Password can not be blank.\n");
        }
        if (!StringUtils.isEmpty(msg.toString())) {
            throw new Exception(msg.toString());
        }
    }

    private void validateUsername(String username) throws Exception {
        Credential cred = null;
        try {
            cred = getCredentialManager().findCredentialByUsername(username);
        } catch (Exception e) {
            // Throwing means username does not already exist.
        }

        if (cred != null) {
            throw new Exception("Username already taken: " + username);
        }

    }

    private void validateAccountName(String acctName) throws Exception {
        DuraSpaceAcct acct = null;
        try {
            acct =
                    getDuraSpaceAcctManager()
                            .findDuraSpaceAccountByAcctName(acctName);
        } catch (Exception e) {
            // Throwing means acctName does not already exist.
        }
        if (acct != null) {
            throw new Exception("Account name already taken: " + acctName);
        }
    }

    public DuraSpaceAcctManager getDuraSpaceAcctManager() {
        return duraSpaceAcctManager;
    }

    public void setDuraSpaceAcctManager(DuraSpaceAcctManager duraSpaceAcctManager) {
        this.duraSpaceAcctManager = duraSpaceAcctManager;
    }

    public CredentialManager getCredentialManager() {
        return credentialManager;
    }

    public void setCredentialManager(CredentialManager credentialManager) {
        this.credentialManager = credentialManager;
    }

}
