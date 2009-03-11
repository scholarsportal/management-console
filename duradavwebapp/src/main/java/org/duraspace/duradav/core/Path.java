package org.duraspace.duradav.core;

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
public class Path {

    public static final Path ROOT = new Path("/");

    private final String string;

    private Path parent;

    private Path(String string) {
        this.string = string;
    }

    public static Path fromString(String string) {
        if (string == null) {
            return ROOT;
        }
        String trimmed = string.trim();
        if (trimmed.length() == 0 || trimmed.equals("/")) {
            return ROOT;
        }
        if (trimmed.startsWith("/")) {
            return new Path(trimmed);
        }
        throw new IllegalArgumentException(
                "Path string (" + trimmed + ") does not begin with '/'");
    }

    public boolean denotesDir() {
        return string.endsWith("/");
    }

    public boolean denotesFile() {
        return !denotesDir();
    }

    public Path getParent() {
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
        if (this == ROOT) return;
        String temp = toString();
        if (denotesDir()) {
            temp = temp.substring(0, temp.length() - 1);
        }
        temp = temp.substring(0, temp.lastIndexOf('/') + 1);
        parent = Path.fromString(temp);
    }

}
