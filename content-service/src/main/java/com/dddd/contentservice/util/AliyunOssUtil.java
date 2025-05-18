package com.dddd.contentservice.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.dddd.contentservice.config.AliyunOssConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class AliyunOssUtil {

    @Autowired
    private AliyunOssConfig config;

    public String uploadFile(MultipartFile file, String userId) throws IOException {
        String fileName = "uploads/" + userId + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        OSS ossClient = new OSSClientBuilder().build(
                config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());

        ossClient.putObject(config.getBucketName(), fileName, file.getInputStream());

        ossClient.shutdown();

        return "https://" + config.getBucketName() + "." + config.getEndpoint().replace("https://", "") + "/" + fileName;
    }

    public void deleteFile(String objectKey) {
        OSS ossClient = new OSSClientBuilder().build(
                config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());

        ossClient.deleteObject(config.getBucketName(), objectKey);

        ossClient.shutdown();
    }
}