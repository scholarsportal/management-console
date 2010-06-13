/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradav.core;

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
