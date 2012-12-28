package org.duracloud.aitsync.mapping;

import java.util.List;

import org.duracloud.aitsync.domain.Mapping;


/**
 * 
 * @author Daniel Bernstein
 * @created 12/18/2012
 */
public interface MappingManager {
    /**
     * Adds a mapping
     * 
     * @param mapping
     * @throws MappingAlreadyExistsException
     */
    void addMapping(Mapping mapping) throws MappingAlreadyExistsException;

    /**
     * Removes a mapping. If no mapping exists for the specified id, null is
     * returned.
     * 
     * @param archiveItAccountId
     * @return
     */
    Mapping removeMapping(long archiveItAccountId);

    /**
     * Returns a list of all mappings
     * 
     * @return
     */
    List<Mapping> getMappings();

    /**
     * Remvoes all mappings
     */
    void clear();

    /**
     * Retrieves a mapping by Archive-It Account Id.
     * 
     * @param archiveItAccountId
     * @return
     */
    Mapping getMapping(long archiveItAccountId);

}
