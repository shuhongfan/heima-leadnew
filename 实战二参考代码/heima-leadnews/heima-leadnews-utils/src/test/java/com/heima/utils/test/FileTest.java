package com.heima.utils.test;

import java.io.File;

public class FileTest {

    public static void main(String[] args) {
        String path = "F:\\黑马头条-v2.3\\04-项目部署 持续集成发布Jenkins+Git+Docker\\03-视频"; // 路径
        File f = new File(path);
        if (!f.exists()) {
            System.out.println(path + " not exists");
            return;
        }

        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (fs.isDirectory()) {
                System.out.println(fs.getName() + " [目录]");
            } else {
                System.out.println(fs.getName());
            }
        }
    }
}
