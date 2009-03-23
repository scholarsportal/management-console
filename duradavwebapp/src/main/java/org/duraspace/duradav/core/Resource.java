package org.duraspace.duradav.core;

import java.util.Date;

public abstract class Resource {

    private final Path path;

    private final Date modifiedDate;

    private final boolean isCollection;

    protected Resource(Path path,
                       Date modifiedDate,
                       boolean isCollection) {
        this.path = path;
        this.modifiedDate = modifiedDate;
        this.isCollection = isCollection;
    }

    public Path getPath() {
        return path;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public boolean isCollection() {
        return isCollection;
    }

}
