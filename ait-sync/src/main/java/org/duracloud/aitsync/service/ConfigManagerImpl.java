package org.duracloud.aitsync.service;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Daniel Bernstein
 * 
 */
public class ConfigManagerImpl implements ConfigManager {
    private Logger log = LoggerFactory.getLogger(ConfigManagerImpl.class);

    public static final String DURACLOUD_AITSYNC_STATE_DIR =
        "duracloud.aitsync.state.dir";
    private File stateDirectory;

    public ConfigManagerImpl() {
        String path = System.getProperty(DURACLOUD_AITSYNC_STATE_DIR);
        if (path == null) {
            log.info("no state directory specified in system properties: using default.");
            path =
                System.getProperty("user.home") + File.separator + ".aitsync";
        }


        stateDirectory = new File(path);
        stateDirectory.mkdirs();

        log.info("state directory set: {}", path);

    }

    @Override
    public File getMappingsFile() {
        return new File(stateDirectory, "mappings.xml");
    }
}
