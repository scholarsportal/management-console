package org.duraspace.duradav.core;

import java.util.Date;

public class Collection extends Resource {

    private final CollectionPath collectionPath;

    private final Iterable<String> children;

    public Collection(CollectionPath path,
                      Date modifiedDate,
                      Iterable<String> children) {
        super(path, modifiedDate);
        this.collectionPath = path;
        this.children = children;
    }

    public CollectionPath getCollectionPath() {
        return collectionPath;
    }

    public Iterable<String> getChildren() {
        return children;
    }

}
