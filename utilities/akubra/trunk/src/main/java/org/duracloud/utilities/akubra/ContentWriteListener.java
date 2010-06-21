package org.duracloud.utilities.akubra;

/**
 * Interface to be notified when the content of a blob has been successfully
 * written.
 *
 * @author Chris Wilper
 */
interface ContentWriteListener {

    /**
     * Signals that the blob content has been successfully written.
     */
    void contentWritten();

}
