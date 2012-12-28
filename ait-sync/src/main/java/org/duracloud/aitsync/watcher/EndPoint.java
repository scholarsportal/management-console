package org.duracloud.aitsync.watcher;

import java.io.InputStream;
import java.net.URL;
/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public interface EndPoint {
    boolean sync(String filename, String md5, URL url, InputStream stream);
}
