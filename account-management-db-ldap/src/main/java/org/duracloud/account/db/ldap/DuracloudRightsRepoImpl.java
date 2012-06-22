/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.ldap;

import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.ldap.converter.DomainConverter;
import org.duracloud.account.db.ldap.converter.DuracloudRightsConverter;
import org.duracloud.account.db.ldap.domain.LdapRdn;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;

import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.duracloud.account.db.ldap.domain.LdapAttribute.ACCOUNT;
import static org.duracloud.account.db.ldap.domain.LdapAttribute.OBJECT_CLASS;
import static org.duracloud.account.db.ldap.domain.LdapAttribute.ROLE_OCCUPANT;
import static org.duracloud.account.db.ldap.domain.LdapAttribute.UNIQUE_ID;
import static org.duracloud.account.db.ldap.domain.LdapObjectClass.RIGHTS;
import static org.duracloud.account.db.ldap.domain.LdapRdn.ACCOUNT_OU;
import static org.duracloud.account.db.ldap.domain.LdapRdn.PEOPLE_OU;

/**
 * This class manages the persistence of DuracloudRights.
 *
 * @author Andrew Woods
 *         Date: Jun 18, 2012
 */
public class DuracloudRightsRepoImpl extends BaseDuracloudRepoImpl implements DuracloudRightsRepo {

    private static final String BASE_OU = LdapRdn.RIGHTS_OU.toString();
    protected static final int WILDCARD_ACCT_ID = 0;

    private final DomainConverter<AccountRights> converter;

    public DuracloudRightsRepoImpl(LdapTemplate ldapTemplate) {
        this(ldapTemplate, null);
    }

    public DuracloudRightsRepoImpl(LdapTemplate ldapTemplate,
                                   DomainConverter<AccountRights> converter) {
        super(ldapTemplate, BASE_OU);

        this.log = LoggerFactory.getLogger(DuracloudRightsRepoImpl.class);

        if (null == converter) {
            converter = new DuracloudRightsConverter(getBaseDn());
        }
        this.converter = converter;
    }

    @Override
    public AccountRights findById(int id) throws DBNotFoundException {
        StringBuilder dn = new StringBuilder();
        dn.append(UNIQUE_ID);
        dn.append("=");
        dn.append(id);
        dn.append(",");
        dn.append(BASE_OU);

        try {
            return (AccountRights) ldapTemplate.lookup(dn.toString(),
                                                       converter);

        } catch (NameNotFoundException e) {
            throw new DBNotFoundException("No items found for dn: " + dn);
        }
    }

    @Override
    public Set<AccountRights> findByUserId(int userId) {
        StringBuilder filter = new StringBuilder();
        filter.append(ROLE_OCCUPANT);
        filter.append("=");
        filter.append(UNIQUE_ID);
        filter.append("=");
        filter.append(userId);
        filter.append(",");
        filter.append(PEOPLE_OU);
        filter.append(",");
        filter.append(getBaseDn());

        return search(filter.toString());
    }

    @Override
    public Set<AccountRights> findByAccountId(int acctId) {
        Set<AccountRights> acctRights = doFindByAccountId(acctId);
        Set<AccountRights> rootRights = findRootAccountRights(acctId);

        // Add user rights to root rights list
        for (AccountRights acctRight : acctRights) {
            if (!inRootRights(acctRight, rootRights)) {
                rootRights.add(acctRight);
            }
        }

        return rootRights;
    }

    private boolean inRootRights(AccountRights userRight,
                                 Set<AccountRights> rootRights) {
        for (AccountRights rootRight : rootRights) {
            if (userRight.getUserId() == rootRight.getUserId() &&
                userRight.getAccountId() == rootRight.getAccountId()) {
                return true;
            }
        }
        return false;
    }

