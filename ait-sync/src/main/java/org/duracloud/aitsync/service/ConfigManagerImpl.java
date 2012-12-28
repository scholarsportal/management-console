package org.duracloud.aitsync.service;

import java.io.File;

import org.duracloud.aitsync.domain.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
@Component
public class ConfigManagerImpl implements ConfigManager {
    private Logger log = LoggerFactory.getLogger(ConfigManagerImpl.class);

    public static final String DURACLOUD_AITSYNC_STATE_DIR =
        "duracloud.aitsync.state.dir";
    private File stateDirectory;

    private Configuration config;
    
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
    public File getStateDirectory(){
        return this.stateDirectory;
    }
    
    @Override
    public int getMaxConcurrentWorkers(){
        return 5;
    }
    
    @Override
    public String getDuracloudUsername() {
        return this.config.getDuracloudUsername();
    }
    
    @Override
    public Configuration getConfiguration() {
        return this.config;
    }
    
    @Override
    public void initialize(Configuration config) {
         this.config = config;
    }
}
