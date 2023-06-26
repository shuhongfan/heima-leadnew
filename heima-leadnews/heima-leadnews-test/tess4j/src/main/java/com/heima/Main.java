package com.heima;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class Main {
    public static void main(String[] args) throws TesseractException {
//        创建实例
        Tesseract tesseract = new Tesseract();

//        设置字体库路径
        tesseract.setDatapath("C:\\Users\\shuho\\IdeaProjects\\heima-leadnew\\heima-leadnews\\heima-leadnews-test\\tess4j\\src\\main\\resources");

//        设置语言
        tesseract.setLanguage("chi_sim");

        File file = new File("C:\\Users\\shuho\\IdeaProjects\\heima-leadnew\\heima-leadnews\\heima-leadnews-test\\tess4j\\src\\main\\resources\\1.png");

//        识别图片
        String result = tesseract.doOCR(file);
        System.out.println("识别的结果为：" + result.replaceAll("\\r|\\n","-").replaceAll(" ",""));
    }
}