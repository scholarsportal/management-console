
package org.duraspace.mainwebapp.domain.repo;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.model.CustomerAcct;

/**
 * <pre>
 * This interface exposes access to the underlying persistence of
 * customer accounts.
 * </pre>
 *
 * @author Andrew Woods
 */
public interface CustomerAcctRepository {

    /**
     * This method returns the customer-account belonging to the customer with
     * the provided Duraspace credentials.
     *
     * @param cred
     *        Duraspace credential.
     * @return
     * @throws Exception
     *         If no accounts found for provided credentials.
     */
    public CustomerAcct findCustomerAcct(Credential cred) throws Exception;

    /**
     * This method returns the number of customer accounts found in the
     * repository.
     *
     * @return
     * @throws Exception
     */
    public int getNumCustomerAccts() throws Exception;

    /**
     * This method persists the provided customer-account data.
     *
     * @param acct
     * @throws Exception
     */
    public void saveCustomerAcct(CustomerAcct acct) throws Exception;

}
