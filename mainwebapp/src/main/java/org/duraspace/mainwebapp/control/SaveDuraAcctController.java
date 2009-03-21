
package org.duraspace.mainwebapp.control;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.cmd.flow.DuraAcctCreateWrapper;
import org.duraspace.mainwebapp.domain.model.Address;
import org.duraspace.mainwebapp.domain.model.DuraSpaceAcct;
import org.duraspace.mainwebapp.domain.model.User;
import org.duraspace.mainwebapp.mgmt.DuraSpaceAcctManager;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class SaveDuraAcctController
        implements Action {

    protected final Logger log = Logger.getLogger(getClass());

    private DuraSpaceAcctManager duraSpaceAcctManager;

    public Event execute(RequestContext context) throws Exception {

        DuraAcctCreateWrapper wrapper =
                (DuraAcctCreateWrapper) context.getFlowScope().get("wrapper");

        User user = wrapper.getUser();
        Address addr = wrapper.getAddrShipping();
        String acctName = wrapper.getDuraAcctName();
        Credential duraCred = wrapper.getDuraCred();

        DuraSpaceAcct duraAcct = new DuraSpaceAcct();
        duraAcct.setAccountName(acctName);

        int userId = getDuraSpaceAcctManager().saveUser(user);
        getDuraSpaceAcctManager().saveAddressForUser(addr, userId);
        getDuraSpaceAcctManager().saveCredentialForUser(duraCred, userId);
        getDuraSpaceAcctManager().saveDuraAcctForUser(duraAcct, userId);

        return new Event(this, "success");
    }

    public DuraSpaceAcctManager getDuraSpaceAcctManager() {
        return duraSpaceAcctManager;
    }

    public void setDuraSpaceAcctManager(DuraSpaceAcctManager duraSpaceAcctManager) {
        this.duraSpaceAcctManager = duraSpaceAcctManager;
    }


}
