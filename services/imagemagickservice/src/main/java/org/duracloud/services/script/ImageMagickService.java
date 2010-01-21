package org.duracloud.services.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Installs ImageMagick for use by other services
 *
 * @author Bill Branan
 *         Date: Jan 21, 2010
 */
public class ImageMagickService extends ScriptService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void start() throws Exception {
        log.info("Starting ImageMagick Service");
        super.start();
    }

    @Override
    public void stop() throws Exception {
        log.info("Stopping ImageMagick Service");
        super.stop();
    }
}
