package org.duracloud.aitsync.watcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.duracloud.aitsync.domain.ArchiveItResource;
import org.duracloud.aitsync.service.RemoteRepoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Daniel Bernstein
 * @created 12/18/2012
 */
@Component
public class RemoteRepoImpl implements RemoteRepo {
    private Logger log = LoggerFactory.getLogger(RemoteRepoImpl.class);

    @Override
    public List<Resource> getResources(long groupId,
                                                Date startDate)
        throws RemoteRepoException {

        BufferedReader reader =
            new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/warc-list.txt")));

        List<Resource> results = new LinkedList<Resource>();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }

                String[] values = line.split("\\s[ ]");
                ArchiveItResource r =
                    new ArchiveItResource(groupId,
                                          values[1],
                                          values[0]);
                results.add(r);
            }
        } catch (IOException e) {
            log.error("unable to parse results from archive-it.", e);
        }

        Collections.sort(results);

        return results;
    }
}
