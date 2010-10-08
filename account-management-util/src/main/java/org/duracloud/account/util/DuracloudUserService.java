/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util;

/**
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public interface DuracloudUserService {

    public boolean isUsernameAvailable(String username);

    public String createNewUser(String username,
                                String password,
                                String firstName,
                                String lastName,
                                String email);

    public void addUserToAccount(String acctId, String username);

    public void removeUserFromAccount(String acctId, String username);

    public void grantAdminRights(String acctId, String username);

    public void revokeAdminRights(String acctId, String username);

    public void sendPasswordReminder(String username);

    public void changePassword(String username,
                               String oldPassword,
                               String newPassword);

}
