
package org.duracloud.duradmin.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.junit.Assert.*;

public class FileDataTest {

    @Before
    public void setUp() throws Exception {
    }

    public static MultipartFile createTestFile(String filename,
                                               String mimetype,
                                               byte[] buffer)
            throws IOException {
        return new MockMultipartFile(filename,
                                     filename,
                                     mimetype,
                                     new ByteArrayInputStream(buffer));
    }

    @Test
    public void test() throws Exception {
        byte[] contents = "this is the contents of the file".getBytes();
        MultipartFile multipart =
                FileDataTest.createTestFile("test.txt",
                                            "plain/text",
                                            contents);

        FileData fileData = new FileData();
        fileData.setFile(multipart);
        Assert.assertEquals(multipart.getOriginalFilename(), fileData.getName());
        Assert.assertEquals(multipart.getContentType(), fileData.getMimetype());
        Assert.assertEquals(contents.length,  fileData.getData().length);
        
    }
}
