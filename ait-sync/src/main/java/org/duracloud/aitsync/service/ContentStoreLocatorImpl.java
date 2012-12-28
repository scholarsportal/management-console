package org.duracloud.aitsync.service;

import org.duracloud.aitsync.domain.Configuration;
import org.duracloud.aitsync.domain.Mapping;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.common.model.Credential;
import org.duracloud.error.ContentStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
@Component
public class ContentStoreLocatorImpl implements ContentStoreLocator {
    private ConfigManager configManager;
 
    @Autowired
    public ContentStoreLocatorImpl(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public ContentStore findContentStore(Mapping mapping) throws ContentStoreException {
        Configuration c = this.configManager.getConfiguration();
        Integer mp = mapping.getDuracloudPort();
        String port = null;
        if(mp != null){
            port = String.valueOf(mp);
        }
        ContentStoreManager manager = new ContentStoreManagerImpl(mapping.getDuracloudHost(), port);
        manager.login(new Credential(c.getDuracloudUsername(), c.getDuracloudPassword()));

        return manager.getPrimaryContentStore();
    }

}
