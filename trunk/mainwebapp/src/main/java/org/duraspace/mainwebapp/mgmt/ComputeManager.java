
package org.duraspace.mainwebapp.mgmt;

import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.Credential;
import org.duraspace.mainwebapp.domain.model.CustomerAcct;

public interface ComputeManager {

    /**
     * This method enforces the following business logic:
     * <p>
     * 1. A customer can have only one compute-instance running per
     * compute-account. 2. After starting a compute-instance, it persists the
     * associated instance-id.
     * </p>
     *
     * @param cred
     * @return
     * @throws Exception
     */
    public abstract CustomerAcct startComputeInstance(Credential cred)
            throws Exception;

    public abstract ComputeAcct startComputeInstance(String computeAcctId)
            throws Exception;

    public abstract CustomerAcct stopComputeInstance(Credential cred)
            throws Exception;

    public abstract ComputeAcct stopComputeInstance(String computeAcctId)
            throws Exception;

    public abstract ComputeAcct findComputeAccount(Credential cred)
            throws Exception;

    public abstract ComputeAcct findComputeAccount(String computeAcctId)
            throws Exception;

}