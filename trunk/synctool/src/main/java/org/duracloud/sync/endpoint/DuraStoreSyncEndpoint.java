package org.duracloud.sync.endpoint;

import org.duracloud.common.util.ChecksumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Endpoint which pushes files to DuraCloud.
 *
 * @author: Bill Branan
 * Date: Mar 17, 2010
 */
public class DuraStoreSyncEndpoint implements SyncEndpoint {

    private final Logger logger =
        LoggerFactory.getLogger(DuraStoreSyncEndpoint.class);

    public void syncFile(File file) {
        logger.info("Syncing file " + file.getAbsolutePath() + " to DuraCloud!");        
    }

    public String computeChecksum(File file) throws FileNotFoundException {
        ChecksumUtil cksumUtil = new ChecksumUtil(ChecksumUtil.Algorithm.MD5);
        return cksumUtil.generateChecksum(file);
    }

    public Iterator<String> getFilesList() {
        return new ArrayList().iterator();
    }
}
