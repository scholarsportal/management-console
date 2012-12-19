package org.duracloud.aitsync.service;

import java.io.File;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public interface ConfigManager {
    /**
     * File where mappings are persisted to disk.
     * @return
     */
    public File getMappingsFile();
}
