package org.duracloud.common.model;

/**
 * @author Andrew Woods
 *         Date: Apr 18, 2010
 */
public class RootUserCredential extends Credential {

    private static final String defaultUsername = "root";
    private static final String defaultPassword = "rpw";

    public RootUserCredential() {
        super(getRootUsername(), getRootPassword());
    }

    static String getRootUsername() {
        String username = System.getProperty("root.username");
        if (null == username) {
            username = defaultUsername;
        }
        return username;
    }

    static String getRootPassword() {
        String password = System.getProperty("root.password");
        if (null == password) {
            password = defaultPassword;
        }
        return password;
    }

}
