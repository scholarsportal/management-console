/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradav.servlet.methods;

enum Depth {

    ZERO ("0"),
    ONE ("1"),
    INFINITY ("infinity");

    public static final String HEADER = "Depth";

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
