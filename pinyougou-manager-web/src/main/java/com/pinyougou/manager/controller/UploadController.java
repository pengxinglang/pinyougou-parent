package com.pinyougou.manager.controller;


import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController

public class UploadController {

    //读取配置文件
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;//文件服务器地址


    /**
     * @RequestParam(value = "file" , required = true)  如果不指定，后端接收不到
     * @param multipartFile
     * @return
     */
    @RequestMapping("/upload")
    public Result upload(@RequestParam(value = "file" , required = true) MultipartFile multipartFile){
        //System.out.println("multipartFile:"+multipartFile.getName());
        //获取文件路径的名称
        String originalFilename = multipartFile.getOriginalFilename();
        //获得名字的后缀名
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        try {
            //创建一个 FastDFS 的客户端
            FastDFSClient fastDFSClient=new FastDFSClient("classpath:config/fdfs_client.conf");
            //返回存储的路径
            String uploadFile = fastDFSClient.uploadFile(multipartFile.getBytes(), extName);
            //拼接路径
            String url= FILE_SERVER_URL+uploadFile;
            //System.out.println(url);
            return  new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"上传失败");
        }

    }
}
