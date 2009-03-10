package org.duraspace.duradav;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public abstract class Body {

    public abstract InputStream getStream();

    public static Body fromStream(final InputStream stream) {
        return new Body() {
            @Override
            public InputStream getStream() {
                return stream;
            }
        };
    }

    public static Body fromFile(final File file) {
        return new Body() {
            @Override
            public InputStream getStream() {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

}
