package com.heima.minio.test;


import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import java.io.FileInputStream;

//@SpringBootTest(classes = MinIOApplication.class)
//@RunWith(SpringRunner.class)
public class MinIOTest {

    /*@Autowired
    private FileStorageService fileStorageService;

    //把list.html文件上传到minio中，并且可以在浏览器中访问

    @Test
    public void test() throws FileNotFoundException {
//        FileInputStream fileInputStream = new FileInputStream("D:\\list.html");
//        String path = fileStorageService.uploadHtmlFile("", "list.html", fileInputStream);
        FileInputStream fileInputStream = new FileInputStream("E:\\tmp\\ak47.jpg");
        String path = fileStorageService.uploadImgFile("", "ak47.jpg", fileInputStream);
        System.out.println(path);
    }*/





    /**
     * 把list.html文件上传到minio中，并且可以在浏览器中访问
     * @param args
     */
    public static void main(String[] args) {

        try {
            FileInputStream fileInputStream = new FileInputStream("C:\\Users\\yuhon\\Downloads\\index.js");

            //1，获取minio的链接信息  创建一个minio的客户端
            MinioClient minioClient = MinioClient.builder().credentials("minio", "minio123").endpoint("http://192.168.200.130:9000").build();

            //2.上传
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object("plugins/js/index.js") //文件名词
                    .contentType("text/js") //文件类型
                    .bucket("leadnews") //桶名称  与minio管理界面创建的桶一致即可
                    .stream(fileInputStream,fileInputStream.available(),-1).build();
            minioClient.putObject(putObjectArgs);

            //访问路径
//            System.out.println("http://192.168.200.130:9000/leadnews/list.html");
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

}