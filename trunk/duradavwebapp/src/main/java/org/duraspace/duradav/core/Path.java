package org.duraspace.duradav.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The location of a file or directory.
 * <p>
 * Path strings always begin with '/' and end with a non-whitespace character.
 * If the last character of a path string is '/', it refers to a directory.
 * <p>
 * Examples:
 * <ul>
 *   <li> /</li>
 *   <li> /dir1/</li>
 *   <li> /file1.txt</li>
 *   <li> /dir1/file2.txt</li>
 * </ul>
 */
public abstract class Path {

    protected final Logger log = LoggerFactory.getLogger(Path.class);

    public static final CollectionPath ROOT = new CollectionPath("/");

    private final String string;

    private final boolean denotesCollection;

    private CollectionPath parent;

    protected Path(String string, boolean denotesCollection) {
        this.string = string.trim();
        this.denotesCollection = denotesCollection;
        validate();
    }

    private void validate() {
        log.debug("validating path: '" + string + "'");

        if (string.length() == 0) {
            throw new IllegalArgumentException("Path cannot be empty");
        }
        if (!string.startsWith("/")) {
            throw new IllegalArgumentException("Path must begin with '/'");
        }
        if (string.endsWith("/")) {
            if (!denotesCollection) {
                throw new IllegalArgumentException("Content path cannot end"
                                                   + " with '/'");
            }
        } else {
            if (denotesCollection) {
                throw new IllegalArgumentException("Collection path must end"
                                                   + " with '/'");
            }
        }
    }

    public boolean denotesCollection() {
        return denotesCollection;
    }

    public CollectionPath getParent() {
        if (parent == null) {
            setParent();
        }
        return parent;
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        return o.toString().equals(toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    private void setParent() {
        if (equals(ROOT)) return;
        String temp = toString();
        if (denotesCollection) {
            temp = temp.substring(0, temp.length() - 1);
        }
        temp = temp.substring(0, temp.lastIndexOf('/') + 1);
        parent = new CollectionPath(temp);
    }

}
