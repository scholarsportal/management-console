package org.duracloud.chunk;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.duracloud.common.error.DuraCloudRuntimeException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Woods
 *         Date: Feb 7, 2010
 */
public class ChunksManifest {

    private String manifestId;
    private int manifestSize;
    private ManifestHeader header;
    private List<ManifestEntry> entries = new ArrayList<ManifestEntry>();

    private int chunkIndex = -1;
    private final static String mimetype = "application/xml";
    private final static String chunkSuffix = ".dura-chunk-";
    private final static String manifestSuffix = ".dura-manifest";
    private static final int MAX_CHUNKS = 9999;

    public ChunksManifest(String parentContentId, String parentMimetype) {
        manifestId = parentContentId + manifestSuffix;
        header = new ManifestHeader(parentContentId, parentMimetype);
    }

    public void setMD5OfFullContent(String md5) {
        header.setParentMD5(md5);
    }

    public String nextChunkId() {
        if (chunkIndex >= MAX_CHUNKS) {
            throw new DuraCloudRuntimeException("Max chunks: " + MAX_CHUNKS);
        }
        return header.getParentContentId() + chunkSuffix + nextChunkIndex();
    }

    private String nextChunkIndex() {
        return String.format("%1$04d", ++chunkIndex);
    }

    public void addEntry(String chunkId, String chunkMD5) {
        entries.add(new ManifestEntry(chunkId, chunkMD5, parseIndex(chunkId)));
    }

    private int parseIndex(String chunkId) {
        String prefix = header.getParentContentId() + chunkSuffix;
        String num = chunkId.substring(prefix.length());
        return Integer.parseInt(num);
    }

    public InputStream getBody() {
        // fixme
        // Construct xml in-memory, getSize, return bytearrayinputstream.
        String xml = "";
        manifestSize = xml.length();
        return new AutoCloseInputStream(new ByteArrayInputStream(xml.getBytes()));
    }

    private static class ManifestHeader {
        private String parentContentId;
        private String parentMimetype;
        private String parentMD5;

        private ManifestHeader(String parentContentId, String parentMimetype) {
            this.parentContentId = parentContentId;
            this.parentMimetype = parentMimetype;
        }

        public String getParentContentId() {
            return parentContentId;
        }

        public String getParentMimetype() {
            return parentMimetype;
        }

        public String getParentMD5() {
            return parentMD5;
        }

        public void setParentMD5(String parentMD5) {
            this.parentMD5 = parentMD5;
        }
    }

    private static class ManifestEntry {
        private String chunkId;
        private String chunkMD5;
        private int index;

        private ManifestEntry(String chunkId, String chunkMD5, int index) {
            this.chunkId = chunkId;
            this.chunkMD5 = chunkMD5;
            this.index = index;
        }
    }
}
