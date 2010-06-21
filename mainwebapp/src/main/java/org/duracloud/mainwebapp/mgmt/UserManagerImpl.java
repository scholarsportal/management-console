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
import org.duracloud.mainwebapp.domain.repo.AddressRepository;
import org.duracloud.mainwebapp.domain.repo.UserRepository;

public class UserManagerImpl
        implements UserManager {

    private UserRepository userRepository;

    private AddressRepository addressRepository;

    private CredentialManager credentialManager;

    public User findUser(Credential duraCred) throws Exception {
        int duraCredId = -1;
        if (!duraCred.hasId()) {
            duraCredId = getCredentialManager().findIdFor(duraCred);
        }
        return getUserRepository().findUserByDuraCredId(duraCredId);
    }

    public List<User> findUsersByDuraAcctId(int duraAcctId) throws Exception {
        return getUserRepository().findUsersByDuraAcctId(duraAcctId);
    }

    public int saveAddressForUser(Address addr, int userId) throws Exception {
        int addrId = getAddressRepository().saveAddress(addr);
        User user = getUserRepository().findUserById(userId);
        user.setAddrShippingId(addrId);
        getUserRepository().saveUser(user);
        return addrId;
    }

    public int saveCredentialForUser(Credential cred, int userId)
            throws Exception {
        int credId = getCredentialManager().saveCredential(cred);
        User user = getUserRepository().findUserById(userId);
        user.setCredentialId(credId);
        getUserRepository().saveUser(user);
        return credId;
    }

    public void saveDuraAcctIdForUser(int duraAcctId, int userId)
            throws Exception {
        User user = getUserRepository().findUserById(userId);
        user.setDuraAcctId(duraAcctId);
        getUserRepository().saveUser(user);
    }

    public int saveUser(User user) throws Exception {
        return getUserRepository().saveUser(user);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CredentialManager getCredentialManager() {
        return credentialManager;
    }

    public void setCredentialManager(CredentialManager credentialManager) {
        this.credentialManager = credentialManager;
    }

    public AddressRepository getAddressRepository() {
        return addressRepository;
    }

    public void setAddressRepository(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

}
