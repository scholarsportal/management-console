package org.duracloud.common.util.chunk;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.duracloud.common.error.DuraCloudRuntimeException;

import java.io.File;
import java.io.IOException;

/**
 * This class is a commandline interface for initiating the read of local
 * content, chunking it, then writing it via a provided ContentWriter.
 *
 * @author Andrew Woods
 *         Date: Feb 5, 2010
 */
public class FileChunkerDriver {

    private static void chunk(File fromDir,
                              File toDir,
                              Long chunkSize,
                              IOFileFilter filter) {

        if (!fromDir.isDirectory()) {
            throw new DuraCloudRuntimeException("Invalid dir: " + fromDir);
        }

        if (!toDir.exists()) {
            toDir.mkdirs();
        }

        ContentWriter writer = new FilesystemContentWriter();
        FileChunker chunker = new FileChunker(writer, chunkSize);
        chunker.addContentFrom(fromDir, toDir.getPath(), filter);

    }

    private static Long getChunkSize(String arg) {
        char unit = arg.toLowerCase().charAt(arg.length() - 1);
        if (unit != 'k' && unit != 'm' && unit != 'g') {
            throw new DuraCloudRuntimeException(
                "Chunk size must be of the form: <digit(s)><K|M|G>");
        }

        int multiplier = Integer.parseInt(arg.substring(0, arg.length() - 1));

        final long KB = 1024;
        final long MB = 1048576;
        final long GB = 1073741824;

        long chunkSize = 1 * MB;
        switch (unit) {
            case 'k':
                chunkSize = multiplier * KB;
                break;
            case 'm':
                chunkSize = multiplier * MB;
                break;
            case 'g':
                chunkSize = multiplier * GB;
                break;
        }
        return chunkSize;
    }

    private static Options getOptions() {

        Option create = new Option("g",
                                   "generate",
                                   true,
                                   "generate test data to <outFile> of " +
                                       "<size> bytes");
        create.setArgs(2);
        create.setArgName("outFile numBytes");
        create.setValueSeparator(' ');

        Option add = new Option("a",
                                "add",
                                true,
                                "add content from <fromDir> to <toDir> " +
                                    "of max chunk size <chunkSize>" +
                                    "in units of K,M,G");
        add.setArgs(3);
        add.setArgName("fromDir toDir chunkSize[K|M|G]");
        add.setValueSeparator(' ');

        Option filteredAdd = new Option("A",
                                        "filteredadd",
                                        true,
                                        "add content from " +
                                            "<fromDir> to <toDir> " +
                                            "of max chunk size <chunkSize> " +
                                            "in units of K,M,G " +
                                            "with <filenameFilter>");
        filteredAdd.setArgs(4);
        filteredAdd.setArgName("fromDir toDir chunkSize[K|M|G] filenameFilter");
        filteredAdd.setValueSeparator(' ');

        Options options = new Options();
        options.addOption(create);
        options.addOption(add);
        options.addOption(filteredAdd);

        return options;
    }

    private static CommandLine parseArgs(String[] args) {
        CommandLineParser parser = new GnuParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(getOptions(), args);
        } catch (ParseException e) {
            System.err.println(e);
            die();
        }
        return cmd;
    }

    private static void usage() {
        HelpFormatter help = new HelpFormatter();
        help.setWidth(80);
        help.printHelp(FileChunker.class.getCanonicalName(), getOptions());
    }

    private static void die() {
        usage();
        System.exit(1);
    }

    /**
     * Main
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        CommandLine cmd = parseArgs(args);

        if (cmd.hasOption("add")) {
            String[] vals = cmd.getOptionValues("add");
            File fromDir = new File(vals[0]);
            File toDir = new File(vals[1]);
            Long chunkSize = getChunkSize(vals[2]);

            IOFileFilter filter = FileFilterUtils.trueFileFilter();
            chunk(fromDir, toDir, chunkSize, filter);

        } else if (cmd.hasOption("filteredadd")) {
            String[] vals = cmd.getOptionValues("filteredadd");
            File fromDir = new File(vals[0]);
            File toDir = new File(vals[1]);
            Long chunkSize = getChunkSize(vals[2]);
            IOFileFilter filter = new RegexFileFilter(".*" + vals[3] + ".*");

            chunk(fromDir, toDir, chunkSize, filter);

        } else if (cmd.hasOption("generate")) {
            String[] vals = cmd.getOptionValues("generate");
            File outFile = new File(vals[0]);
            long contentSize = Long.parseLong(vals[1]);

            FileChunker.createTestContent(outFile, contentSize);

        } else {
            usage();
        }
    }

}
