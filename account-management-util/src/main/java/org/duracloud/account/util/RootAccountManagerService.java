package org.duracloud.account.util;

import java.util.List;

import org.duracloud.security.domain.SecurityUserBean;



/**
 * An interface for the account management application administrator.
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public interface RootAccountManagerService {

	/**
	 * 
	 * @param filter optional filter on accountid 
	 * @return
	 */
	public List<AccountDetail> listAllAccounts(String filter);

	/**
	 * 
	 * @param filter optional filter on username
	 * @return
	 */
	public List<SecurityUserBean> listAllUsers(String filter);
	
}
