
package org.duraspace.mainwebapp.mgmt;

import org.duraspace.common.model.Credential;

public interface CredentialManager {

    public int findIdFor(Credential duraSpaceCred) throws Exception;

    public int saveCredential(Credential cred) throws Exception;

    public Credential findCredentialById(int credId) throws Exception;

}
