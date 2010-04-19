package org.duracloud.security;

import org.springframework.security.userdetails.UserDetailsService;
import org.duracloud.security.domain.SecurityUserBean;

import java.util.List;

/**
 * @author Andrew Woods
 *         Date: Apr 15, 2010
 */
public interface DuracloudUserDetailsService extends UserDetailsService {
    
    public void setUsers(List<SecurityUserBean> users);
}
