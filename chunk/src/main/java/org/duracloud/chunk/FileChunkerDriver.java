package org.duracloud.chunk;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.duracloud.chunk.error.NotFoundException;
import org.duracloud.chunk.writer.ContentWriter;
import org.duracloud.chunk.writer.DuracloudContentWriter;
import org.duracloud.chunk.writer.FilesystemContentWriter;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.error.ContentStoreException;

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
                              File toSpace,
                              Long chunkSize,
                              IOFileFilter filter,
                              ContentWriter writer,
                              boolean saveMD5s) throws NotFoundException {

        if (!fromDir.isDirectory()) {
            throw new DuraCloudRuntimeException("Invalid dir: " + fromDir);
        }

        FileChunker chunker = new FileChunker(writer, chunkSize);
        chunker.addContentFrom(fromDir, toSpace.getPath(), filter, saveMD5s);
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
                                "add content from dir:<f> to space:<t> of max" +
                                    " chunk size:<s> in units of K,M,G");
        add.setArgs(3);
        add.setArgName("f t s{K|M|G}");
        add.setValueSeparator(' ');

        Option filteredAdd = new Option("A",
                                        "filteredadd",
                                        true,
                                        "add content from " +
                                            "dir:<f> to space:<t> " +
                                            "of max chunk size:<s> " +
                                            "in units of K,M,G " +
                                            "with filename <filter>");
        filteredAdd.setArgs(4);
        filteredAdd.setArgName("f t s{K|M|G} filter");
        filteredAdd.setValueSeparator(' ');

        Option cloud = new Option("c",
                                  "cloudstore",
                                  true,
                                  "use cloud store found at <host>:<port> " +
                                      "as content dest");
        cloud.setArgs(2);
        cloud.setArgName("host:port");
        cloud.setValueSeparator(':');

        Option saveChunkMD5s = new Option("m",
                                          "saveChunkMD5s",
                                          false,
                                          "if this option is set, chunk " +
                                              "MD5s will be preserved in " +
                                              "the manifest");

        Options options = new Options();
        options.addOption(create);
        options.addOption(add);
        options.addOption(filteredAdd);
        options.addOption(cloud);
        options.addOption(saveChunkMD5s);

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
    public static void main(String[] args)
        throws IOException, NotFoundException, ContentStoreException {

        CommandLine cmd = parseArgs(args);

        // Where will content be written?
        ContentWriter writer;
        if (cmd.hasOption("cloudstore")) {
            String[] vals = cmd.getOptionValues("cloudstore");
            String host = vals[0];
            String port = vals[1];
            ContentStoreManager mgr = new ContentStoreManagerImpl(host, port);

            writer = new DuracloudContentWriter(mgr.getPrimaryContentStore());
        } else {
            writer = new FilesystemContentWriter();
        }

        // Will Chunk MD5's be preserved?
        boolean chunkMD5 = false;
        if (cmd.hasOption("saveChunkMD5s")) {
            chunkMD5 = true;
        }

        if (cmd.hasOption("add")) {
            String[] vals = cmd.getOptionValues("add");
            File fromDir = new File(vals[0]);
            File toDir = new File(vals[1]);
            Long chunkSize = getChunkSize(vals[2]);

            IOFileFilter filter = FileFilterUtils.trueFileFilter();
            chunk(fromDir, toDir, chunkSize, filter, writer, chunkMD5);

        } else if (cmd.hasOption("filteredadd")) {
            String[] vals = cmd.getOptionValues("filteredadd");
            File fromDir = new File(vals[0]);
            File toDir = new File(vals[1]);
            Long chunkSize = getChunkSize(vals[2]);
            IOFileFilter filter = new RegexFileFilter(".*" + vals[3] + ".*");

            chunk(fromDir, toDir, chunkSize, filter, writer, chunkMD5);

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
