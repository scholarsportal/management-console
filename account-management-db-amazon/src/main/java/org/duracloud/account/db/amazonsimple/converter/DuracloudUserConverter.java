/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.amazonsimple.converter;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;

/**
 * This class is responsible for converting DuracloudUser objects to/from
 * AmazonSimpleDB attributes.
 *
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class DuracloudUserConverter implements DomainConverter<DuracloudUser> {

    private final Logger log = LoggerFactory.getLogger(DuracloudUserConverter.class);

    protected static final String PASSWORD_ATT = "PASSWORD";
    protected static final String FIRSTNAME_ATT = "FIRSTNAME";
    protected static final String LASTNAME_ATT = "LASTNAME";
    protected static final String EMAIL_ATT = "EMAIL";
    protected static final String ACCTS_ATT = "ACCTS";

    private static final String ACCT_DELIM = "::";
    private static final String WITH_DELIM = "=>";
    private static final String ROLE_DELIM = ",";

    private String domain;

    @Override
    public List<ReplaceableAttribute> toAttributesAndIncrement(DuracloudUser user) {
        List<ReplaceableAttribute> atts = new ArrayList<ReplaceableAttribute>();

        String counter = FormatUtil.padded(user.getCounter() + 1);
        String accts = asString(user.getAcctToRoles());

        atts.add(new ReplaceableAttribute(ACCTS_ATT, accts, true));
        atts.add(new ReplaceableAttribute(PASSWORD_ATT,
                                          user.getPassword(),
                                          true));
        atts.add(new ReplaceableAttribute(FIRSTNAME_ATT,
                                          user.getFirstName(),
                                          true));
        atts.add(new ReplaceableAttribute(LASTNAME_ATT,
                                          user.getLastName(),
                                          true));
        atts.add(new ReplaceableAttribute(EMAIL_ATT, user.getEmail(), true));
        atts.add(new ReplaceableAttribute(COUNTER_ATT, counter, true));

        return atts;
    }

    /**
     * This method formats the arg map as follows:
     * acct0=>role0,role1,roleX||acct1=>role0||...
     *
     * @param acctToRoles map
     * @return the string value of the map
     */
    protected String asString(Map<String, List<String>> acctToRoles) {
        StringBuilder sb = new StringBuilder();
        Set<String> accts = acctToRoles.keySet();
        for (String acct : accts) {
            sb.append(acct);
            sb.append(WITH_DELIM);

            List<String> roles = acctToRoles.get(acct);
            for (String role : roles) {
                sb.append(role);
                sb.append(ROLE_DELIM);
            }

            if (roles.size() > 0) {
                sb.delete(sb.length() - ROLE_DELIM.length(), sb.length());
            }
            sb.append(ACCT_DELIM);
        }

        if (accts.size() > 0) {
            sb.delete(sb.length() - ACCT_DELIM.length(), sb.length());
        }

        return sb.toString();
    }

    @Override
    public DuracloudUser fromAttributes(Collection<Attribute> atts, String id) {
        int counter = -1;
        String username = id;
        String password = null;
        String firstname = null;
        String lastname = null;
        String email = null;
        Map<String, List<String>> acctToRoles = null;

        for (Attribute att : atts) {
            String name = att.getName();
            String value = att.getValue();
            if (COUNTER_ATT.equals(name)) {
                counter = SimpleDBUtils.decodeZeroPaddingInt(value);

            } else if (PASSWORD_ATT.equals(name)) {
                password = value;

            } else if (FIRSTNAME_ATT.equals(name)) {
                firstname = value;

            } else if (LASTNAME_ATT.equals(name)) {
                lastname = value;

            } else if (EMAIL_ATT.equals(name)) {
                email = value;

            } else if (ACCTS_ATT.equals(name)) {
                acctToRoles = fromString(value);

            } else {
                StringBuilder msg = new StringBuilder("Unexpected name: ");
                msg.append(name);
                msg.append(" in domain: ");
                msg.append(getDomain());
                msg.append(" [with id]: ");
                msg.append(id);
                log.info(msg.toString());
            }
        }

        DuracloudUser user = new DuracloudUser(username,
                                               password,
                                               firstname,
                                               lastname,
                                               email,
                                               counter);
        user.setAcctToRoles(acctToRoles);
        return user;
    }

    /**
     * This method parses the arg text into a map.
     * The expected format of the arg is as follows:
     * acct0=>role0,role1,roleX||acct1=>role0||...
     *
     * @param text
     * @return
     */
    private Map<String, List<String>> fromString(String text) {
        Map<String, List<String>> acctToRoles = new HashMap<String, List<String>>();

        String[] acctRolesList = text.split(ACCT_DELIM);
        for (String acctRoles : acctRolesList) {

            String[] tokens = acctRoles.split(WITH_DELIM);
            if (tokens.length == 2) {
                String[] roles = tokens[1].split(ROLE_DELIM);
                acctToRoles.put(tokens[0], Arrays.asList(roles));

            } else {
                log.warn("skipped tokens: " + acctRoles + " in: " + text);
            }
        }

        return acctToRoles;
    }

    public String getDomain() {
        if (null == domain) {
            domain = "unknown-domain";
        }
        return domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }
}
