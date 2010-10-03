package org.duracloud.account.util;


/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public interface AccountManagerService {
	/**
	 * Returns an <code>AccountService</code> interface.
	 * @param accountId
	 * @return
	 * @throws AccountNotFoundException
	 */
	public AccountService getAccount(String accountId)
			throws AccountNotFoundException;

	
	/**
	 * 
	 * @param username
	 * @return true if username is available
	 */
	public boolean isUsernameAvailable(String username);

	/**
	 * 
	 * @param username
	 * @return account id
	 * @throws UsernameAlreadyExistsException
	 */
	public String createAccount(String username)
			throws UsernameAlreadyExistsException;

	/**
	 * 
	 * @param username
	 * @param password
	 * @return an account id
	 * @throws AccountNotFoundException
	 */
	public String lookupAccount(String username, String password)
			throws AccountNotFoundException;
}
