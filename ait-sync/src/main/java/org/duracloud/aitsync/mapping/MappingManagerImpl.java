package org.duracloud.aitsync.mapping;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.duracloud.aitsync.config.ConfigManager;
import org.duracloud.aitsync.domain.Mapping;
import org.duracloud.aitsync.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Daniel Bernstein
 * @created 12/18/2012
 */
public class MappingManagerImpl implements MappingManager {
    private Logger log = LoggerFactory.getLogger(MappingManagerImpl.class);
    private Map<Long, Mapping> mappings = new HashMap<Long, Mapping>();

    private ConfigManager configManager;

    public MappingManagerImpl(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @PostConstruct
    public void load() {
        File mappingsFile = getMappingsFile();
        if (mappingsFile.exists()) {
            try {
                this.mappings =
                    (Map<Long, Mapping>) IOUtils.fromXML(mappingsFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private File getMappingsFile() {
        return new File(this.configManager.getStateDirectory(), "mappings.xml");
    }

    @PreDestroy
    public void shutdown() {
        File mappingsFile = getMappingsFile();
        IOUtils.toXML(mappingsFile, this.mappings);
    }

    @Override
    public void addMapping(Mapping mapping)
        throws MappingAlreadyExistsException {

        if (this.mappings.containsKey(mapping.getArchiveItAccountId())) {
            throw new MappingAlreadyExistsException();
        }

        this.mappings.put(mapping.getArchiveItAccountId(), mapping);
    }

    @Override
    public Mapping removeMapping(long archiveItAccountId) {
        return this.mappings.remove(archiveItAccountId);
    }

    @Override
    public List<Mapping> getMappings() {
        return new ArrayList<Mapping>(this.mappings.values());
    }

    @Override
    public void clear() {
        mappings.clear();
    }

    @Override
    public Mapping getMapping(long archiveItAccountId) {
        return mappings.get(archiveItAccountId);
    }
}
