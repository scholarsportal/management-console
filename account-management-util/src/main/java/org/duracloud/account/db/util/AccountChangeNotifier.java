package org.duracloud.account.db.util;

public interface AccountChangeNotifier {

    /**
     * Notifies listeners that an account had changed.
     * @param account
     */
    public void accountChanged(String account);
    
    /**
     * Notifies listeners that one or more storage providers associated with an account has changed.
     * @param account
     */
    public void storageProvidersChanged(String account);

    /**
     * Notifies listeners that the set of users associated with an account have changed
     * @param account
     */
    public void userStoreChanged(String account);


    /**
     * Notifies listeners that a root user's status has changed:  a root user was added, removed, or changed.
     */
    public void rootUsersChanged();
}
