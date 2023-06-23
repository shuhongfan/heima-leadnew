package com.heima.minio.test;

import com.heima.file.service.FileStorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest(classes = MinIOStaterTest.class)
@RunWith(SpringRunner.class)
public class MinIOStaterTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Test
    public void testUpdateImgFile() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\shuho\\IdeaProjects\\heima-leadnew\\heima-leadnews\\heima-leadnews-test\\freemarker-demo\\list.html");

        String filePath = fileStorageService.uploadHtmlFile("", "list.html", fileInputStream);
        System.out.println(filePath);
    }
}
