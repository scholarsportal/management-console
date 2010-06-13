/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.control;

import org.duracloud.common.model.Credential;
import org.duracloud.mainwebapp.domain.cmd.flow.DuraAcctCreateWrapper;
import org.duracloud.mainwebapp.domain.model.Address;
import org.duracloud.mainwebapp.domain.model.DuraCloudAcct;
import org.duracloud.mainwebapp.domain.model.User;
import org.duracloud.mainwebapp.mgmt.DuraCloudAcctManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class SaveDuraAcctController
        implements Action {

    protected final Logger log = LoggerFactory.getLogger(SaveDuraAcctController.class);

    private DuraCloudAcctManager duraCloudAcctManager;

    public Event execute(RequestContext context) throws Exception {

        DuraAcctCreateWrapper wrapper =
                (DuraAcctCreateWrapper) context.getFlowScope().get("wrapper");

        User user = wrapper.getUser();
        Address addr = wrapper.getAddrShipping();
        String acctName = wrapper.getDuraAcctName();
        Credential duraCred = wrapper.getDuraCred();

        DuraCloudAcct duraAcct = new DuraCloudAcct();
        duraAcct.setAccountName(acctName);

        int userId = getDuraCloudAcctManager().saveUser(user);
        getDuraCloudAcctManager().saveAddressForUser(addr, userId);
        getDuraCloudAcctManager().saveCredentialForUser(duraCred, userId);
        getDuraCloudAcctManager().saveDuraAcctForUser(duraAcct, userId);

        return new Event(this, "success");
    }

    public DuraCloudAcctManager getDuraCloudAcctManager() {
        return duraCloudAcctManager;
    }

    public void setDuraCloudAcctManager(DuraCloudAcctManager duraCloudAcctManager) {
        this.duraCloudAcctManager = duraCloudAcctManager;
    }


}
