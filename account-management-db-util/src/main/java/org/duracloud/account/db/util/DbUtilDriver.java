/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util;

import java.io.File;
import java.io.IOException;

/**
 * This class is the starting point for the account management database
 * utility. This tool provides a way to retrieve and store data in the
 * database which sits behind the Account Management App without the
 * need of a UI. There are four modes in which this tool can be run:
 *
 * GET - This mode pulls down the information stored in the database. A new
 *       file is created in the work directory for each domain (table) in the
 *       database, and all items (rows) are stored in XML markup.
 *
 * PUT - This mode pushes the information contained in the files in the work
 *       directory into the database, overwriting any entries which already
 *       exist. The best way to use this feature is to perform a GET first,
 *       then edit the files which were produced to add/update any entries.
 *
 * CLEAR - This mode clears out all of the items (rows) in all of the database
 *         domains (tables). As a safety precaution, a GET is performed prior
 *         to the CLEAR, so that you have a backup of the data which was in
 *         the database prior to the clear.
 *
 * Usage Notes:
 *
 * - If you want to remove items (rows) from the database, you can perform a
 *   CLEAR, then edit the files which were created in the GET, followed by a
 *   PUT. This will ensure that only the items (rows) included in the files
 *   are in the database.
 *
 * - A PUT will only perform updates based on what is included in the files in
 *   the work directory. If you only want to work with a single domain (table),
 *   you can perform a GET, then delete all of the files produced other than
 *   the one you'd like to edit.
 *
 * - Performing a GET will overwrite any files produced on a previous run if
 *   it is pointed to the same work directory. This is also the case for the
 *   GET which is performed automatically prior to a CLEAR. Be careful not to
 *   overwrite your edited files when performing either of these functions.
 * 
 * - When performing a PUT command, the items (rows) to be added are compared
 *   to the existing database. If an item (row) already exists and all of the
 *   values are equal, no update occurs (and the database counter is not
 *   updated, as this only occurs when an item is changed.)
 * 
 * @author Bill Branan
 * Date: Dec 21, 2010
 */
public class DbUtilDriver {   

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            usage("Three arguments are required, you supplied: " + args.length);
            System.exit(1);
        }

        DbUtil.COMMAND command = null;
        String commandArg = args[0];
        if(commandArg.equalsIgnoreCase(DbUtil.COMMAND.GET.name())) {
            command = DbUtil.COMMAND.GET;
        } else if(commandArg.equalsIgnoreCase(DbUtil.COMMAND.PUT.name())) {
            command = DbUtil.COMMAND.PUT;
        } else if(commandArg.equalsIgnoreCase(DbUtil.COMMAND.CLEAR.name())) {
            command = DbUtil.COMMAND.CLEAR;
        } else {
            usage("The first argument must be either GET, PUT, or CLEAR. " +
                  "You supplied: " + commandArg);
            System.exit(1);
        }

        File credsFile = new File(args[1]);
        if (!credsFile.exists()) {
            usage("Credential file does not exist: " + credsFile.getPath());
            System.exit(1);
        }

        File workDir = new File(args[2]);
        if (!workDir.exists()) {
            usage("The work directory must exist: " + workDir.getPath());
            System.exit(1);
        } else if(!workDir.isDirectory()) {
            usage("The work directory must be a directory: " + workDir.getPath());
            System.exit(1);
        }

        DbUtil dbUtil = new DbUtil(credsFile, workDir);
        dbUtil.runCommand(command);
    }

    private static void usage(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("Error: " + msg);
        sb.append("\n\n");
        sb.append("Usage: ");
        sb.append("\n\t");
        sb.append("DbUtilDriver [get/put/clear] [credentials-file] [work-dir]");
        sb.append("\n\n\t\t");
        sb.append("GET - retrieves and stores all db data in work-dir");
        sb.append("\n\n\t\t");
        sb.append("PUT - updates db based on files in work-dir");
        sb.append("\n\n\t\t");
        sb.append("CLEAR - performs a GET, then removes all data from db");        
        sb.append("\n\n\t");
        sb.append("where [get/put/clear] is one of get, put, or clear commands");
        sb.append("\n\n\t");
        sb.append("where [credentials-file] is an xml file containing provider");
        sb.append("\n\n\t\t");
        sb.append("credentials, this file can be the same as the file used to");
        sb.append("\n\n\t\t");
        sb.append("initialize the AMA");
        sb.append("\n\n\t");
        sb.append("where [work-dir] is a directory to which db data will be");
        sb.append("\n\n\t\t");
        sb.append("written, in the case of a get, or from which data will be");
        sb.append("\n\n\t\t");
        sb.append("read, in the case of a put or clear");

        System.out.println(sb.toString());
    }    
}
