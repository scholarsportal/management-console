package org.duracloud.services.webapputil.tomcat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andrew Woods
 *         Date: Nov 30, 2009
 */
public class ServerXmlInputStream extends BufferedInputStream {

    private static int BUFFER_SIZE = 8192;
    private static int BASE_PORT = 8080;
    private int portOffset;

    private static final Pattern PORT_PATTERN = Pattern.compile("8\\d\\d\\d");


    public ServerXmlInputStream(InputStream input, int port) {
        super(input, BUFFER_SIZE);
        if (port < 1000) {
            throw new IllegalArgumentException("port must be greater than 0");
        }

        portOffset = port - BASE_PORT;
        super.buf = filteredContent();
        super.in = input;
        super.pos = 0;
        super.count = super.buf.length;
    }

    private byte[] filteredContent() {
        StringBuilder sb = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(this));
        String line = readLine(br);
        while (null != line) {
            Matcher m = PORT_PATTERN.matcher(line);
            StringBuffer newLine = new StringBuffer();
            while (m.find()) {
                String oldPort = line.substring(m.start(), m.start() + 4);
                Integer newPort = Integer.parseInt(oldPort) + portOffset;
                m.appendReplacement(newLine, newPort.toString());
            }

            if (newLine.length() > 0) {
                m.appendTail(newLine);
                line = newLine.toString();
            }

            sb.append(line);

            line = readLine(br);
        }

        return sb.toString().getBytes();
    }

    private String readLine(BufferedReader br) {
        String line = null;
        try {
            line = br.readLine();
        } catch (IOException e) {
            // do nothing.
        }
        return line;
    }

}
