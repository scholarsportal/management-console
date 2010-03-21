package org.duracloud.common.model;

/**
 * @author Andrew Woods
 *         Date: Mar 20, 2010
 */
public class SystemUserCredential extends Credential {
    // FIXME: this capability needs to come from a secure source.
    public SystemUserCredential() {
        super("system", "spw");
    }
}
