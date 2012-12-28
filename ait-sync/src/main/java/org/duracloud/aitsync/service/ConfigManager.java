package org.duracloud.aitsync.service;

import java.io.File;

import org.duracloud.aitsync.domain.Configuration;


/**
 * 
 * @author Daniel Bernstein
 * Date:  12/17/2012
 *
 */
public interface ConfigManager {

    /**
     * The root directory for all saved state.
     * @return
     */
    File getStateDirectory();

    int getMaxConcurrentWorkers();

    String getDuracloudUsername();

    void initialize(Configuration config);

    Configuration getConfiguration();
    
}
