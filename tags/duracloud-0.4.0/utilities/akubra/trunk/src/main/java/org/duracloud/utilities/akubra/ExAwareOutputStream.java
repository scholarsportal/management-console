package org.duracloud.utilities.akubra;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Wraps a given OutputStream, overriding all methods to check whether
 * an exception has been externally signaled after the underlying call
 * is made, and throwing that exception if so.
 * <p>
 * Instances of this class will also optionally notify a constructor-provided
 * listener when successfully closed.
 *
 * @author Chris Wilper
 */
class ExAwareOutputStream extends FilterOutputStream {

    private final ContentWriteListener listener;

    private IOException exception;

    /**
     * Creates an instance.
     *
     * @param sink the underlying stream to write to.
     * @param listener the listener to notify when the output stream is
     *                 successfully closed (null if notification is not needed).
     */
    ExAwareOutputStream(OutputStream sink,
                        ContentWriteListener listener) {
        super(sink);
        this.listener = listener;
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
        if (listener != null) listener.contentWritten();
    }

}