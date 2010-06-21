/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.repo;

import org.duracloud.mainwebapp.domain.model.Authority;

public interface AuthorityRepository {

    void saveAuthority(Authority authority) throws Exception;

}
