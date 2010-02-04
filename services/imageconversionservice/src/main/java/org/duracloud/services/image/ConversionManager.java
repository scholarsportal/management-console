package org.duracloud.services.image;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.duracloud.client.ContentStore;
import org.duracloud.domain.Content;
import org.duracloud.error.ContentStoreException;
import org.duracloud.error.NotFoundException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Handles the conversion of image files from one format to another.
 * Image files are pulled from the source space, converted locally, and the
 * result is stored in the destination space.
 *
 * @author Bill Branan
 *         Date: Jan 28, 2010
 */
public class ConversionManager {

    private boolean continueConversion = true;
    private boolean conversionComplete = false;
    private List<String> successfulConversions;
    private Map<String, String> unsuccessfulConversions;
    private Map<String, String> extMimeMap;

    private ContentStore contentStore;
    private File workDir;
    private String toFormat;
    private String sourceSpaceId;
    private String destSpaceId;
    private String namePrefix;
    private String nameSuffix;

    private String convertScript;

    public ConversionManager(ContentStore contentStore,
                             File workDir,
                             String toFormat,
                             String sourceSpaceId,
                             String destSpaceId,
                             String namePrefix,
                             String nameSuffix) {
        this.contentStore = contentStore;
        this.workDir = workDir;
        this.toFormat = toFormat;
        this.sourceSpaceId = sourceSpaceId;
        this.destSpaceId = destSpaceId;
        this.namePrefix = namePrefix;
        this.nameSuffix = nameSuffix;

        successfulConversions = new ArrayList<String>();
        unsuccessfulConversions = new HashMap<String, String>();
        extMimeMap = new HashMap<String, String>();
        loadExtMimeMap();
    }

    private void loadExtMimeMap() {
        // Load supported file types: gif, jpg, png, tiff, jp2, bmp, pdf, psd
        extMimeMap.put("default", "application/octet-stream");
        extMimeMap.put("gif", "image/gif");
        extMimeMap.put("jpg", "image/jpeg");
        extMimeMap.put("png", "image/png");
        extMimeMap.put("tiff", "image/tiff");
        extMimeMap.put("jp2", "image/jp2");
        extMimeMap.put("bmp", "image/bmp");
        extMimeMap.put("pdf", "application/pdf");
        extMimeMap.put("psd", "image/psd");
    }

    public void startConversion() {
        printStartMessage();

        workDir.setWritable(true);

        // Get content list from space where source images reside (limit to prefix)
        Iterator<String> contentIds;
        try {
            contentIds =
                contentStore.getSpaceContents(sourceSpaceId, namePrefix);
        } catch(ContentStoreException e) {
            throw new RuntimeException("Conversion could not be started due" +
                                       " to error: " + e.getMessage(), e);
        }

        // Ensure that the destination space exists
        try {
            checkDestSpace();
        } catch(ContentStoreException e) {
            String err = "Could not access destination space " + destSpaceId +
                         "due to error: " + e.getMessage();
            throw new RuntimeException(err, e);
        }

        while (continueConversion && contentIds.hasNext()) {
            String contentId = contentIds.next();

            // Perform conversion for files matching suffix
            if(fileMatchesSuffix(contentId)) {
                try {
                    performConversion(contentId);
                    successfulConversions.add(contentId);
                } catch(Exception e) {
                    unsuccessfulConversions.put(contentId, e.getMessage());
                }
            }
        }

        // Write conversion results file to target dir
        try {
            storeConversionResults();
        } catch(ContentStoreException e) {
            throw new RuntimeException("Could not store conversion results " +
                                       "due to error: " + e.getMessage(), e);
        }

        // Indicate that the conversion is complete
        conversionComplete = true;

        printEndMessage();
    }

    private void printStartMessage() {
        StringBuffer startMsg = new StringBuffer();
        startMsg.append("Starting Image Conversion. Image source space: ");
        startMsg.append(sourceSpaceId);
        startMsg.append(". Image destination space: ");
        startMsg.append(destSpaceId);
        startMsg.append(". Converting to format: '");
        startMsg.append(toFormat);
        startMsg.append("'. Name prefix: '");
        startMsg.append(namePrefix);
        startMsg.append("'. Name suffix: '");
        startMsg.append(nameSuffix);
        startMsg.append("'.");

        // TODO: Convert to log msg
        System.out.println(startMsg.toString());
    }

    private void printEndMessage() {
        // TODO: Convert to log msg
        System.out.println(getConversionStatus());
    }

    protected boolean fileMatchesSuffix(String contentId) {
        boolean fileMatchSuffix = false;
        if (nameSuffix != null && !nameSuffix.equals("")) {
            if (contentId.endsWith(nameSuffix)) {
                fileMatchSuffix = true;
            }
        } else {
            fileMatchSuffix = true;
        }
        return fileMatchSuffix;
    }

