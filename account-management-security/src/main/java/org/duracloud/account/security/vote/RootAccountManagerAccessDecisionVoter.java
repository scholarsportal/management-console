/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.security.vote;

import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.RootAccountManagerService;

/**
 * This class votes on calls to the RootAccountManagerService.
 *
 * @author DanielBernstein
 * Date: 1/23/2012
 */
public class RootAccountManagerAccessDecisionVoter extends AccountManagerAccessDecisionVoter {

    public RootAccountManagerAccessDecisionVoter(DuracloudRepoMgr repoMgr) {
        super(repoMgr);
    }

    @Override
    protected Class<?> getTargetService() {
        return RootAccountManagerService.class;
    }

}
