package org.duracloud.mainwebapp.mgmt;

import org.duracloud.common.model.Credential;

public interface CredentialManager {

    public int findIdFor(Credential duraCloudCred) throws Exception;

    public int saveCredential(Credential cred) throws Exception;

    public Credential findCredentialById(int credId) throws Exception;

    public Credential findCredentialByUsername(String username)
            throws Exception;

}
