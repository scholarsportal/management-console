package org.duracloud.aitsync;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
/**
 * 
 * @author Daniel Bernstein
 * @created 12/17/2012
 *
 */
public interface RestUtils {
    public InputStream getInputStream(HttpServletRequest request)
        throws IOException;
}
