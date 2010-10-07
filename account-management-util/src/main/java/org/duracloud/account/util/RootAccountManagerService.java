/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util;

import java.util.List;

import org.duracloud.account.util.domain.AccountDetail;
import org.duracloud.security.domain.SecurityUserBean;

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
    public List<AccountDetail> listAllAccounts(String filter);

    /**
     * @param filter optional filter on username
     * @return
     */
    public List<SecurityUserBean> listAllUsers(String filter);

}
