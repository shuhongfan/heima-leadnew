package com.heima.wemedia.service;

import com.heima.file.service.FileStorageService;
import com.heima.utils.common.Tess4jUtils;
import com.heima.wemedia.WemediaApplication;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;


@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class WmNewsAutoScanServiceTest {

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    @Test
    public void autoScanWmNews() {

        wmNewsAutoScanService.autoScanWmNews(6240);
    }

    @Autowired
    private FileStorageService fileStorageService;

    @SneakyThrows
    @Test
    public void testTess4j() {
        //下载文件
        byte[] bytes = fileStorageService.downLoadFile("http://192.168.200.130:9000/leadnews/2021/05/21/8bde311fa21448b18a8ca2378610b16d.png");
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        BufferedImage imageFile = ImageIO.read(in);
        //识别文件
        String result = Tess4jUtils.doOCR(imageFile);
        System.out.println("识别内容：------------------");
        System.out.println(result);
    }
}