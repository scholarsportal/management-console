package org.duracloud.account.util;

import java.util.List;

import org.duracloud.storage.domain.StorageProviderType;

/**
 * An interface for manipulating account data.
 * 
 * @author "Daniel Bernstein (dbernstein@duracloud.org)"
 * 
 */
public interface AccountService {
	/**
	 * 
	 * @return
	 */
	public AccountDetail retrieveAccountDetail();
	/**
	 * 
	 * @param fullName
	 * @param email
	 * @param orgName
	 * @param department
	 */
	public void storeAccountInfo(String fullName, String email, String orgName,
			String department);

	/**
	 * 
	 * @return
	 */
	public PaymentInfo retrievePaymentInfo();
	
	/**
	 * 
	 * @param paymentInfo
	 */
	public void storePaymentInfo(PaymentInfo paymentInfo);
	
	/**
	 * 
	 * @param username
	 */
	public void sendPasswordReminder(String username);

	/**
	 * 
	 * @param oldPassword
	 * @param newPassword
	 */
	public void changePassword(String oldPassword, String newPassword);
	
	/**
	 * 
	 * @param subdomain
	 */
	public void storeSubdomain(String subdomain);

	/**
	 * 
	 * @return
	 */
	public String retrieveSubdomain();

	/**
	 * 
	 * @return
	 */
	public List<StorageProviderType> retrieveStorageProviders();
	
	/**
	 * 
	 * @param storageProviderType
	 */
	public void addStorageProvider(StorageProviderType storageProviderType);
	
	/**
	 * 
	 * @param storageProviderType
	 */
	public void removeStorageProvider(StorageProviderType storageProviderType);
	
	/**
	 * 
	 * @param user
	 * @throws UsernameAlreadyExistsException
	 */
	public void addUser(DuracloudUser user) throws UsernameAlreadyExistsException;
	
	/**
	 * 
	 * @param user
	 */
	public void removeUser(String username);
	
	/**
	 * 
	 * @param username
	 */
	public void revokeAdminPrivileges(String username);
	
	/**
	 * 
	 * @param username
	 */
	public void grantAdminPrivileges(String username);
	
	/**
	 * 
	 * @return empty list
	 */
	public List<DuracloudUser> listUsers();
}
