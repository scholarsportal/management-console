/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.repo;

import java.util.List;

import org.duracloud.mainwebapp.domain.model.ComputeAcct;

/**
 * This interface provides access to the underlying persistence layer of
 * customer-compute-accounts.
 *
 * @author Andrew Woods
 */
public interface ComputeAcctRepository {

    /**
     * This method returns the compute-account associated with the provided id.
     *
     * @param acctId
     * @return
     * @throws Exception
     *         If no accounts found for provided id.
     */
    public ComputeAcct findComputeAcct(String acctId) throws Exception;

    /**
     * This method persists the provided compute-account-data to the repository.
     *
     * @param acct
     * @throws Exception
     */
    public int saveComputeAcct(ComputeAcct acct) throws Exception;

    public List<ComputeAcct> findComputeAcctsByDuraAcctId(int id) throws Exception;

    public ComputeAcct findComputeAcctById(int id) throws Exception;

    public boolean isComputeNamespaceTaken(String computeAcctNamespace);

}
