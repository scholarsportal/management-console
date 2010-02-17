
package org.duracloud.duradmin.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileData
        implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String name;

    private String mimetype;
    
    
    private String tempFileName;
    
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
    
    /**
     * 
     * @return null if the multipart file has not been set.
     * @throws IOException
     */
    public byte[] getData() throws IOException{
      if(this.tempFileName == null){
          return null;
      }
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      InputStream is = new FileInputStream(new File(tempFileName));
      int read = -1;
      byte[] buf = new byte[1024];
      try {
          while ((read = is.read(buf)) > -1) {
              os.write(buf, 0, read);
          }
          return os.toByteArray();
      } finally {
          os.close();
          is.close();
      }
        
    }

    public void dereferenceFileData(){
        if(this.tempFileName != null){
            File file = new File(this.tempFileName);
            if(file.exists()){
                file.delete();
            }
            
            this.tempFileName = null;
        }
    }

    public void setFile(MultipartFile file) throws IOException {
        if (file == null
                || !StringUtils.hasText(file
                        .getOriginalFilename())) {
            return;
        }

        setName(file.getOriginalFilename());
        setMimetype(file.getContentType());
        File tempFile = File.createTempFile("tmp-content-item-upload-", null);
        tempFile.deleteOnExit();
        file.transferTo(tempFile);
        this.tempFileName = tempFile.getCanonicalPath();
    }
}
