package org.duraspace.duradav.core;

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
