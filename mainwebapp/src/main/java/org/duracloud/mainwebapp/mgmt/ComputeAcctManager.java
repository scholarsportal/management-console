/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.mgmt;

import java.util.List;

import org.duracloud.common.model.Credential;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.mainwebapp.domain.model.ComputeAcct;
import org.duracloud.mainwebapp.domain.model.ComputeProvider;

/**
 * This interface encapsulates the navigation of account repositories and the
 * implementation of business logic.
 *
 * @author Andrew Woods
 */
public interface ComputeAcctManager {

    /**
     * <pre>
     * This method starts a compute instance for the customer assicated with
     * the provide Duracloud credentials.
     *
     * Note: The credentials are for Duracloud,
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
     * @param compute
     *        -acct-id
     * @return
     * @throws Exception
     *         If there is an error from the underlying compute-provider or if
     *         an instance is already running.
     */
    public abstract ComputeAcct startComputeInstance(int computeAcctId)
            throws Exception;

    /**
     * This method passes parameters to the compute instance needed for proper
     * functioning.
     *
     * @param computeAcctId
     * @return ComputeAcct that was initialized
     */
    public abstract ComputeAcct initializeComputeApp(int computeAcctId);

    /**
     * This method overrides the above method, stopping a compute instance
     * associated with the provided compute-account-id.
     *
     * @param computeAcctId
     * @return
     * @throws Exception
     */
    public abstract ComputeAcct stopComputeInstance(int computeAcctId)
            throws Exception;

    /**
     * This method overrides the above method, returning the compute-account
     * data associated with the provided compute-account-id.
     *
     * @param computeAcctId
     * @return
     * @throws Exception
     */
    public abstract ComputeAcct findComputeAccountAndLoadCredential(int computeAcctId)
            throws Exception;

    public List<ComputeAcct> findComputeAccountsByDuraAcctId(int duraAcctId)
            throws Exception;

    public abstract int saveComputeAcct(ComputeAcct computeAcct)
            throws Exception;

    public abstract int saveCredentialForComputeAcct(Credential computeAcctCred,
                                                     int computeAcctId)
            throws Exception;

    public abstract ComputeProvider findComputeProviderForComputeAcct(int computeAcctId)
            throws Exception;

    public abstract boolean isComputeNamespaceTaken(String computeAcctNamespace);

    public abstract int findComputeProviderIdByProviderType(ComputeProviderType providerType)
            throws Exception;

    public abstract String getSpacesRequestURL(int computeAcctId)
            throws Exception;

}
