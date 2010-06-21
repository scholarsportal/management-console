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
import org.duracloud.mainwebapp.domain.model.Address;
import org.duracloud.mainwebapp.domain.model.User;

public interface UserManager {

    public User findUser(Credential duraCred) throws Exception;

    public int saveUser(User user) throws Exception;

    public int saveAddressForUser(Address addr, int userId) throws Exception;

    public int saveCredentialForUser(Credential cred, int userId)
            throws Exception;

    public void saveDuraAcctIdForUser(int duraAcctId, int userId)
            throws Exception;

    public List<User> findUsersByDuraAcctId(int duraAcctId) throws Exception;

}
