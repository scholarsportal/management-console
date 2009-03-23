package org.duraspace.duradav.servlet.methods;

enum Depth {

    ZERO ("0"),
    ONE ("1"),
    INFINITY ("infinity");

    private final String string;

    Depth(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

    public static Depth parse(String headerValue, Depth defaultDepth) {
        if (headerValue == null) return defaultDepth;
        for (Depth depth : values()) {
            if (headerValue.equalsIgnoreCase(depth.toString())) return depth;
        }
        return null;
    }

}
