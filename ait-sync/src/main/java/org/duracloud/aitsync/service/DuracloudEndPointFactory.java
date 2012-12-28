package org.duracloud.aitsync.service;

import org.duracloud.aitsync.domain.Mapping;
import org.duracloud.aitsync.mapping.MappingManager;
import org.duracloud.aitsync.watcher.DuracloudEndPoint;
import org.duracloud.aitsync.watcher.EndPoint;
import org.duracloud.aitsync.watcher.EndPointException;
import org.duracloud.aitsync.watcher.Resource;
import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Daniel Bernstein 
 * Date: 12/24/2012
 * 
 */
@Component
public class DuracloudEndPointFactory implements EndPointFactory {
    private MappingManager mappingManager;
    private ContentStoreLocator locator;
    private ConfigManager configManager;

    @Autowired
    public DuracloudEndPointFactory(
        MappingManager mappingManager, ContentStoreLocator locator,
        ConfigManager configManager) {
        super();
        this.mappingManager = mappingManager;
        this.locator = locator;
        this.configManager = configManager;
    }

    @Override
    public EndPoint createEndPoint(Resource resource) throws EndPointException {
        try {

            Mapping mapping = mappingManager.getMapping(resource.getGroupId());
            ContentStore contentStore = locator.findContentStore(mapping);
            EndPoint endPoint =
                new DuracloudEndPoint(contentStore,
                                      mapping.getDuracloudSpaceId(),
                                      configManager.getDuracloudUsername());
            return endPoint;
        } catch (ContentStoreException e) {
            throw new EndPointException("failed to create endpoint", e);
        }

    }

}
