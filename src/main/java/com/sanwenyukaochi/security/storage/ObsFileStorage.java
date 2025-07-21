package com.sanwenyukaochi.security.storage;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

@Component
@RequiredArgsConstructor
@Slf4j
public class ObsFileStorage implements FileStorage {


    private final ObsClient obsClient;

    @Value("${huawei.obs.bucketName}")
    private String bucketName;

    @Value("${huawei.obs.bucketPrefix}")
    private String bucketPrefix;

    @Value("${huawei.obs.bucketPath}")
    private String bucketPath;

    @Override
    public String getBucketPrefix() {
        return bucketPrefix;
    }

    @Override
    public String getBucketPath() {
        return bucketPath;
    }

    /**
     * 上传文件-断点续传上传
     */
    @Override
    public void uploadFileByCheckpoint(String objectName, String localPath) {
        try {
            UploadFileRequest request = new UploadFileRequest(bucketName, objectName);
            // 设置待上传的本地文件，localFile为待上传的本地文件路径，需要指定到具体带文件后缀的文件名
            request.setUploadFile(localPath);
            // 设置分段上传时的最大并发数
            request.setTaskNum(5);
            // 设置分段大小为10MB
            request.setPartSize(10 * 1024 * 1024);
            // 开启断点续传模式
            request.setEnableCheckpoint(true);
            // 进行断点续传上传
            CompleteMultipartUploadResult result = obsClient.uploadFile(request);
            log.info("UploadFile successfully");
        } catch (ObsException e) {
            // 发生异常时可再次调用断点续传上传接口进行重新上传
            log.error("UploadFile failed");
            logObsException(e);
        } catch (Exception e) {
            log.error("UploadFile failed");
            // 其他异常信息打印
            e.printStackTrace();
        }
    }



    /**
     * 上传文件-上传网络流
     */
    @Override
    public void uploadFileByUrlStream(String objectName, String url) {
        try {
            // 上传网络流
            InputStream inputStream = new URL(url).openStream();
            obsClient.putObject(bucketName, objectName, inputStream);
            log.info("putObject successfully");
        } catch (ObsException e) {
            log.error("putObject failed");
            logObsException(e);
        } catch (Exception e) {
            log.error("putObject failed");
            // 其他异常信息打印
            e.printStackTrace();
        }
    }

    /**
     * 上传文件-上传文件流
     */
    @Override
    public void uploadFileByFileStream(String objectName, String localFile) {
        try {
            // 待上传的本地文件路径，需要指定到具体的文件名
            FileInputStream fis = new FileInputStream(new File(localFile));
            PutObjectRequest request = new PutObjectRequest();
            request.setBucketName(bucketName);
            request.setObjectKey(objectName);
            request.setInput(fis);
            obsClient.putObject(request);
            log.info("putObject successfully");
        } catch (ObsException e) {
            log.error("putObject failed");
            logObsException(e);
        } catch (Exception e) {
            log.error("putObject failed");
            // 其他异常信息打印
            e.printStackTrace();
        }
    }

    @Override
    public void uploadFileByByteStream(String objectName, byte[] bytes) {
        try {
            obsClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
            System.out.println("putObject successfully");
        } catch (ObsException e) {
            System.out.println("putObject failed");
            // 请求失败,打印http状态码
            System.out.println("HTTP Code:" + e.getResponseCode());
            // 请求失败,打印服务端错误码
            System.out.println("Error Code:" + e.getErrorCode());
            // 请求失败,打印详细错误信息
            System.out.println("Error Message:" + e.getErrorMessage());
            // 请求失败,打印请求id
            System.out.println("Request ID:" + e.getErrorRequestId());
            System.out.println("Host ID:" + e.getErrorHostId());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("putObject failed");
            // 其他异常信息打印
            e.printStackTrace();
        }
    }

    /**
     * 下载文件-断点续传下载
     */
    @Override
    public void downloadFileByCheckpoint(String objectName, String localFile) {
        try {
            DownloadFileRequest request = new DownloadFileRequest(bucketName, objectName);
            // 设置下载对象的本地文件路径
            request.setDownloadFile(localFile);
            // 设置分段下载时的最大并发数
            request.setTaskNum(5);
            // 设置分段大小为10MB
            request.setPartSize(10 * 1024 * 1024);
            // 开启断点续传模式
            request.setEnableCheckpoint(true);
            // 进行断点续传下载
            DownloadFileResult result = obsClient.downloadFile(request);
            log.info("downloadFile successfully");
            log.info("Etag:" + result.getObjectMetadata().getEtag());
        } catch (ObsException e) {
            log.error("downloadFile failed");
            logObsException(e);
        } catch (Exception e) {
            log.error("downloadFile failed");
            // 其他异常信息打印
            e.printStackTrace();
        }
    }

    /**
     * 重命名文件
     */
    @Override
    public void renameObject(String oldKey, String newKey) {
        CopyObjectRequest copyRequest = new CopyObjectRequest(bucketName, oldKey, bucketName, newKey);
        obsClient.copyObject(copyRequest);
        obsClient.deleteObject(bucketName, oldKey);
    }

    private static void logObsException(ObsException e) {
        // 请求失败,打印http状态码
        log.error("HTTP Code:{}", e.getResponseCode());
        // 请求失败,打印服务端错误码
        log.error("Error Code:{}", e.getErrorCode());
        // 请求失败,打印详细错误信息
        log.error("Error Message:{}", e.getErrorMessage());
        // 请求失败,打印请求id
        log.error("Request ID:{}", e.getErrorRequestId());
        log.error("Host ID:{}", e.getErrorHostId());
        e.printStackTrace();
    }
}