    protected void performConversion(String contentId)
        throws IOException, ContentStoreException {
        // Stream the content item down to the work directory
        Content sourceContent =
            contentStore.getContent(sourceSpaceId, contentId);
        InputStream sourceStream = sourceContent.getStream();
        File sourceFile = writeSourceToFile(sourceStream, contentId);

        // Perform conversion
        File convertedFile = convertImage(sourceFile);

        // Stream converted file to destination space
        FileInputStream convertedFileStream =
            new FileInputStream(convertedFile);
        String mimetype = extMimeMap.get(toFormat);
        if(mimetype == null) {
            mimetype = extMimeMap.get("default");
        }

        // Store the converted file in the destination space
        contentStore.addContent(destSpaceId,
                                convertedFile.getName(),
                                convertedFileStream,
                                convertedFile.length(),
                                mimetype,
                                sourceContent.getMetadata());

        // Delete source and converted files from work directory
        if(!sourceFile.delete()) {
            sourceFile.deleteOnExit();
        }

        convertedFileStream.close();
        if(!convertedFile.delete()) {
            convertedFile.deleteOnExit();
        }
    }

    private void checkDestSpace() throws ContentStoreException {
        // Create the destination space if it does not exist
        try {
            contentStore.getSpaceMetadata(destSpaceId);
        } catch (NotFoundException e) {
            contentStore.createSpace(destSpaceId, null);
        }
    }

    protected File writeSourceToFile(InputStream sourceStream,
                                     String fileName) throws IOException {
        File sourceFile = new File(workDir, fileName);
        if(sourceFile.exists()) {
            sourceFile.delete();
        }
        sourceFile.createNewFile();
        FileOutputStream sourceOut = new FileOutputStream(sourceFile);

        long sizeCopied = IOUtils.copyLarge(sourceStream, sourceOut);
        if(sizeCopied <= 0) {
            throw new IOException("Unable to copy any bytes from file " +
                fileName);
        }

        sourceOut.close();
        return sourceFile;
    }

    /*
     * Converts a local image to a given format using ImageMagick.
     * Returns the name of the converted image.
     */
    protected File convertImage(File sourceFile) throws IOException {
        String fileName = sourceFile.getName();
        // TODO: Convert to log msg
        System.out.println("Converting " + fileName + " to " + toFormat);

        ProcessBuilder pb =
            new ProcessBuilder(getConvertScript(), toFormat, fileName);
        pb.directory(workDir);
        Process p = pb.start();

        try {
            p.waitFor();  // Wait for the conversion to complete
        } catch (InterruptedException e) {
            throw new IOException("Conversion process interruped for " +
                fileName, e);
        }

        String convertedFileName = FilenameUtils.getBaseName(fileName);
        convertedFileName += "." + toFormat;
        File convertedFile = new File(workDir, convertedFileName);
        if(convertedFile.exists()) {
            return convertedFile;
        } else {
            throw new IOException("Could not find converted file: " +
                convertedFileName);
        }
    }

    private String getConvertScript() throws IOException {
        if(convertScript == null) {
            convertScript = createScript();
        }
        return convertScript;
    }

    private String createScript() throws IOException {
        String fileName;
        List<String> scriptLines = new ArrayList<String>();

        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().indexOf("windows") >= 0) { // windows
            fileName = "convert.bat";
            scriptLines.add("mogrify -format %1 %2");
        } else { // linux
            fileName = "convert.sh";
            scriptLines.add("#!/bin/bash");
            scriptLines.add("mogrify -format $1 $2");            
        }

        File scriptFile = new File(workDir, fileName);
        FileUtils.writeLines(scriptFile, scriptLines);
        scriptFile.setExecutable(true);
        return scriptFile.getAbsolutePath();
    }

    protected void storeConversionResults() throws ContentStoreException {
        String results = collectConversionResults();

        Date now = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String resultsId = "Conversion-Results-" + dateFormat.format(now);

        byte[] resultsBytes;
        try {
            resultsBytes = results.getBytes("UTF-8");
        } catch(UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        ByteArrayInputStream resultsStream =
            new ByteArrayInputStream(resultsBytes);

        contentStore.addContent(destSpaceId,
                                resultsId,
                                resultsStream,
                                resultsBytes.length,
                                "text/plain",
                                null);
    }

    protected String collectConversionResults() {
        StringBuilder results = new StringBuilder();
        if(continueConversion) {
            results.append("Conversion Process Completed. ");
        } else {
            results.append("Conversion Process Interrupted. ");
        }
        results.append("Results for completed conversions:\n\n");

        results.append("Successfully converted content items:\n");
        for(String contentId : successfulConversions) {
            results.append(contentId);
            results.append("\n");
        }
        results.append("\n");

        results.append("Unable to convert content items:\n");
        for(String contentId : unsuccessfulConversions.keySet()) {
            results.append(contentId);
            String error = unsuccessfulConversions.get(contentId);
            results.append(", due to error: ");
            results.append(error);
            results.append("\n");
        }
        return results.toString();
    }

    public String getConversionStatus() {
        String status = "Conversion In Progress";
        if(conversionComplete) {
            status =  "Conversion Complete";
        }
        return status +
            ". Successful convertions: " + successfulConversions.size() +
            ". Unsuccessful conversions: " + unsuccessfulConversions.size();
    }

    public void stopConversion() {
        // Indicate that conversion should stop after the
        // current file is completed
        continueConversion = false;
    }
}
