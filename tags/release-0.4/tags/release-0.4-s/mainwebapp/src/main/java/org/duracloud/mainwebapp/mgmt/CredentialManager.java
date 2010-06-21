/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.mgmt;

import org.duracloud.common.model.Credential;

public interface CredentialManager {

    public int findIdFor(Credential duraCloudCred) throws Exception;

    public int saveCredential(Credential cred) throws Exception;

    public Credential findCredentialById(int credId) throws Exception;

    public Credential findCredentialByUsername(String username)
            throws Exception;

}
