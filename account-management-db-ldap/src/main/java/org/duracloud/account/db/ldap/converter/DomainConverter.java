/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.ldap.converter;

import org.springframework.ldap.core.simple.ParameterizedContextMapper;

import javax.naming.directory.Attributes;

/**
 * This interface defines contract for LDAP to/from Domain Object converters.
 *
 * @author Andrew Woods
 *         Date: 6/7/12
 */
public interface DomainConverter<T> extends ParameterizedContextMapper<T> {

    /**
     * This method converts the arg domain object to a set of LDAP attributes.
     *
     * @param item to convert
     * @return LDAP attributes representing arg object
     */
    public Attributes toAttributes(T item);
}
