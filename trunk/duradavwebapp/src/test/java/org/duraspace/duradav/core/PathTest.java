package org.duraspace.duradav.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class PathTest {

    private static final String[] FILES = new String[] {
        // fromString, expected toString
        "/file", "/file",
        " /file", "/file",
        "/file ", "/file",
        " /file ", "/file",
        "/dir/file", "/dir/file",
        "/dir/dir/file", "/dir/dir/file"
    };

    private static final String[] DIRS = new String[] {
        // fromString, expected toString
        "/", "/",
        " /", "/",
        "/ ", "/",
        " / ", "/",
        "/dir/", "/dir/",
        "/dir/dir/", "/dir/dir/"
    };

    @Test
    public void testFromStringFileGood() {
        for (int i = 0; i < FILES.length; i += 2) {
            new ContentPath(FILES[i]);
        }
    }

    @Test
    public void testFromStringDirGood() {
        for (int i = 0; i < DIRS.length; i += 2) {
            new CollectionPath(DIRS[i]);
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFromStringBad() {
        // TODO: test difference cases
        new CollectionPath("no/leading/slash");
    }

    @Test
    public void testGetParentOfRoot() {
        assertNull(Path.ROOT.getParent());
    }

    @Test
    public void testGetParentOfDir() {
        assertEquals("/",     new CollectionPath("/dir/").getParent().toString());
        assertEquals("/dir/", new CollectionPath("/dir/dir/").getParent().toString());
    }

    @Test
    public void testGetParentOfFile() {
        assertEquals("/",     new ContentPath("/file").getParent().toString());
        assertEquals("/dir/", new ContentPath("/dir/file").getParent().toString());
    }

    @Test
    public void testToString() {
        assertEquals("/", Path.ROOT.toString());
        for (int i = 0; i < FILES.length; i += 2) {
            Path path = new ContentPath(FILES[i]);
            assertEquals(FILES[i+1], path.toString());
        }
        for (int i = 0; i < DIRS.length; i += 2) {
            Path path = new CollectionPath(DIRS[i]);
            assertEquals(DIRS[i+1], path.toString());
        }
    }

    @Test
    public void testEqualsTrue() {
        assertEquals(Path.ROOT, new CollectionPath("/"));
        for (int i = 0; i < FILES.length; i += 2) {
            Path path1 = new ContentPath(FILES[i]);
            Path path2 = new ContentPath(FILES[i+1]);
            assertEquals(path1, path2);
        }
        for (int i = 0; i < DIRS.length; i += 2) {
            Path path1 = new CollectionPath(DIRS[i]);
            Path path2 = new CollectionPath(DIRS[i+1]);
            assertEquals(path1, path2);
        }
    }

    @Test
    public void testEqualsFalse() {
        assertFalse(Path.ROOT.equals(null));
        Path path1 = new ContentPath("/file");
        Path path2 = new CollectionPath("/dir/");
        assertFalse(path1.equals(path2));
        assertFalse(path2.equals(path1));
    }
}

