package org.duracloud.common.model;

/**
 * @author Andrew Woods
 *         Date: Mar 25, 2010
 */
public interface Securable {

    /**
     * This method supplies user credentials to the application.
     *
     * @param credential of user
     */
    public void login(Credential credential);

    /**
     * This method clears any previously logged-in credentials.
     */
    public void logout();
}
