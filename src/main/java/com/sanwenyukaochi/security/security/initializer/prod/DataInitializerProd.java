package com.sanwenyukaochi.security.security.initializer.prod;

import com.sanwenyukaochi.security.security.jwt.RSAUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"prod"})
@RequiredArgsConstructor
@Slf4j
public class DataInitializerProd implements CommandLineRunner {

    @Override
    @SneakyThrows
    public void run(String... args) {
        RSAUtil.RSAKey rsaKey = RSAUtil.generateKeyPair();
        RSAUtil.setPublicKey(rsaKey.getPublicKeyString());
        RSAUtil.setPrivateKey(rsaKey.getPrivateKeyString());
    }
    
}