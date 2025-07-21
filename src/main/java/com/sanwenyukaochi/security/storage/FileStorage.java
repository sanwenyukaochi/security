package com.sanwenyukaochi.security.storage;

public interface FileStorage {

    String getBucketPrefix();

    String getBucketPath();

    void uploadFileByCheckpoint(String objectName, String localPath);

    void uploadFileByUrlStream(String objectName, String url);

    void uploadFileByFileStream(String objectName, String localFile);

    void uploadFileByByteStream(String objectName, byte[] bytes);

    void downloadFileByCheckpoint(String objectName, String localFile);

    void renameObject(String oldKey, String newKey);

    void deleteObject(String objectName);
}
