package org.duracloud.computeprovider.mgmt;

public enum InstanceState {

    RUNNING("running"),
    PENDING("pending"),
    TERMINATED("terminated"),
    SHUTTINGDOWN("shutting-down"),
    UNKNOWN("unknown");

    private final String text;

    private InstanceState(String s) {
        text = s;
    }

    public static InstanceState fromString(String s) {
        for (InstanceState state : values()) {
            if (state.text.equalsIgnoreCase(s)) {
                return state;
            }
        }
        return UNKNOWN;
    }

}