    private Set<AccountRights> findRootAccountRights(int acctId) {
        Set<AccountRights> rootAccountRights = new HashSet<AccountRights>();

        StringBuilder filter = new StringBuilder();
        filter.append(ACCOUNT);
        filter.append("=");
        filter.append(UNIQUE_ID);
        filter.append("=");
        filter.append(WILDCARD_ACCT_ID);
        filter.append(",");
        filter.append(ACCOUNT_OU);
        filter.append(",");
        filter.append(getBaseDn());

        Set<AccountRights> rights = search(filter.toString());
        for (AccountRights right : rights) {
            if (right.getRoles().contains(Role.ROLE_ROOT)) {
                rootAccountRights.add(new AccountRights(right.getId(),
                                                        acctId,
                                                        right.getUserId(),
                                                        right.getRoles()));

            } else {
                log.info("Unexpected rights in wildcard acct member: " + right);
            }
        }

        return rootAccountRights;
    }

    @Override
    public Set<AccountRights> findByAccountIdSkipRoot(int accountId) {
        return doFindByAccountId(accountId);
    }

    private Set<AccountRights> doFindByAccountId(int acctId) {
        StringBuilder filter = new StringBuilder();
        filter.append(ACCOUNT);
        filter.append("=");
        filter.append(UNIQUE_ID);
        filter.append("=");
        filter.append(acctId);
        filter.append(",");
        filter.append(ACCOUNT_OU);
        filter.append(",");
        filter.append(getBaseDn());

        return search(filter.toString());
    }

    private Set<AccountRights> search(String filter) {
        List<AccountRights> rightsList = null;
        try {
            rightsList = ldapTemplate.search(BASE_OU, filter, converter);

        } catch (NameNotFoundException e) {
            log.info("No items found for filter: {}", filter, e);
        }

        if (null == rightsList || rightsList.size() == 0) {
            rightsList = new ArrayList<AccountRights>();
        }

        Set<AccountRights> rights = new HashSet<AccountRights>();
        for (AccountRights right : rightsList) {
            rights.add(right);
        }

        return rights;
    }

    @Override
    public AccountRights findByAccountIdAndUserId(int accountId, int userId)
        throws DBNotFoundException {
        StringBuilder acctDn = new StringBuilder();
        acctDn.append(UNIQUE_ID);
        acctDn.append("=");
        acctDn.append(accountId);
        acctDn.append(",");
        acctDn.append(ACCOUNT_OU);
        acctDn.append(",");
        acctDn.append(getBaseDn());

        StringBuilder userDn = new StringBuilder();
        userDn.append(UNIQUE_ID);
        userDn.append("=");
        userDn.append(userId);
        userDn.append(",");
        userDn.append(PEOPLE_OU);
        userDn.append(",");
        userDn.append(getBaseDn());

        Filter acctFilter = new EqualsFilter(ACCOUNT.toString(),
                                             acctDn.toString());
        Filter userFilter = new EqualsFilter(ROLE_OCCUPANT.toString(),
                                             userDn.toString());
        Filter filter = new AndFilter().and(acctFilter).and(userFilter);

        AccountRights rootRight = null;
        AccountRights right = null;
        try {
            // find item.
            right = (AccountRights) ldapTemplate.searchForObject(BASE_OU,
                                                                 filter.encode(),
                                                                 converter);
        } catch (EmptyResultDataAccessException e) {
            rootRight = getRightsIfUserIsRoot(accountId, userId, e);
        } catch (IncorrectResultSizeDataAccessException e) {
            rootRight = getRightsIfUserIsRoot(accountId, userId, e);
        }

        if (null != right) {
            return right;

        } else {
            return rootRight;
        }
    }

    private AccountRights getRightsIfUserIsRoot(int accountId,
                                                int userId,
                                                Exception e)
        throws DBNotFoundException {
        AccountRights rootRight = null;

        // item not found, does account exist?
        Set<AccountRights> rights = doFindByAccountId(accountId);
        if (rights.size() > 0) { // account exists
            for (AccountRights root : getRootAccountRights(accountId)) {
                // was the target user a root user?
                if (root.getUserId() == userId) {
                    rootRight = new AccountRights(root.getId(),
                                                  accountId,
                                                  root.getUserId(),
                                                  root.getRoles(),
                                                  root.getCounter());
                }
            }
        }

        // item not found and not root.
        if (null == rootRight) {
            throw new DBNotFoundException(e);
        }
        return rootRight;
    }

