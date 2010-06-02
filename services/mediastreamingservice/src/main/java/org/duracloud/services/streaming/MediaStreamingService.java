package org.duracloud.services.streaming;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.common.model.Credential;
import org.duracloud.common.util.IOUtil;
import org.duracloud.common.util.MimetypeUtil;
import org.duracloud.common.util.SerializationUtil;
import org.duracloud.error.ContentStoreException;
import org.duracloud.services.BaseService;
import org.duracloud.services.ComputeService;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Service which provides media streaming capabilities
 *
 * @author Bill Branan
 *         Date: May 12, 2010
 */
public class MediaStreamingService extends BaseService implements ComputeService, ManagedService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String DEFAULT_DURASTORE_HOST = "localhost";
    private static final String DEFAULT_DURASTORE_PORT = "8080";
    private static final String DEFAULT_DURASTORE_CONTEXT = "durastore";
    private static final String DEFAULT_MEDIA_SOURCE_SPACE_ID = "media-source";
    private static final String DEFAULT_MEDIA_VIEWER_SPACE_ID = "media-viewer";

    private static final String ENABLE_STREAMING_TASK = "enable-streaming";
    private static final String DISABLE_STREAMING_TASK = "disable-streaming";    

    private String duraStoreHost;
    private String duraStorePort;
    private String duraStoreContext;
    private String username;
    private String password;    
    private String mediaViewerSpaceId;
    private String mediaSourceSpaceId;

    private ContentStore contentStore;

    private String streamHost;
    private String enableStreamingResult;

    @Override
    public void start() throws Exception {
        log("Starting Media Streaming Service as " + username);
        this.setServiceStatus(ServiceStatus.STARTING);
        
        ContentStoreManager storeManager =
            new ContentStoreManagerImpl(duraStoreHost,
                                        duraStorePort,
                                        duraStoreContext);
        storeManager.login(new Credential(username, password));
        contentStore = storeManager.getPrimaryContentStore();

        File workDir = new File(getServiceWorkDir());
        workDir.setWritable(true);
       
        // Create/enable distribution        
        String enableStreamingResponse =
            contentStore.performTask(ENABLE_STREAMING_TASK, mediaSourceSpaceId);
        Map<String, String> responseMap =
            SerializationUtil.deserializeMap(enableStreamingResponse);
        streamHost = responseMap.get("domain-name");
        enableStreamingResult = responseMap.get("results");

        // Create playlist in work dir
        PlaylistCreator creator = new PlaylistCreator();
        String playlistXml =
            creator.createPlaylist(contentStore, mediaSourceSpaceId);
        storePlaylist(playlistXml, workDir);

        // Replace variables in example player html files
        String sampleMediaId =
            getIdFromSpace(contentStore, mediaSourceSpaceId);
        updatePlayers(workDir, streamHost, sampleMediaId);

        // Move files from work dir to media viewer space
        moveFilesToSpace(workDir.listFiles(), contentStore, mediaViewerSpaceId);

        this.setServiceStatus(ServiceStatus.STARTED);        
    }

    private File storePlaylist(String playlistXml, File workDir) {
        File playlist = new File(workDir, "playlist.xml");

        FileOutputStream fileStream;
        try {
            fileStream = new FileOutputStream(playlist);
        } catch(FileNotFoundException e) {
            throw new RuntimeException("Unable to create playlist due to: " +
                                       e.getMessage());
        }

        OutputStreamWriter writer = new OutputStreamWriter(fileStream);
        try {
            writer.write(playlistXml, 0, playlistXml.length());
            writer.close();
        } catch(IOException e) {
            throw new RuntimeException("Unable to create playlist due to: " +
                                       e.getMessage());
        }

        return playlist;
    }

    private void updatePlayers(File workDir,
                               String streamHost,
                               String sampleMediaId) {
        File singlePlayer = new File(workDir, "singleplayer.html");
        File playlistPlayer = new File(workDir, "playlistplayer.html");

        try {
            IOUtil.fileFindReplace(singlePlayer, "$STREAM-HOST", streamHost);
            IOUtil.fileFindReplace(singlePlayer, "$MEDIA-FILE", sampleMediaId);
            IOUtil.fileFindReplace(playlistPlayer, "$STREAM-HOST", streamHost);
        } catch(IOException e) {
            throw new RuntimeException("Unable to update player files due to: "
                                       + e.getMessage());
        }
    }

    private String getIdFromSpace(ContentStore contentStore, String spaceId)
        throws ContentStoreException {
        Iterator<String> contents = contentStore.getSpaceContents(spaceId);
        String contentId = "";
        if(contents.hasNext()) {
            contentId = contents.next();
        }
        return contentId;
    }

    private void moveFilesToSpace(File[] files,
                                  ContentStore contentStore,
                                  String spaceId)
        throws ContentStoreException, FileNotFoundException {
        MimetypeUtil mimeUtil = new MimetypeUtil();

        List<File> toAdd = new ArrayList<File>();
        for (File file : files) {
            toAdd.add(file);
        }

        int maxloops = 20;
        int loops;
        for (loops = 0; !toAdd.isEmpty() && loops < maxloops; loops++) {
            File file = toAdd.remove(0);
            try {
                contentStore.addContent(spaceId,
                                        file.getName(),
                                        new FileInputStream(file),
                                        file.length(),
                                        mimeUtil.getMimeType(file),
                                        null,
                                        null);
            } catch (ContentStoreException e) {
                log(e.getMessage());
                toAdd.add(file);
            }
        }

        if(loops == maxloops) {
            log("Unable to complete loading of files into " + spaceId);
        }
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Stopping Media Streaming Service");
        this.setServiceStatus(ServiceStatus.STOPPING);
        
        // Disable distribution
        contentStore.performTask(DISABLE_STREAMING_TASK, mediaSourceSpaceId);
        
        this.setServiceStatus(ServiceStatus.STOPPED);
    }

    @Override
    public Map<String, String> getServiceProps() {
        Map<String, String> props = super.getServiceProps();
        
        // Add stream host
        props.put("streamHost", streamHost);

        String streamingStatus = enableStreamingResult;
        if(streamingStatus == null) {
            streamingStatus = "Enabling Streaming...";
        }

        props.put("streamingStatus", streamingStatus);
        
        return props;
    }

    @SuppressWarnings("unchecked")
    public void updated(Dictionary config) throws ConfigurationException {
        log("Attempt made to update Media Streaming Service configuration " +
            "via updated method. Updates should occur via class setters.");
    }

    public String getDuraStoreHost() {
        return duraStoreHost;
    }

    public void setDuraStoreHost(String duraStoreHost) {
        if(duraStoreHost != null && !duraStoreHost.equals("") ) {
            this.duraStoreHost = duraStoreHost;
        } else {
            log("Attempt made to set duraStoreHost to " + duraStoreHost +
                ", which is not valid. Setting value to default: " +
                DEFAULT_DURASTORE_HOST);
            this.duraStoreHost = DEFAULT_DURASTORE_HOST;
        }
    }

    public String getDuraStorePort() {
        return duraStorePort;
    }

    public void setDuraStorePort(String duraStorePort) {
        if(duraStorePort != null) {
            this.duraStorePort = duraStorePort;
        } else {
            log("Attempt made to set duraStorePort to null, which is not " +
                "valid. Setting value to default: " + DEFAULT_DURASTORE_PORT);
            this.duraStorePort = DEFAULT_DURASTORE_PORT;
        }
    }

    public String getDuraStoreContext() {
        return duraStoreContext;
    }

    public void setDuraStoreContext(String duraStoreContext) {
        if(duraStoreContext != null && !duraStoreContext.equals("")) {
            this.duraStoreContext = duraStoreContext;
        } else {
            log("Attempt made to set duraStoreContext to null or empty, " +
                "which is not valid. Setting value to default: " +
                DEFAULT_DURASTORE_CONTEXT);
            this.duraStoreContext = DEFAULT_DURASTORE_CONTEXT;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMediaViewerSpaceId() {
        return mediaViewerSpaceId;
    }

    public void setMediaViewerSpaceId(String mediaViewerSpaceId) {
        if(mediaViewerSpaceId != null && !mediaViewerSpaceId.equals("")) {
            this.mediaViewerSpaceId = mediaViewerSpaceId;
        } else {
            log("Attempt made to set mediaViewerSpaceId to null or empty, " +
                ", which is not valid. Setting value to default: " +
                DEFAULT_MEDIA_VIEWER_SPACE_ID);
            this.mediaViewerSpaceId = DEFAULT_MEDIA_VIEWER_SPACE_ID;
        }
    } 
    
    public String getMediaSourceSpaceId() {
        return mediaSourceSpaceId;
    }

    public void setMediaSourceSpaceId(String mediaSourceSpaceId) {
        if(mediaSourceSpaceId != null && !mediaSourceSpaceId.equals("")) {
            this.mediaSourceSpaceId = mediaSourceSpaceId;
        } else {
            log("Attempt made to set mediaSourceSpaceId to to null or empty, " +
                ", which is not valid. Setting value to default: " +
                DEFAULT_MEDIA_SOURCE_SPACE_ID);
            this.mediaSourceSpaceId = DEFAULT_MEDIA_SOURCE_SPACE_ID;
        }
    }

    private void log(String logMsg) {
        log.warn(logMsg);
        System.out.println(logMsg);
    }
}
