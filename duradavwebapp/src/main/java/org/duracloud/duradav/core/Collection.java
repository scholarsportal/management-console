/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradav.core;

import java.util.Date;

public class Collection extends Resource {

    private final CollectionPath collectionPath;

    private final Iterable<String> children;

    public Collection(CollectionPath path,
                      Date modifiedDate,
                      Iterable<String> children) {
        super(path, modifiedDate, true);
        this.collectionPath = path;
        this.children = children;
    }

    public CollectionPath getCollectionPath() {
        return collectionPath;
    }

    /**
     * Gets the path suffixes of each direct child of this collection.
     * Suffixes are of the form "name" or "name/", for content and collection
     * children, respectively.
     */
    public Iterable<String> getChildren() {
        return children;
    }

}
