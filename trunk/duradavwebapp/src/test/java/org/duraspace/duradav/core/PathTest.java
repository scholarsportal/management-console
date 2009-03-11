package org.duraspace.duradav.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
        null, "/",
        "", "/",
        " ", "/",
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
            Path.fromString(FILES[i]);
        }
    }

    @Test
    public void testFromStringDirGood() {
        for (int i = 0; i < DIRS.length; i += 2) {
            Path.fromString(DIRS[i]);
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFromStringBad() {
        Path.fromString("no/leading/slash");
    }

    @Test
    public void testDenotesDirTrue() {
        assertTrue(Path.ROOT.denotesDir());
        for (int i = 0; i < DIRS.length; i += 2) {
            Path path = Path.fromString(DIRS[i]);
            assertTrue(path.denotesDir());
        }
    }

    @Test
    public void testDenotesDirFalse() {
        for (int i = 0; i < FILES.length; i += 2) {
            Path path = Path.fromString(FILES[i]);
            assertFalse(path.denotesDir());
        }
    }

    @Test
    public void testDenotesFileTrue() {
        for (int i = 0; i < FILES.length; i += 2) {
            Path path = Path.fromString(FILES[i]);
            assertTrue(path.denotesFile());
        }
    }

    @Test
    public void testDenotesFileFalse() {
        assertFalse(Path.ROOT.denotesFile());
        for (int i = 0; i < DIRS.length; i += 2) {
            Path path = Path.fromString(DIRS[i]);
            assertFalse(path.denotesFile());
        }
    }

    @Test
    public void testGetParentOfRoot() {
        assertNull(Path.ROOT.getParent());
    }

    @Test
    public void testGetParentOfDir() {
        assertEquals("/",     Path.fromString("/dir/").getParent().toString());
        assertEquals("/dir/", Path.fromString("/dir/dir/").getParent().toString());
    }

    @Test
    public void testGetParentOfFile() {
        assertEquals("/",     Path.fromString("/file").getParent().toString());
        assertEquals("/dir/", Path.fromString("/dir/file").getParent().toString());
    }

    @Test
    public void testToString() {
        assertEquals("/", Path.ROOT.toString());
        for (int i = 0; i < FILES.length; i += 2) {
            Path path = Path.fromString(FILES[i]);
            assertEquals(FILES[i+1], path.toString());
        }
        for (int i = 0; i < DIRS.length; i += 2) {
            Path path = Path.fromString(DIRS[i]);
            assertEquals(DIRS[i+1], path.toString());
        }
    }

    @Test
    public void testEqualsTrue() {
        assertEquals(Path.ROOT, Path.fromString("/"));
        for (int i = 0; i < FILES.length; i += 2) {
            Path path1 = Path.fromString(FILES[i]);
            Path path2 = Path.fromString(FILES[i+1]);
            assertEquals(path1, path2);
        }
        for (int i = 0; i < DIRS.length; i += 2) {
            Path path1 = Path.fromString(DIRS[i]);
            Path path2 = Path.fromString(DIRS[i+1]);
            assertEquals(path1, path2);
        }
    }

    @Test
    public void testEqualsFalse() {
        assertFalse(Path.ROOT.equals(null));
        Path path1 = Path.fromString("/file");
        Path path2 = Path.fromString("/dir/");
        assertFalse(path1.equals(path2));
        assertFalse(path2.equals(path1));
    }
}

