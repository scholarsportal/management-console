package org.duracloud.services.webapputil;

import org.duracloud.services.ComputeService;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * This interface abstracts the ability to (un)deploy a webapp to/from an
 * application server.
 *
 * @author Andrew Woods
 *         Date: Dec 7, 2009
 */
public interface WebAppUtil extends ComputeService {

    /**
     * This method deploys the arg war to a new application server under the
     * arg context.
     *
     * @param context
     * @param war
     * @return URL of running webapp
     */
    public URL deploy(String context, InputStream war);

    /**
     * This method undeploys the webapp currently running at the arg url and
     * destroys the application server that was hosting it.
     *
     * @param url of webapp to undeploy
     */
    public void unDeploy(URL url);

    /**
     * This method returns the directory in which this service stores its
     * internal resources.
     *
     * @return directory of resources.
     */
    public File getWorkDir();
}
