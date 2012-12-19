package org.duracloud.aitsync.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.duracloud.aitsync.domain.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

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
        File mappingsFile = configManager.getMappingsFile();
        if (mappingsFile.exists()) {
            XStream xstream = new XStream();
            InputStream is = null;
            try {
                is = new FileInputStream(mappingsFile);
                this.mappings = (HashMap<Long, Mapping>) xstream.fromXML(is);
            } catch (FileNotFoundException e) {
                log.error("file not found", e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        File mappingsFile = configManager.getMappingsFile();
        XStream xstream = new XStream();
        Writer writer = null;

        try {
            writer = new OutputStreamWriter(new FileOutputStream(mappingsFile));
            xstream.toXML(this.mappings, writer);
        } catch (IOException e) {
            log.error("file not found", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void addMapping(Mapping mapping)
        throws MappingAlreadyExistsException {
        
        if(this.mappings.containsKey(mapping.getArchiveItAccountId())){
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
