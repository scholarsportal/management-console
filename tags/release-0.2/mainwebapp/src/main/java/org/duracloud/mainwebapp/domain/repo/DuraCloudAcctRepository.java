package org.duracloud.mainwebapp.domain.repo;

import org.duracloud.common.model.Credential;
import org.duracloud.mainwebapp.domain.model.DuraCloudAcct;

/**
 * <pre>
 * This interface exposes access to the underlying persistence of
 * customer accounts.
 * </pre>
 *
 * @author Andrew Woods
 */
public interface DuraCloudAcctRepository {

    public DuraCloudAcct findDuraAcctById(int id) throws Exception;

    public DuraCloudAcct findDuraAcctByName(String duraAcctName)
            throws Exception;

    /**
     * This method returns the customer-account belonging to the customer with
     * the provided Duracloud credentials.
     *
     * @param cred
     *        Duracloud credential.
     * @return
     * @throws Exception
     *         If no accounts found for provided credentials.
     */
    public DuraCloudAcct findDuraCloudAcct(Credential cred) throws Exception;

    public int saveDuraAcct(DuraCloudAcct duraAcct) throws Exception;

}
