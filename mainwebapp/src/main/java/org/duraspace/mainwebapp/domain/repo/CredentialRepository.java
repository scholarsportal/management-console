
package org.duraspace.mainwebapp.domain.repo;

import org.duraspace.common.model.Credential;

public interface CredentialRepository {

    public abstract int findIdFor(Credential cred) throws Exception;

    public abstract int saveCredential(Credential cred) throws Exception;

    public abstract Credential findCredentialById(int credId)
            throws Exception;

}