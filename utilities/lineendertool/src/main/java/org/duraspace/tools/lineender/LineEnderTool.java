package org.duraspace.tools.lineender;

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
 * Line Ender Tool - Used to update the end of line characters in Java files
 *
 * @author: Bill Branan
 * Date: June 11, 2010
 */
public class LineEnderTool implements FileHandler {

    private File workingDir;
    private boolean test;
    private int updated = 0;
    private int noUpdate = 0;
    private int failed = 0;

    public LineEnderTool (File workingDir, boolean test) {
        this.workingDir = workingDir;
        this.test = test;
    }

    public void updateLineEnders() {
        System.out.println("-------------------------------------------------");
        System.out.println("About to update line enders in: " +
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
                if(fileContents.indexOf("\r\n") >= 0 ||
                   fileContents.indexOf("\r") >= 0) {
					System.out.println("File enders need to be updated for file " +
					                   file.getAbsolutePath());
					if(!test) {
						// Update file enders
						fileContents = fileContents.replaceAll("\r\n", "\n");
						fileContents = fileContents.replaceAll("\r", "\n");
                        FileUtils.writeStringToFile(file, fileContents, "UTF-8");
					}
                	updated++;
				} else {
					noUpdate++;
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
                      "line ending values");
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

        LineEnderTool tool = new LineEnderTool(workingDir, test);
        tool.updateLineEnders();
    }

}