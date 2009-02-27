
package org.duraspace.mainwebapp.domain.repo;

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
     * This method returns the total number of compute-accounts stored in the
     * repository.
     *
     * @return
     * @throws Exception
     */
    public int getNumComputeAccts() throws Exception;

    /**
     * This method persists the provided compute-account-data to the repository.
     *
     * @param acct
     * @throws Exception
     */
    public void saveComputeAcct(ComputeAcct acct) throws Exception;

}
