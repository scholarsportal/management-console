/*
 * Copyright (c) 2009-2015 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util;

import org.duracloud.account.db.model.DuracloudMill;
import org.springframework.security.access.annotation.Secured;

/**
 *  Duracloud Mill Configuration Management
 *
 * @author: Daniel Bernstein
 * 
 */
public interface DuracloudMillConfigService {

    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public DuracloudMill get();

    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public void set(String dbHost, 
                    Integer dbPort, 
                    String dbName,
                    String dbUsername, 
                    String dbPassword, 
                    String auditQueue,
                    String auditLogId);

}
