package org.duracloud.services.streaming;

import static junit.framework.Assert.assertTrue;
import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Bill Branan
 * Date: May 19, 2010
 */
public class PlaylistCreatorTest {

    private ContentStore contentStore;

    private static final String videoTitle = "Video Title";
    private static final String videoDescription = "Video Description";

    @Before
    public void setUp() throws ContentStoreException {
        List<String> mediaFiles = new ArrayList<String>();
        mediaFiles.add("video1");
        mediaFiles.add("video2");
        mediaFiles.add("video3");

        Map<String, String> mediaMetadata = new HashMap<String, String>();
        mediaMetadata.put(PlaylistCreator.TITLE_META, videoTitle);
        mediaMetadata.put(PlaylistCreator.DESCRIPTION_META, videoDescription);

        contentStore = createMockContentStore(mediaFiles, mediaMetadata);

    }

    private ContentStore createMockContentStore(List<String> files,
                                                Map<String, String> metadata)
        throws ContentStoreException {
        ContentStore contentStore = EasyMock.createMock(ContentStore.class);

        EasyMock
            .expect(contentStore.getSpaceContents(EasyMock.isA(String.class)))
            .andReturn(files.iterator())
            .anyTimes();

        EasyMock
            .expect(contentStore.getContentMetadata(EasyMock.isA(String.class),
                                                    EasyMock.isA(String.class)))
            .andReturn(metadata)
            .anyTimes();

        EasyMock.replay(contentStore);
        return contentStore;
    }

    @After
    public void tearDown() {
        EasyMock.verify(contentStore);
        contentStore = null;
    }

    @Test
    public void testPlaylistCreator() throws Exception {
        PlaylistCreator creator = new PlaylistCreator();
        String playlist = creator.createPlaylist(contentStore, "spaceId");

        assertTrue(playlist.contains("<rss xmlns:media=\"http://search.yahoo." +
                                     "com/mrss/\" version=\"2.0\">"));

        assertTrue(playlist.contains("<media:content url=\"video1\" />"));
        assertTrue(playlist.contains("<media:content url=\"video2\" />"));
        assertTrue(playlist.contains("<media:content url=\"video3\" />"));

        assertTrue(playlist.contains("<title>"+videoTitle+"</title>"));
        assertTrue(playlist.contains("<description>"+videoDescription+
                                     "</description>"));
    }
}
