
package org.duraspace.mainwebapp.domain.repo;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.model.DuraSpaceAcct;

/**
 * <pre>
 * This interface exposes access to the underlying persistence of
 * customer accounts.
 * </pre>
 *
 * @author Andrew Woods
 */
public interface DuraSpaceAcctRepository {

    public DuraSpaceAcct findDuraAcctById(int id) throws Exception;

    public DuraSpaceAcct findDuraAcctByName(String duraAcctName)
            throws Exception;

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
    public DuraSpaceAcct findDuraSpaceAcct(Credential cred) throws Exception;

    public int saveDuraAcct(DuraSpaceAcct duraAcct) throws Exception;

}
