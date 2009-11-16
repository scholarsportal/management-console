package org.duracloud.utilities.akubra;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Wraps a given OutputStream, overriding all methods to check whether
 * an exception has been externally signaled after the underlying call
 * is made, and throwing that exception if so.
 *
 * @author Chris Wilper
 */
class ExAwareOutputStream extends FilterOutputStream {

    private IOException exception;

    ExAwareOutputStream(OutputStream sink) {
        super(sink);
    }

    void setException(IOException exception) {
        this.exception = exception;
    }

    @Override
    public void write(int b) throws IOException {
        super.write(b);
        if (exception != null) throw exception;
    }

    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
        if (exception != null) throw exception;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
        if (exception != null) throw exception;
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        if (exception != null) throw exception;
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (exception != null) throw exception;
    }

}