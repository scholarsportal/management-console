/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.error.UserAlreadyExistsException;
import org.duracloud.account.util.DuracloudUserService;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class DuracloudUserServiceImpl implements DuracloudUserService {

    private DuracloudUserRepo duracloudUserRepo;

    public DuracloudUserServiceImpl(DuracloudUserRepo duracloudUserRepo) {
        this.duracloudUserRepo = duracloudUserRepo;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        // Default method body
        return false;
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
        duracloudUserRepo.save(user);
        return user.getId();
    }

    private void throwIfUserExists(DuracloudUser user)
        throws UserAlreadyExistsException {
        try {
            duracloudUserRepo.findById(user.getId());
            throw new UserAlreadyExistsException(user.getId());

        } catch (DBNotFoundException e) {
            // do nothing
        }
    }

    @Override
    public void addUserToAccount(String acctId, String username) {
        // Default method body

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
