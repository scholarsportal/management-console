/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.security;

import java.util.Map;

/**
 * @author Andrew Woods
 * Date: 4/8/11
 */
public interface AnnotationParser {

    /**
     * This method searches through a class and its hierarchy for a given
     * annotation.
     * Once the methods with the given annotation are found, a mapping of the
     * method names and annotation argument values is returned.
     *
     * @param annotationClass sought
     * @param targetClass     over which annotation should be found
     * @return map of method names and annotation arguments
     */
    public Map<String, Object[]> getMethodAnnotationsForClass(Class annotationClass,
                                                              Class targetClass);
}
