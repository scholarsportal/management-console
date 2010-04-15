package org.duracloud.security;

import java.util.List;

/**
 * @author Andrew Woods
 *         Date: Mar 28, 2010
 */
public class SecurityUserBean {
    private String username;
    private String password;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;
    private List<String> grantedAuthorities;

    public static final String SCHEMA_VERSION = "0.2";

    public SecurityUserBean(String username,
                            String password,
                            List<String> grantedAuthorities) {
        this(username, password, true, true, true, true, grantedAuthorities);
    }

    public SecurityUserBean(String username,
                            String password,
                            boolean enabled,
                            boolean accountNonExpired,
                            boolean credentialsNonExpired,
                            boolean accountNonLocked,
                            List<String> grantedAuthorities) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.grantedAuthorities = grantedAuthorities;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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

    public List<String> getGrantedAuthorities() {
        return grantedAuthorities;
    }
}
