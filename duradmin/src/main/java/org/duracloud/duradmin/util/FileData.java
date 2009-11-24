
package org.duracloud.duradmin.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileData
        implements Serializable {

    private String name;

    private String mimetype;

    private byte[] data = new byte[0];

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getSize() {
        return this.data.length;
    }

    public void setFile(MultipartFile file) throws IOException {
        if (file == null
                || !StringUtils.hasText(file
                        .getOriginalFilename())) {
            return;
        }

        setName(file.getOriginalFilename());
        setMimetype(file.getContentType());

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        InputStream is = file.getInputStream();
        int read = -1;
        byte[] buf = new byte[1024];
        try {
            while ((read = is.read(buf)) > -1) {
                os.write(buf, 0, read);
            }
            setData(os.toByteArray());
        } finally {
            os.close();
            is.close();
        }

    }

}
