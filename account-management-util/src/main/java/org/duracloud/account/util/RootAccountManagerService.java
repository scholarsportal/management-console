/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import java.util.List;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;

/**
 * An interface for the account management application administrator.
 *
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */

public interface RootAccountManagerService {

    /**
     * @param filter optional filter on accountid
     * @return
     */
    public List<AccountInfo> listAllAccounts(String filter);

    /**
     * @param filter optional filter on username
     * @return
     */
    public List<DuracloudUser> listAllUsers(String filter);

    /**
     * 
     * @param imageId
     * @param version
     * @param description
     */
    public void addDuracloudImage(String imageId,
                                  String version,
                                  String description);

}
