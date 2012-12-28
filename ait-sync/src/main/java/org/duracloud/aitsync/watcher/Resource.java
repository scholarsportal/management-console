package org.duracloud.aitsync.watcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public interface Resource  extends Comparable<Resource> {
    InputStream getInputStream() throws IOException;
    String getMd5();
    Long getGroupId();
    Date getCreatedDate();
    String getFilename();
    URL toURL();
}
