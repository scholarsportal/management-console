/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.error.DBNotFoundException;

import java.util.Set;

/**
 * @author: Bill Branan
 * Date: Dec 2, 2010
 */
public interface DuracloudUserInvitationRepo extends BaseRepo<UserInvitation> {

    /**
     * This method returns the user invitation which matches the given
     * redemption code
     *
     * @param redemptionCode the unique code used to redeem this invitation
     * @return invitation associated with the given code
     * @throws DBNotFoundException if no item is found
     */
    public UserInvitation findByRedemptionCode(String redemptionCode) throws DBNotFoundException;

    /**
     * This method returns the set of invitations associated with a given
     * DuraCloud account
     *
     * @param id the identifier of the DuraCloud account
     * @return set of outstanding invitations for the given account
     */
    public Set<UserInvitation> findByAccountId(int id);

}
