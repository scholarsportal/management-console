package org.duracloud.security;

import org.springframework.dao.DataAccessException;
import org.springframework.security.userdetails.User;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * This class acts as the repository of username/password/role info for access
 * to this DuraCloud application.
 * The actual info-content is stored in a flat file within DuraCloud itself.
 *
 * @author Andrew Woods
 *         Date: Mar 11, 2010
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    private Map<String, User> usersTable;

    /**
     * This method retrieves UserDetails for all users from a flat file in
     * DuraCloud.
     *
     * @param username of principal for whom details are sought
     * @return UserDetails for arg username
     * @throws UsernameNotFoundException if username not found
     * @throws DataAccessException       if system error while retrieving info
     */
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException, DataAccessException {

        return new User("", "", true, true, true, true, null);
    }

    public void setUsers(List<User> users) {
    }
}
