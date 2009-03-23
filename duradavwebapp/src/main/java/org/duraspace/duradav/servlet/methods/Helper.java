package org.duraspace.duradav.servlet.methods;

import javax.servlet.http.HttpServletRequest;

abstract class Helper {

    public static int getDepth(HttpServletRequest req, int defaultValue) {
        String depth = req.getHeader("Depth");
        if (depth == null) {
            return defaultValue;
        } else if (depth.equals("0")) {
            return 0;
        } else if (depth.equals("1")) {
            return 1;
        } else if (depth.equals("infinity")) {
            return -1;
        } else {
            throw new RuntimeException("Unrecognized Depth: " + depth);
        }
    }

}
