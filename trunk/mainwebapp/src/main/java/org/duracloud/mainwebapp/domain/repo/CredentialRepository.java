/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.repo;

import org.duracloud.common.model.Credential;

public interface CredentialRepository {

    public abstract int findIdFor(Credential cred) throws Exception;

    public abstract int saveCredential(Credential cred) throws Exception;

    public abstract Credential findCredentialById(int credId) throws Exception;

    public abstract Credential findCredentialByUsername(String username)
            throws Exception;

}