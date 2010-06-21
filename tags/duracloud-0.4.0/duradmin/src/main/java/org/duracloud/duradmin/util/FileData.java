/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradmin.util;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

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
    public File getData() throws IOException{
      if(this.tempFileName == null){
          return null;
      }
      return new File(tempFileName);        
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
