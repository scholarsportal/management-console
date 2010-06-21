package org.duracloud.chunk.writer;

/**
 * @author Andrew Woods
 *         Date: Feb 15, 2010
 */
public class AddContentResult {
    private String spaceId;
    private String contentId;
    private String md5 = "not-found";
    private State state = State.UNKNOWN;

    public AddContentResult(String spaceId, String contentId) {
        this.spaceId = spaceId;
        this.contentId = contentId;
    }

    public static enum State {
        SUCCESS, ERROR, UNKNOWN;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public String getContentId() {
        return contentId;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

}
