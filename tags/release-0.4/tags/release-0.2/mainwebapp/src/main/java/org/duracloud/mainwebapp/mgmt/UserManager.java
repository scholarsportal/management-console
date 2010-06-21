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
