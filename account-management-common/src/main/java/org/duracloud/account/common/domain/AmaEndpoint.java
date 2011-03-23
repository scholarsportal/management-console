/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

/**
 * @author Andrew Woods
 *         Date: 3/21/11
 */
public class AmaEndpoint {

    private static String host = "localhost";
    private static String port = "8080";
    private static String ctxt = "ama";

    public static void initialize(String h, String p, String c) {
        host = h;
        port = p;
        ctxt = c;
    }

    public static String getHost() {
        return host;
    }

    public static String getCtxt() {
        return ctxt;
    }

    public static String getPort() {
        return port;
    }
}
