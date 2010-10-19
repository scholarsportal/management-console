/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.common.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class DuracloudUser implements Identifiable, UserDetails  {

	private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Map<String, List<String>> acctToRoles; // acct-ids --> roles
    
    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonLocked = true;

    private int counter;

    public DuracloudUser(String username,
                         String password,
                         String firstName,
                         String lastName,
                         String email) {
        this(username, password, firstName, lastName, email, 0);
    }

    public DuracloudUser(String username,
                         String password,
                         String firstName,
                         String lastName,
                         String email,
                         int counter) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.counter = counter;
        this.acctToRoles = new HashMap<String,List<String>>();
    }

    
    public List<String> getRolesByAcct(String accountId){
	    	List<String> roles = this.acctToRoles.get(accountId);
	    	if(roles != null){
	    		return roles;
	    	}else{
	    		return new ArrayList<String>(0);
	    	}
    }
    
    /**
     * This method adds the arg acctId to the user's list with the priviledge
     * of "ROLE_USER".
     *
     * @param acctId to add to user
     */
    public void addAccount(String acctId) {
    	
        List<String> roles = this.acctToRoles.get(acctId);
        if(roles == null){
        	roles = new ArrayList<String>(1);
        }
        roles.add(Role.ROLE_USER.name());
        this.acctToRoles.put(acctId, roles);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, List<String>> getAcctToRoles() {
        return acctToRoles;
    }

    public void setAcctToRoles(Map<String, List<String>> acctToRoles) {
        this.acctToRoles = acctToRoles;
    }

    public Integer getCounter() {
        return counter;
    }

    @Override
    public String getId() {
        return getUsername();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DuracloudUser)) {
            return false;
        }

        DuracloudUser that = (DuracloudUser) o;

        if (acctToRoles != null ?
            acctToRoles.size() != (that.getAcctToRoles().size()) :
            that.acctToRoles != null) {
            return false;
        }
        if (email != null ? !email.equals(that.email) : that.email != null) {
            return false;
        }
        if (firstName != null ? !firstName.equals(that.firstName) :
            that.firstName != null) {
            return false;
        }
        if (lastName != null ? !lastName.equals(that.lastName) :
            that.lastName != null) {
            return false;
        }
        if (password != null ? !password.equals(that.password) :
            that.password != null) {
            return false;
        }
        if (username != null ? !username.equals(that.username) :
            that.username != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + getAcctToRoles().size();
        return result;
    }

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	/**
	 * Returns the set of all possible roles a user can play
	 * This method is implemented as part of the UserDetails
	 * interface (<code>UserDetails</code>).
	 * @return 
	 */
	public Collection<GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		authorities.add(new GrantedAuthorityImpl(Role.ROLE_USER.name()));
		for(String acct : this.acctToRoles.keySet()){
			List<String> roles = this.acctToRoles.get(acct);
			if(roles != null){
				for(String role : roles){
					authorities.add(Role.valueOf(role).authority());
				}
			}
		}
		return authorities;
	}

}
