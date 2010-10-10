/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Identifiable;

import java.util.Collection;
import java.util.List;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public interface DomainConverter<T extends Identifiable> {

    List<ReplaceableAttribute> toAttributesAndIncrement(T item);

    T fromAttributes(Collection<Attribute> atts, String id);

    void setDomain(String domain);
}
