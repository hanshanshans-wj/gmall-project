package com.wuju.gmall.gmallmanageweb.controller;

import com.alibaba.dubbo.remoting.Client;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
public class FileUploadController {
    @Value("${fileServer.url}")
    String fileUrl;
    @RequestMapping("fileUpload")
    public String uploadFile(MultipartFile file) throws IOException, MyException {
    //读取配置文件的tracker.conf
        String imgUrl=fileUrl;
        if (file!=null){
            String configFile=this.getClass().getResource("/tracker.conf").getFile();
            ClientGlobal.init(configFile);
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer connection = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(connection, null);
            //文件名
            String originalFilename = file.getOriginalFilename();
            //后缀名
            String s = StringUtils.substringAfterLast(originalFilename, ".");
            String[] uploadFile = storageClient.upload_file(file.getBytes(), s, null);
            for (int i = 0; i <uploadFile.length ; i++) {
                String path = uploadFile[i];
                //1group
                //2 路径
                //http://192.168.25.130/group1/M00/00/00/wKgZgl3r6xSAI6m_AAAl_GXv6Z4378.jpg
                imgUrl="/"+path;
                System.out.println("path"+path);
            }

        }

        return imgUrl;
    }
}
