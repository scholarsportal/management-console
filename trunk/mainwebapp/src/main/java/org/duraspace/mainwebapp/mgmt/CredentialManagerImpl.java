
package org.duraspace.mainwebapp.mgmt;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.model.Authority;
import org.duraspace.mainwebapp.domain.repo.AuthorityRepository;
import org.duraspace.mainwebapp.domain.repo.CredentialRepository;

public class CredentialManagerImpl
        implements CredentialManager {

    private CredentialRepository credentialRepository;

    private AuthorityRepository authorityRepository;

    public int findIdFor(Credential duraSpaceCred) throws Exception {
        return getCredentialRepository().findIdFor(duraSpaceCred);
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
