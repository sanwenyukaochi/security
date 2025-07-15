package com.sanwenyukaochi.security;

import com.sanwenyukaochi.security.security.jwt.RSAUtil;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class RSATests {

    @Test
    public void initializeRSA() throws Exception {
        RSAUtil.RSAKey rsaKey = RSAUtil.generateKeyPair();
        String publicPem = toPem("PUBLIC KEY", rsaKey.getPublicKeyString());
        String privatePem = toPem("PRIVATE KEY", rsaKey.getPrivateKeyString());
        File pubFile = new File("src/main/resources/keys/public.pem");
        File priFile = new File("src/main/resources/keys/private.pem");
        ensureParentDirectoryAndWritePemToFile(publicPem, pubFile);
        ensureParentDirectoryAndWritePemToFile(privatePem, priFile);
        System.out.println("公钥已写入: " + pubFile.getAbsolutePath());
        System.out.println("私钥已写入: " + priFile.getAbsolutePath());
    }

    private static String toPem(String type, String base64Body) {
        return "-----BEGIN " + type + "-----\n" +
                chunkString(base64Body, 64) +
                "\n-----END " + type + "-----\n";
    }

    private static String chunkString(String str, int chunkSize) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i += chunkSize) {
            sb.append(str, i, Math.min(i + chunkSize, str.length())).append("\n");
        }
        return sb.toString().trim();
    }

    private static void ensureParentDirectoryAndWritePemToFile(String pemContent, File file) {
        File parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new RuntimeException("创建目录失败: " + parent.getAbsolutePath());
        }
        
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write(pemContent);
        } catch (IOException e) {
            throw new RuntimeException("写入 PEM 文件失败: " + file.getAbsolutePath(), e);
        }
    }

    @Test
    public void testEncryptWithPublicKey() {
        String encrypted = RSAUtil.encrypt("123456");
        System.out.println("加密结果: " + encrypted);
    }
}
