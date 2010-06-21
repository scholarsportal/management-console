package org.duraspace.tools.fileheader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;

import java.io.File;

/*
 * File Header Tool - Used to update file headers for Java files
 *
 * @author: Bill Branan
 * Date: June 11, 2010
 */
public class FileHeaderTool implements FileHandler {

    private static final String HEADER =
        "/*\n" +
        " * The contents of this file are subject to the license and copyright\n" +
        " * detailed in the LICENSE and NOTICE files at the root of the source\n" +
        " * tree and available online at\n" +
        " *\n" +
        " *     http://duracloud.org/license/\n" +
        " */\n";

    private File workingDir;
    private boolean test;
    private int updated = 0;
    private int noUpdate = 0;
    private int failed = 0;

    public FileHeaderTool (File workingDir, boolean test) {
        this.workingDir = workingDir;
        this.test = test;
    }

    public void updateFileHeaders() {
        System.out.println("-------------------------------------------------");
        System.out.println("About to update file headers in: " +
            workingDir.getAbsolutePath());
        if(test) {
            System.out.println("This is a TEST, no updates will be made");
        }
        System.out.println("-------------------------------------------------");

        DirWalker walker = new DirWalker(workingDir, this);
        walker.walkDirs();

        System.out.println("-------------------------------------------------");

        System.out.println("Files Successfully Updated: " + updated);

        if(noUpdate > 0) {
            System.out.println("Files With No Updates Needed: " + noUpdate);
        }

        if(failed > 0) {
            System.out.println("Files Failed: " + failed);
        }
    }

    @Override
    public void handleFile(File file) {
        if(file.getName().endsWith(".java")) {
            String filePath = file.getAbsolutePath();
            try {
                String fileContents = FileUtils.readFileToString(file, "UTF-8");
                if(fileContents.startsWith(HEADER)) {
                    System.out.println("Header found, no update needed, " +
                                       "in file: " + filePath);
                    noUpdate++;
                } else {
                    System.out.println("Adding header to file: " + filePath);
                    if(!test) {
                        int start = fileContents.indexOf("package");
                        if(start >= 0) {
                            String newContents =
                                HEADER + fileContents.substring(start);
                            FileUtils.writeStringToFile(file,
                                                        newContents,
                                                        "UTF-8");
                        } else {
                            throw new Exception("File does not contain " +
                                                "a package declaration!");
                        }
                    }
                    updated++;
                }
            } catch(Exception e) {
                failed++;
                System.out.println("ERROR: Unable to update file: " + filePath +
                                   " due to " + e.getMessage());
            }
        }
    }

    private static void usage(Options cmdOptions) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Running the File Header Tool", cmdOptions);
        System.exit(1);
    }

    public static void main(String[] args) {
        Options cmdOptions = new Options();
        File workingDir = null;
        boolean test = false;

        Option dirOption =
           new Option("d", "directory", true,
                      "The top level directory under which all files of " +
                      "type *.java will be updated to have the proper " +
                      "header value");
        dirOption.setRequired(true);
        cmdOptions.addOption(dirOption);

        Option testOption =
           new Option("t", "test", false,
                      "Determines if the tool should run in test mode. In " +
                      "test mode no files are updated, but console output " +
                      "indicates which files will be updated.");
        testOption.setRequired(false);
        cmdOptions.addOption(testOption);

        CommandLine cmd = null;
        try {
            CommandLineParser parser = new PosixParser();
            cmd = parser.parse(cmdOptions, args);
        } catch(ParseException e) {
            System.out.println(e.getMessage());
            usage(cmdOptions);
        }

        if(cmd.hasOption("d")) {
            try {
                String dir = cmd.getOptionValue("d");
                workingDir = new File(dir);
                if(!(workingDir.exists() && workingDir.isDirectory())) {
                    throw new RuntimeException("The directory " + dir +
                                               " does not exist");
                }
            } catch(Exception e) {
                System.out.println(e.getMessage());
                usage(cmdOptions);
            }
        } else {
            usage(cmdOptions);
        }

        if(cmd.hasOption("t")) {
            test = true;
        } else {
            test = false;
        }

        FileHeaderTool tool = new FileHeaderTool(workingDir, test);
        tool.updateFileHeaders();
    }

}