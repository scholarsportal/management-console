/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util.domain;

import org.duracloud.common.model.Credential;
import org.duracloud.security.domain.SecurityUserBean;

import java.util.Map;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class DuracloudUser extends SecurityUserBean {
    private String firstName;
    private String lastName;
    private String email;

}
