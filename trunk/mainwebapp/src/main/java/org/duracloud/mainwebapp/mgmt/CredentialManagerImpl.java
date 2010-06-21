/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.mgmt;

import org.duracloud.common.model.Credential;
import org.duracloud.mainwebapp.domain.model.Authority;
import org.duracloud.mainwebapp.domain.repo.AuthorityRepository;
import org.duracloud.mainwebapp.domain.repo.CredentialRepository;

public class CredentialManagerImpl
        implements CredentialManager {

    private CredentialRepository credentialRepository;

    private AuthorityRepository authorityRepository;

    public int findIdFor(Credential duraCloudCred) throws Exception {
        return getCredentialRepository().findIdFor(duraCloudCred);
    }

    public int saveCredential(Credential cred) throws Exception {
        Authority authority = new Authority();
        authority.setUsername(cred.getUsername());
        authority.setAuthority("");
        getAuthorityRepository().saveAuthority(authority);

        return getCredentialRepository().saveCredential(cred);
    }

    public Credential findCredentialById(int credId) throws Exception {
        return getCredentialRepository().findCredentialById(credId);
    }

    public Credential findCredentialByUsername(String username)
            throws Exception {
        return getCredentialRepository().findCredentialByUsername(username);
    }

    public CredentialRepository getCredentialRepository() {
        return credentialRepository;
    }

    public void setCredentialRepository(CredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    public AuthorityRepository getAuthorityRepository() {
        return authorityRepository;
    }

    public void setAuthorityRepository(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

}
