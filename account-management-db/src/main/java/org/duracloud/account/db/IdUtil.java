/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

/**
 * @author Andrew Woods
 *         Date: Dec 7, 2010
 */
public interface IdUtil {

    public void initialize(DuracloudUserRepo userRepo,
                           DuracloudAccountRepo accountRepo,
                           DuracloudRightsRepo rightsRepo);

    int newAccountId();

    int newUserId();

    int newRightsId();
}
