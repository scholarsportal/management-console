/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import org.duracloud.account.common.domain.Identifiable;

import java.util.Collection;
import java.util.List;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public interface DomainConverter<T extends Identifiable> {

    public List<ReplaceableAttribute> toAttributesAndIncrement(T item);

    public T fromAttributes(Collection<Attribute> atts, int id);

    public void setDomain(String domain);
}
