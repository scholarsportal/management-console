package org.duracloud.security;

import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.User;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
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

    private Map<String, User> usersTable = new HashMap<String, User>();

    public UserDetailsServiceImpl() {
        //FIXME: removed hardcoded users.
        List<String> grants0 = new ArrayList<String>();
        grants0.add("ROLE_ROOT");
        grants0.add("ROLE_ADMIN");
        grants0.add("ROLE_USER");
        SecurityUserBean user0 = new SecurityUserBean("root",
                                                      "rpw",
                                                      true,
                                                      true,
                                                      true,
                                                      true,
                                                      grants0);

        List<String> grants1 = new ArrayList<String>();
        grants1.add("ROLE_ADMIN");
        grants1.add("ROLE_USER");
        SecurityUserBean user1 = new SecurityUserBean("admin",
                                                      "apw",
                                                      true,
                                                      true,
                                                      true,
                                                      true,
                                                      grants1);

        List<String> grants2 = new ArrayList<String>();
        grants2.add("ROLE_USER");
        SecurityUserBean user2 = new SecurityUserBean("user",
                                                      "upw",
                                                      true,
                                                      true,
                                                      true,
                                                      true,
                                                      grants2);

        List<SecurityUserBean> users = new ArrayList<SecurityUserBean>();
        users.add(user0);
        users.add(user1);
        users.add(user2);
        setUsers(users);
    }

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
        UserDetails userDetails = usersTable.get(username);
        if (null == userDetails) {
            throw new UsernameNotFoundException(username);
        }
        return userDetails;
    }

    public void setUsers(List<SecurityUserBean> users) {
        for (SecurityUserBean u : users) {

            List<String> grantBeans = u.getGrantedAuthorities();
            GrantedAuthority[] grants = new GrantedAuthority[grantBeans.size()];
            for (int i = 0; i < grantBeans.size(); ++i) {
                grants[i] = new GrantedAuthorityImpl(grantBeans.get(i));
            }

            User user = new User(u.getUsername(),
                                 u.getPassword(),
                                 u.isEnabled(),
                                 u.isAccountNonExpired(),
                                 u.isCredentialsNonExpired(),
                                 u.isAccountNonLocked(),
                                 grants);

            usersTable.put(u.getUsername(), user);
        }
    }
}
