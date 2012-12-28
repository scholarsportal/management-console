package org.duracloud.aitsync.test;

import java.io.File;

import org.duracloud.aitsync.service.ConfigManagerImpl;
/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public class Utils {
    public static void configureStateDirectory() {
        File sd =
            new File(System.getProperty("java.io.tmpdir")
                + File.separator + "test" + System.currentTimeMillis());
        sd.deleteOnExit();
        sd.mkdirs();
        System.setProperty(ConfigManagerImpl.DURACLOUD_AITSYNC_STATE_DIR,
                           sd.getAbsolutePath());
    }

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }        
    }
}
