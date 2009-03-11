package org.duraspace.duradav.core;

import java.util.Date;

public class Resource {

    private final Path path;

    private final Date modifiedDate;

    public Resource(Path path,
                    Date modifiedDate) {
        this.path = path;
        this.modifiedDate = modifiedDate;
    }

    public Path getPath() {
        return path;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

}
