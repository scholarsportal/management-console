package org.duracloud.utilities.akubra;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.testng.annotations.Test;

import static org.testng.Assert.fail;

/**
 * Unit tests for ExAwareOutputStream.
 *
 * @author Chris Wilper
 */
public class ExAwareOutputStreamTest {

    private ExAwareOutputStream out;

    private void init(boolean setException) {
        out = new ExAwareOutputStream(new ByteArrayOutputStream(), null);
        if (setException) {
            out.setException(new IOException());
        }
    }

    @Test
    public void allMethodsWithoutSetException() throws IOException {
        init(false);
        out.write(1);
        out.write(new byte[1]);
        out.write(new byte[1], 0, 1);
        out.flush();
        out.close();
    }

    @Test
    public void writeIntWithSetException() {
        init(true);
        try {
            out.write(1);
        } catch (IOException e1) {
            try {
                out.write(new byte[1]);
            } catch (IOException e2) {
                try {
                    out.write(new byte[1], 0, 1);
                } catch (IOException e3) {
                    try {
                        out.flush();
                    } catch (IOException e4) {
                        try {
                            out.flush();
                        } catch (IOException e5) {
                            try {
                                out.close();
                            } catch (IOException e6) {
                                return;
                            }
                        }
                    }
                }
            }
        }
        fail();
    }

    @Test(expectedExceptions=IOException.class)
    public void writeByteArrayWithSetException() throws IOException {
        init(true);
        out.write(new byte[1]);
    }

}
