package org.duracloud.chunk.manifest;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.log4j.Logger;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.chunk.manifest.xml.ManifestDocumentBinding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author Andrew Woods
 *         Date: Feb 7, 2010
 */
public class ChunksManifest extends ChunksManifestBean {

    private final Logger log = Logger.getLogger(getClass());

    public static final String SCHEMA_VERSION = "0.2";
    
    private int chunkIndex = -1;
    private final static String mimetype = "application/xml";
    private final static String chunkSuffix = ".dura-chunk-";
    private final static String manifestSuffix = ".dura-manifest";
    private static final int MAX_CHUNKS = 9999;


    public ChunksManifest(ChunksManifestBean bean) {
        this.setEntries(bean.getEntries());
        this.setHeader(bean.getHeader());
    }

    public ChunksManifest(String sourceContentId,
                          String sourceMimetype,
                          long sourceByteSize) {
        this.setEntries(new ArrayList<ManifestEntry>());
        this.setHeader(new ManifestHeader(sourceContentId,
                                          sourceMimetype,
                                          sourceByteSize));
    }

    public void setMD5OfSourceContent(String md5) {
        getHeader().setSourceMD5(md5);
    }

    public String getManifestId() {
        return getHeader().getSourceContentId() + manifestSuffix;
    }

    public String nextChunkId() {
        if (chunkIndex >= MAX_CHUNKS) {
            throw new DuraCloudRuntimeException("Max chunks: " + MAX_CHUNKS);
        }
        return getHeader().getSourceContentId() + chunkSuffix +
            nextChunkIndex();
    }

    private String nextChunkIndex() {
        return String.format("%1$04d", ++chunkIndex);
    }

    public void addEntry(String chunkId, String chunkMD5, long chunkSize) {
        getEntries().add(new ManifestEntry(chunkId,
                                           chunkMD5,
                                           parseIndex(chunkId),
                                           chunkSize));
    }

    private int parseIndex(String chunkId) {
        String prefix = getHeader().getSourceContentId() + chunkSuffix;
        String num = chunkId.substring(prefix.length());
        return Integer.parseInt(num);
    }

    public InputStream getBody() {
        String xml = ManifestDocumentBinding.createDocumentFrom(this);
        log.debug("Manifest body: '"+xml+"'");
        
        return new AutoCloseInputStream(new ByteArrayInputStream(xml.getBytes()));
    }


}
