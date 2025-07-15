package com.sanwenyukaochi.security;

import com.sanwenyukaochi.security.security.jwt.RSAUtil;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

public class RSATests {

    @Test
    public void initializeRSA() throws NoSuchAlgorithmException {
        RSAUtil.RSAKey rsaKey = RSAUtil.generateKeyPair();
        RSAUtil.setPublicKey(rsaKey.getPublicKeyString());
        RSAUtil.setPrivateKey(rsaKey.getPrivateKeyString());
        System.out.println(rsaKey.getPublicKeyString());
        System.out.println(rsaKey.getPrivateKeyString());
    }
    
    @Test
    public void test01() {
        String str = RSAUtil.encrypt("123456");
        System.out.println(str);
    }
    
}
