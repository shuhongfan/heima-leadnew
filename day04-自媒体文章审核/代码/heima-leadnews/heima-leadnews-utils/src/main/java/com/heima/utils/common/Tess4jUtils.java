package com.heima.utils.common;

import lombok.SneakyThrows;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import java.awt.image.BufferedImage;

public class Tess4jUtils {

    @SneakyThrows
    public static String doOCR(BufferedImage imageFile) {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("D:\\workspace\\tessdata");
        tesseract.setLanguage("chi_sim");//中文识别
        return tesseract.doOCR(imageFile).replaceAll("\\r|\\n", "-");
    }
}
