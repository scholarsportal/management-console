/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.error.UserAlreadyExistsException;
import org.duracloud.account.util.DuracloudUserService;

import java.util.List;
import java.util.Map;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class DuracloudUserServiceImpl implements DuracloudUserService {

    private DuracloudUserRepo userRepo;
    private DuracloudAccountRepo accountRepo;

    public DuracloudUserServiceImpl(DuracloudUserRepo userRepo,
                                    DuracloudAccountRepo accountRepo) {
        this.userRepo = userRepo;
        this.accountRepo = this.accountRepo;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        try {
            userRepo.findById(username);
            return false;

        } catch (DBNotFoundException e) {
            return true;
        }
    }

    @Override
    public String createNewUser(String username,
                                String password,
                                String firstName,
                                String lastName,
                                String email)
        throws DBConcurrentUpdateException, UserAlreadyExistsException {
        DuracloudUser user = new DuracloudUser(username,
                                               password,
                                               firstName,
                                               lastName,
                                               email);
        throwIfUserExists(user);
        userRepo.save(user);
        return user.getId();
    }

    private void throwIfUserExists(DuracloudUser user)
        throws UserAlreadyExistsException {

        if (!isUsernameAvailable(user.getId())) {
            throw new UserAlreadyExistsException(user.getId());
        }
    }

    @Override
    public void addUserToAccount(String acctId, String username)
        throws DBNotFoundException {
        DuracloudUser user = userRepo.findById(username);
        user.addAccount(acctId);
    }

    @Override
    public void removeUserFromAccount(String acctId, String username) {
        // Default method body

    }

    @Override
    public void grantAdminRights(String acctId, String username) {
        // Default method body

    }

    @Override
    public void revokeAdminRights(String acctId, String username) {
        // Default method body

    }

    @Override
    public void sendPasswordReminder(String username) {
        // Default method body

    }

    @Override
    public void changePassword(String username,
                               String oldPassword,
                               String newPassword) {
        // Default method body

    }
}
