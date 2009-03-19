package org.duraspace.storage;

/**
 * Contains the information necessary to access a storage
 * provider account.
 *
 * @author Bill Branan
 */
public class StorageAccount {

    public enum AccountType {S3, Azure, Sun};

    private String id = null;
    private String username = null;
    private String password = null;
    private AccountType type = null;

    public StorageAccount(String id,
                          String username,
                          String password,
                          AccountType type) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the type
     */
    public AccountType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(AccountType type) {
        this.type = type;
    }

}
