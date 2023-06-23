package com.heima.minio.test;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinIOTest {
    public static void main(String[] args) throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\shuho\\IdeaProjects\\heima-leadnew\\heima-leadnews\\heima-leadnews-test\\freemarker-demo\\list.html");

//        1.获取minio的链接信息，创建一个minio的客户端
        MinioClient minioClient = MinioClient.builder().credentials("minio", "minio123").endpoint("http://192.168.200.130:9000").build();

//        2.上传
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .object("list.html")  // 文件名称
                .contentType("text/html") // 文件类型
                .bucket("leadnews") // 桶名称
                .stream(fileInputStream, fileInputStream.available(), -1).build();

        minioClient.putObject(putObjectArgs);
        System.out.println("http://192.168.200.130:9000/leadnews/list.html");
    }
}