    /**
     * This method returns AccountRights for each root user and sets the
     * rights.acctId to the arg accountId.
     *
     * @param accountId with which returned Roots will be associated
     * @return Set of AccountRights containing Roots
     */
    private Set<AccountRights> getRootAccountRights(int accountId) {
        Set<AccountRights> rights = null;
        try {
            rights = doFindByAccountId(WILDCARD_ACCT_ID);

        } catch (Exception e) {
            // do nothing.
        }

        Set<AccountRights> rootAccountRights = new HashSet<AccountRights>();
        if (null == rights) {
            return rootAccountRights;
        }

        for (AccountRights r : rights) {
            if (r.getRoles().contains(Role.ROLE_ROOT)) {
                rootAccountRights.add(new AccountRights(r.getId(),
                                                        accountId,
                                                        r.getUserId(),
                                                        r.getRoles(),
                                                        r.getCounter()));
            } else {
                log.debug("Wildcard Account member without ROOT role: {}", r);
            }
        }

        return rootAccountRights;
    }


    @Override
    public AccountRights findAccountRightsForUser(int accountId, int userId)
        throws DBNotFoundException {

        StringBuilder userDn = new StringBuilder();
        userDn.append(UNIQUE_ID);
        userDn.append("=");
        userDn.append(userId);
        userDn.append(",");
        userDn.append(PEOPLE_OU);
        userDn.append(",");
        userDn.append(getBaseDn());

        Filter filter = new EqualsFilter(ROLE_OCCUPANT.toString(),
                                         userDn.toString());

        List<AccountRights> rights = ldapTemplate.search(BASE_OU,
                                                         filter.encode(),
                                                         converter);

        if (null == rights || 0 == rights.size()) {
            StringBuilder err = new StringBuilder();
            err.append("No rights found for filter: ");
            err.append(filter.encode());
            log.warn(err.toString());
            throw new DBNotFoundException(err.toString());
        }

        AccountRights accountRights = null;
        for (AccountRights right : rights) {
            // Is user root?
            if (right.getRoles().contains(Role.ROLE_ROOT)) {
                accountRights = new AccountRights(right.getId(),
                                                  accountId,
                                                  right.getUserId(),
                                                  right.getRoles(),
                                                  right.getCounter());
                break;

            } else if (right.getAccountId() == accountId) {
                accountRights = right;
            }
        }

        return accountRights;
    }

    @Override
    public void save(AccountRights item) throws DBConcurrentUpdateException {
        Attributes attrs = converter.toAttributes(item);

        StringBuilder dn = new StringBuilder();
        dn.append(UNIQUE_ID);
        dn.append("=");
        dn.append(item.getId());
        dn.append(",");
        dn.append(BASE_OU);

        try {
            ldapTemplate.bind(dn.toString(), null, attrs);

        } catch (NameNotFoundException e) {
            log.warn("Item not saved: {}", item, e);
        } catch (NameAlreadyBoundException e) {
            log.info("Updating item: {}", item, e);
            ldapTemplate.rebind(dn.toString(), null, attrs);
        }
    }

    @Override
    public Set<Integer> getIds() {
        List<AccountRights> rights;

        Filter filter = new EqualsFilter(OBJECT_CLASS.toString(),
                                         RIGHTS.toString());
        try {
            rights = ldapTemplate.search(BASE_OU, filter.encode(), converter);

        } catch (NameNotFoundException e) {
            log.info(e.getMessage());
            rights = new ArrayList<AccountRights>();
        }

        Set<Integer> ids = new HashSet<Integer>();
        for (AccountRights right : rights) {
            ids.add(right.getId());
        }
        return ids;
    }

}
