/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util;

import java.io.File;
import java.io.IOException;

/**
 * This class is the starting point for the account management database
 * convertion utility. This tool provides a way to update data from the
 * Account table to split it into data for the Account table and
 * ServerDetails table
 * 
 * @author: Bill Branan
 * Date: Feb 10, 2012
 */
public class DbConverterDriver {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage("One argument is required, you supplied: " + args.length);
            System.exit(1);
        }

        File workDir = new File(args[0]);
        if (!workDir.exists()) {
            usage("The work directory must exist: " + workDir.getPath());
            System.exit(1);
        } else if(!workDir.isDirectory()) {
            usage("The work directory must be a directory: " + workDir.getPath());
            System.exit(1);
        }

        DbConverter dbConverter = new DbConverter(workDir);
        dbConverter.run();
    }

    private static void usage(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("Error: " + msg);
        sb.append("\n\n");
        sb.append("Usage: ");
        sb.append("\n\t");
        sb.append("DbConverterDriver [work-dir]");
        sb.append("\n\n\t");
        sb.append("where [work-dir] is a directory from which db data to be");
        sb.append("\n\t\t");
        sb.append("converted can be read, and where converted db data can be");
        sb.append("\n\t\t");
        sb.append("written.");

        System.out.println(sb.toString());
    }    
}
