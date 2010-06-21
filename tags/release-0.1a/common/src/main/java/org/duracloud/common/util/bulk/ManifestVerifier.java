package org.duracloud.common.util.bulk;

import org.duracloud.common.util.error.ManifestVerifyException;
import org.apache.commons.io.input.AutoCloseInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

    private String file0;
    private String file1;

    private Map<String, String> entries0; // filename -> checksum
    private Map<String, String> entries1;

    private List<String> cksumMismatchPairs;


    ManifestVerifier(String file0, String file1) {
        this.file0 = file0;
        this.file1 = file1;
        entries0 = new HashMap<String, String>();
        entries1 = new HashMap<String, String>();
        cksumMismatchPairs = new ArrayList<String>();
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

    private void loadEntries(String filename, Map<String, String> entries) {
        InputStream input = getInputStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(input));

        String line = readLine(br);
        while (line != null) {
            addEntry(line, entries);
            line = readLine(br);
        }
    }

    private void addEntry(String line, Map<String, String> entries) {
        String[] cksumFilenamePair = line.split("\\s");
        if (cksumFilenamePair == null || cksumFilenamePair.length != 2) {
            throw new RuntimeException("Invalid manifest file.");
        }

        entries.put(cksumFilenamePair[1], cksumFilenamePair[0]);
    }

    private InputStream getInputStream(String file) {
        try {
            return new AutoCloseInputStream(new FileInputStream(new File(file)));
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
            throw new ManifestVerifyException(file0, file1, size0, size1);
        }
    }

    private void verifyChecksums() throws ManifestVerifyException {
        for (String name : entries0.keySet()) {

            String cksum0 = entries0.get(name);
            String cksum1 = entries1.get(name);
            if (cksum1 == null) {
                this.cksumMismatchPairs.add(name + ":\n\t" + cksum0 + ":null");
            } else if (!cksum0.equals(cksum1)) {
                this.cksumMismatchPairs.add(
                    name + ":\n\t" + cksum0 + " != \n\t" + cksum1);
            }
        }

        if (!this.cksumMismatchPairs.isEmpty()) {
            throw new ManifestVerifyException(file0, file1, cksumMismatchPairs);
        }
    }

}
