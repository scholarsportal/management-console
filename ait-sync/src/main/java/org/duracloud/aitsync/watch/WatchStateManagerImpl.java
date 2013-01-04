package org.duracloud.aitsync.watch;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.duracloud.aitsync.config.ConfigManager;
import org.duracloud.aitsync.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 * 
 */
@Component
class WatchStateManagerImpl implements WatchStateManager {
    private Logger log = LoggerFactory.getLogger(WatchStateManagerImpl.class);
    private Map<Long, InnerWatchState> watchStateMap;
    private ConfigManager configManager;
    private static String WATCH_STATE_FILE = "watch-state.xml";

    @Autowired
    public WatchStateManagerImpl(ConfigManager configManager) {
        this.watchStateMap = new HashMap<Long, InnerWatchState>();
        this.configManager = configManager;
    }

    @PostConstruct
    public void init() {
        try {
            File file = getWatchStateFile();
            if (file.exists()) {
                this.watchStateMap =
                    (Map<Long, InnerWatchState>) IOUtils.fromXML(file);
                log.info("restored state from " + file.getAbsolutePath());
            } else {
                log.info("no state to restore.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File getWatchStateFile() {
        return new File(this.configManager.getStateDirectory(),
                        WATCH_STATE_FILE);
    }

    @PreDestroy
    public void destroy() {
        try {
            File file = getWatchStateFile();
            IOUtils.toXML(file, this.watchStateMap);
            log.info("stored state to " + file.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WatchState getState(long archiveItAccountId) {
        return getInnerState(archiveItAccountId);
    }

    private InnerWatchState getInnerState(long archiveItAccountId) {
        InnerWatchState watchState = this.watchStateMap.get(archiveItAccountId);
        if (watchState == null) {
            watchState = new InnerWatchState();
            watchState.setDateOfLastCopiedResource(new Date(0));
            this.watchStateMap.put(archiveItAccountId, watchState);
        }

        return watchState;
    }

    @Override
    public void setDateOfLastCopiedResource(long archiveItAccountId, Date date) {
        getInnerState(archiveItAccountId).setDateOfLastCopiedResource(date);
    }

    @Override
    public void removeState(long archiveItAccountId) {
        this.watchStateMap.remove(archiveItAccountId);
    }

    private static class InnerWatchState implements WatchState {

        private Date dateOfLastCopiedResource;

        public Date getDateOfLastCopiedResource() {
            return dateOfLastCopiedResource;
        };

        protected void setDateOfLastCopiedResource(Date date) {
            this.dateOfLastCopiedResource = date;
        }

    }
}
