/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

import java.util.Set;

import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.error.DBNotFoundException;

/**
 * @author Daniel Bernstein
 *         Date: Dec 15, 2010
 */
public interface DuracloudUserInvitationRepo extends BaseRepo<UserInvitation> {
    /**
     * This method returns a single user invitation with the given redemptionCode
     *
     * @param the redemption code of the user invitation
     * @return UserInvitation
     * @throws org.duracloud.account.db.error.DBNotFoundException if no item found
     */
    public UserInvitation findByRedemptionCode(String redemptionCode) throws DBNotFoundException;

    /**
     * This method returns the set of invitations for a given account
     * @param the account id
     * @return
     */
    public Set<UserInvitation> findByAccountId(int id);


}
