package org.duracloud.sync.endpoint;

import org.duracloud.chunk.FileChunker;
import org.duracloud.chunk.FileChunkerOptions;
import org.duracloud.chunk.writer.DuracloudContentWriter;

import java.io.File;

/**
 * @author: Bill Branan
 * Date: Apr 8, 2010
 */
public class DuraStoreChunkSyncEndpoint extends DuraStoreSyncEndpoint {

    private long maxFileSize;
    private FileChunker chunker;

    public DuraStoreChunkSyncEndpoint(String host,
                                      int port,
                                      String context,
                                      String username,
                                      String password,
                                      String spaceId,
                                      long maxFileSize) {
        super(host, port, context, username, password, spaceId);

        if(maxFileSize % 1024 != 0) {
            throw new RuntimeException("Max file size must be a " +
                                       "multiple of 1024");
        }
        this.maxFileSize = maxFileSize;

        DuracloudContentWriter contentWriter =
            new DuracloudContentWriter(getContentStore());
        FileChunkerOptions chunkerOptions =
            new FileChunkerOptions(maxFileSize);
        chunker = new FileChunker(contentWriter, chunkerOptions);
    }

    @Override
    protected void addUpdateContent(String contentId,
                                    String contentChecksum,
                                    File syncFile) {
        chunker.addContent(getSpaceId(), contentId, contentChecksum, syncFile);
    }

}
