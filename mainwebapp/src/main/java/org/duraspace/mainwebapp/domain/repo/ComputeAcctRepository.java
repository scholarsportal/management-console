
package org.duraspace.mainwebapp.domain.repo;

import java.util.List;

import org.duraspace.mainwebapp.domain.model.ComputeAcct;

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

}
