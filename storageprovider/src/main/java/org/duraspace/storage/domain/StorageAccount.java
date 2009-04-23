package org.duraspace.storage.domain;



/**
 * Contains the information necessary to access a storage
 * provider account.
 *
 * @author Bill Branan
 */
public class StorageAccount {

    private String id = null;
    private String username = null;
    private String password = null;
    private StorageProviderType type = null;

    public StorageAccount(String id,
                          String username,
                          String password,
                          StorageProviderType type) {
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
    public StorageProviderType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(StorageProviderType type) {
        this.type = type;
    }

}
