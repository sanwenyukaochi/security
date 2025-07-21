package com.sanwenyukaochi.security;

import com.sanwenyukaochi.security.storage.FileStorage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OBSTests {
    @Autowired
    private FileStorage fileStorage;

    @Test
    public void testPrivateKeyStr() {
        String bucketPath = fileStorage.getBucketPath();
        String bucketPrefix = fileStorage.getBucketPrefix();
        System.out.println("bucketPath: " + bucketPath);
        System.out.println("bucketPrefix: " + bucketPrefix);
    }

    @Test
    public void testPrivateKeyStr1() {
        fileStorage.uploadFileByFileStream("testFile.json", "/Users/songyifan/Desktop/testFile.json");
    }
}