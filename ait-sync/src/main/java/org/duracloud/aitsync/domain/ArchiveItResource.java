package org.duracloud.aitsync.domain;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.duracloud.aitsync.watcher.Resource;

/**
 * 
 * @author Daniel Bernstein
 * @created 12/17/2012
 * 
 */
public class ArchiveItResource implements Resource {
    private String md5;
    private String filename;
    private String date;
    private static Pattern PATTERN = Pattern.compile(".*[-]([0-9]{14})[-]*.");
    private Long accountId;
    private static SimpleDateFormat FORMAT =
        new SimpleDateFormat("yyyyMMddHHmmss");

    public ArchiveItResource(Long accountId, String filename, String md5) {
        super();
        this.accountId = accountId;
        this.filename = filename;
        this.md5 = md5;
        this.date = getDateString();
    }
    
    public InputStream getInputStream() throws IOException{
        return toURL().openStream();
    }
    
    public Long getGroupId(){
        return this.accountId;
    }
    
    public String getFilename() {
        return filename;
    }

    public URL toURL() {
        try {
            return new URL("https://partner.archive-it.org/cgi-bin/getarcs.pl/"
                + getFilename());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMd5() {
        return md5;
    }

    @Override
    public Date getCreatedDate() {
        try {
            return FORMAT.parse(this.date);
        } catch (ParseException e) {
            throw new RuntimeException("unable to parse date string: "
                + this.date, e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArchiveItResource) {
            ArchiveItResource other = (ArchiveItResource) obj;
            if (this.filename.equals(other.filename)
                && this.md5.equals(other.md5)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.filename.hashCode() + this.md5.hashCode();
    }

    @Override
    public int compareTo(Resource o) {
            return getCreatedDate().compareTo(o.getCreatedDate());
    }

    private String getDateString() {
        Matcher m = PATTERN.matcher(this.filename);

        if (m.find()) {
            return m.group(1);
        } else {
            return "";
        }

    }
}
