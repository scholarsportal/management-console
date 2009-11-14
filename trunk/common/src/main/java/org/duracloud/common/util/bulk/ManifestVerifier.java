package org.duracloud.common.util.bulk;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.log4j.Logger;
import org.duracloud.common.util.error.ManifestVerifyException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class compares two manifest files for
 * equal size and
 * checksum/entryname mappings
 * The expected format of the input files is
 * <checksum><whitespace><entryname>
 *
 * @author Andrew Woods
 *         Date: Oct 24, 2009
 */
public class ManifestVerifier {

    private final Logger log = Logger.getLogger(this.getClass());

    private File file0;
    private File file1;

    private Map<String, String> entries0; // filename -> checksum
    private Map<String, String> entries1;

    private List<String> filters;

    private List<String> validPairs;
    private List<String> cksumMismatchPairs;


    public ManifestVerifier(File file0, File file1) {
        this.file0 = file0;
        this.file1 = file1;
        entries0 = new HashMap<String, String>();
        entries1 = new HashMap<String, String>();
        filters = new ArrayList<String>();
        validPairs = new ArrayList<String>();
        cksumMismatchPairs = new ArrayList<String>();
    }

    public void report(OutputStream out) {
        StringBuilder sb = new StringBuilder();
        sb.append("=======================\n");
        sb.append("Total entries: " + entries0.size() + "\n");
        sb.append("Valid entries: " + validPairs.size() + "\n");
        sb.append("Error entries: " + cksumMismatchPairs.size() + "\n");

        sb.append("-----------------------\n\n");
        sb.append("All entries:\n");
        sb.append("-----------------------\n");
        for (String name : entries0.keySet()) {
            sb.append("\t" + name + "\t" + entries0.get(name) + "\n");
        }

        sb.append("-----------------------\n\n");
        sb.append("Valid entries:\n");
        sb.append("-----------------------\n");
        for (String entry : validPairs) {
            sb.append("\t" + entry + "\n");
        }

        sb.append("-----------------------\n\n");
        sb.append("Error entries:\n");
        sb.append("-----------------------\n");
        for (String entry : cksumMismatchPairs) {
            sb.append("\t" + entry + "\n");
        }
        sb.append("\n=======================\n\n");


        try {
            out.write(sb.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method checks the provided manifest files for:
     * same number of manifest entries
     * equal checksums per entry
     *
     * @param filters List of names that if found in the manifests will be ignored.
     * @throws ManifestVerifyException if files differ in size or checksums
     */
    public void verify(String... filters) throws ManifestVerifyException {
        if (filters != null) {
            this.filters = Arrays.asList(filters);
            logFilters();
        }
        verify();
    }


    /**
     * This method checks the provided manifest files for:
     * same number of manifest entries
     * equal checksums per entry
     *
     * @throws ManifestVerifyException if files differ in size or checksums
     */
    public void verify() throws ManifestVerifyException {
        loadEntries();
        verifyEntryCount();
        verifyChecksums();
    }

    private void loadEntries() {
        loadEntries(file0, entries0);
        loadEntries(file1, entries1);
    }

    private void loadEntries(File file, Map<String, String> entries) {
        InputStream input = getInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(input));

        String line = readLine(br);
        while (line != null) {
            if (!isFiltered(line)) {
                addEntry(line, entries);
            }
            line = readLine(br);
        }
    }

    private boolean isFiltered(String line) {
        for (String filter : filters) {
            if (line.indexOf(filter) != -1) {
                return true;
            }
        }
        return false;
    }

    private void addEntry(String line, Map<String, String> entries) {
        String[] cksumFilenamePair = line.split("\\s");
        if (cksumFilenamePair == null || cksumFilenamePair.length != 2) {
            throw new RuntimeException("Invalid manifest file.");
        }

        entries.put(cksumFilenamePair[1], cksumFilenamePair[0]);
    }

    private InputStream getInputStream(File file) {
        try {
            return new AutoCloseInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String readLine(BufferedReader br) {
        try {
            return br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void verifyEntryCount() throws ManifestVerifyException {
        int size0 = entries0.size();
        int size1 = entries1.size();
        if (size0 != size1) {
            throw new ManifestVerifyException(file0.getName(),
                                              file1.getName(),
                                              size0,
                                              size1);
        }
    }

    private void verifyChecksums() throws ManifestVerifyException {
        for (String name : entries0.keySet()) {

            String cksum0 = entries0.get(name);
            String cksum1 = entries1.get(name);
            if (cksum1 == null) {
                this.cksumMismatchPairs.add(name + ":\n\t\t" + cksum0 + ":null");

            } else if (!cksum0.equals(cksum1)) {
                this.cksumMismatchPairs.add(
                    name + ":\n\t\t" + cksum0 + " != \n\t\t" + cksum1);
            } else {
                this.validPairs.add(name + "\t" + cksum0);
            }
        }

        if (!this.cksumMismatchPairs.isEmpty()) {
            throw new ManifestVerifyException(file0.getName(),
                                              file1.getName(),
                                              cksumMismatchPairs);
        }
    }

    private void logFilters() {
        StringBuilder sb = new StringBuilder();
        if (filters.size() > 0) {
            sb.append("Filters: [");

            for (String filter : filters) {
                sb.append("|" + filter);
            }
            sb.append("|]");
        } else {
            sb.append("NO-FILTERS");
        }

        log.info(sb.toString());
    }

}
