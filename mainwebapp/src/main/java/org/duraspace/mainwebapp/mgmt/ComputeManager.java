
package org.duraspace.mainwebapp.mgmt;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.CustomerAcct;

/**
 * This interface encapsulates the navigation of account repositories and the
 * implementation of business logic.
 *
 * @author Andrew Woods
 */
public interface ComputeManager {

    /**
     * <pre>
     * This method starts a compute instance for the customer assicated with
     * the provide Duraspace credentials.
     *
     * Note: The credentials are for Duraspace,
     *       not the underlying compute-provider
     *
     * It enforces the following business logic:
     *
     * 1. A customer can have only one compute-instance running per
     * compute-account.
     * 2. After starting a compute-instance, it persists the
     * associated instance-id.
     * </pre>
     *
     * @param cred
     *        Duraspace credential
     * @return
     * @throws Exception
     *         If there is an error from the underlying compute-provider or if
     *         an instance is already running.
     */
    public abstract CustomerAcct startComputeInstance(Credential cred)
            throws Exception;

    /**
     * This method overrides the above method, starting a compute instance
     * associated with the provided compute-account-id.
     *
     * @param computeAcctId
     * @return
     * @throws Exception
     */
    public abstract ComputeAcct startComputeInstance(String computeAcctId)
            throws Exception;

    /**
     * This method passes parameters to the compute instance needed for proper
     * functioning.
     *
     * <pre>
     * These parameters include the host and port of the main-duraspace
     * web application.
     * </pre>
     *
     * @param computeAcctId
     * @return ComputeAcct that was initialized
     */
    public abstract ComputeAcct initializeComputeApp(String computeAcctId);

    /**
     * This method terminates the compute instance associated with the provided
     * Duraspace credentials.
     *
     * @param cred
     *        Duraspace credential
     * @return
     * @throws Exception
     *         If error from underlying compute-provider.
     */
    public abstract CustomerAcct stopComputeInstance(Credential cred)
            throws Exception;

    /**
     * This method overrides the above method, stopping a compute instance
     * associated with the provided compute-account-id.
     *
     * @param computeAcctId
     * @return
     * @throws Exception
     */
    public abstract ComputeAcct stopComputeInstance(String computeAcctId)
            throws Exception;

    /**
     * This method returns the compute-account data associated with the provided
     * Duraspace credential.
     *
     * @param cred
     *        Duraspace credential.
     * @return
     * @throws Exception
     */
    public abstract ComputeAcct findComputeAccount(Credential cred)
            throws Exception;

    /**
     * This method overrides the above method, returning the compute-account
     * data associated with the provided compute-account-id.
     *
     * @param computeAcctId
     * @return
     * @throws Exception
     */
    public abstract ComputeAcct findComputeAccount(String computeAcctId)
            throws Exception;

}