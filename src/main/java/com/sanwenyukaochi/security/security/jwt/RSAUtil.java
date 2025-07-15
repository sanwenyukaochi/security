package com.sanwenyukaochi.security.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RSAUtil {

	//@Value("${spring.app.rsaPublicKey}")
	private static String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAptE01Mvg5z8J5cbX3GyVt+LoZEo7RetAmsfmS5lhBolq3vzkm2A1g1cA1PAEQMTvB5KKGT9JnE6G5AO4AmgWKGF3j4ugsCjZpHYUhm44Dz3NbKgEmOQfH0ZU9Evbuwd+Dnwjx/14300QSX11floBBCx1qvMbt/SJ1LIM9ZLQNvxXbAD0DOAMnqshSKEAqvAP2n87wVuUyqsAOH/ZEFUMIpPmNUhGMDI0Sbk5OwQjrU9oqTWiQmgMYcv2bWtrXrd6Kf8OY93bRdX0smmaLgNTqHjuhER01HA3uJndOisMMhWRb8hhrrVjbSRPy/4BfxN75T0WhhS6j7rremZVy9BgAwIDAQAB";

	//@Value("${spring.app.rsaPrivateKey}")
	private static String PRIVATE_KEY ="MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCm0TTUy+DnPwnlxtfcbJW34uhkSjtF60Cax+ZLmWEGiWre/OSbYDWDVwDU8ARAxO8HkooZP0mcTobkA7gCaBYoYXePi6CwKNmkdhSGbjgPPc1sqASY5B8fRlT0S9u7B34OfCPH/XjfTRBJfXV+WgEELHWq8xu39InUsgz1ktA2/FdsAPQM4AyeqyFIoQCq8A/afzvBW5TKqwA4f9kQVQwik+Y1SEYwMjRJuTk7BCOtT2ipNaJCaAxhy/Zta2tet3op/w5j3dtF1fSyaZouA1OoeO6ERHTUcDe4md06KwwyFZFvyGGutWNtJE/L/gF/E3vlPRaGFLqPuut6ZlXL0GADAgMBAAECggEAQBYoR0YqGXzg1wscm7yFijcck4bnHZXi5HO+mDWNDl9VlOQwCTcdZ99RXPz2jVF7CPw1nLxxEaEjdk6tbxNAy/Oh5J4+Nd8Dugh5GyoV1FkoZcFovroI8NFqys2n1ULsHTF455iLyrHG+4y8yjVWpZ1U+T9bW0ERgIqEqwVjg3OwFzp2QnVmFVh3SdkE7FOa9tg7jYAhjLkB8K+SUd65BHuKZ5A68M0clsLEcD8aa7e37W/JR05Lw8dpkRwikU6/12FXN+67GXMjKKsWcouIhrETFytugJ7e9AtKUIbPyFbjDmB9LZt/zIZZKbuw8a+ML94Rm5OtwEgN0I80VZUhbQKBgQDdFRr2cmDYaTJhpienwHRos1iMds8UJDG6JZBpOy1yI65omc5ps368f+KmoDMCicnk9J+Zal79v3cTddXLcLa0xjSu7Utewn6eyVnVSSouDIXF1mpos04qvM7dnAN0DFQ1W3NvA/naTa75gfx3oMIVWv6welVaGSqVNqTP36pRXQKBgQDBKgb+1vaWvhx8CS+jXHLYO8Or8ZGHrszys3g5bN+LHjuorVbozhqfF+h3SzupNMj59eWV1bn911mOIG/J23QmFEqqCpCvtMUBJLLIiFyumlUGue+bQYydGlrb83N9IUzSiWs0ZpyM2WFcSYoukqtoQ6qYMnay9cZ23dlRH4mA3wKBgQDDGbv4nsl+UZ+HcyTtsjZIq3TKSJLIOIS1vC6r3vBlGL3yS0FQIHoIiWcQGrHJXKWR6prpvWhkz9Gal9N7PFXQRFX7xSdNUaCCKjifs3iIuL5Y77Zp/UpPBt4bzXFyuOqbR8AfyUd18jYmtCKDw5djVzEJtnuaDjl5AocBd2WLdQKBgQCZG+H9n5BHvhs/6dwbxcy2PvEDWnA7Nz/2ZHE4O2cZk3ZAZl1MHQoW7tFxtW1t2owvLUy0rntFjKvMr0NMoil3gYAJNmSnEUdSd69KqmOkdmpM8ZbN3nBBmsFINLlnBr0o6InUOD2Am1HD3/qqndFFzNTs3JsfkRal44U8+x13ywKBgQCMjHoWq2cfN5yjk1UE+GFrQDrMc7fa2MQo1ACAgNNNVVcqfOpoAJnZV8lLLUrPJOT566J387ULgjvRvazRAWsNoRIF7mjBv39oyq97b1f78a3YpYvUrjdKGixeH0NdWsJzPds4xY/fHTnkal4b09hM/NyRlYyFn6v6GREnRfTbEA=="; 

	public static String getPublicKeyStr() {
		return PUBLIC_KEY;
	}

	public static void setPublicKey(String publicKey) {
		PUBLIC_KEY = publicKey;
	}

	public static String getPrivateKeyStr() {
		return PRIVATE_KEY;
	}

	public static void setPrivateKey(String privateKey) {
		PRIVATE_KEY = privateKey;
	}

	@SneakyThrows
	public static RSAPublicKey getPublicKey() {
		byte[] decoded = Base64.getDecoder().decode(PUBLIC_KEY);
		return (RSAPublicKey) KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(decoded));
	}

	@SneakyThrows
	public static RSAPrivateKey getPrivateKey() {
		byte[] decoded = Base64.getDecoder().decode(PRIVATE_KEY);
		return (RSAPrivateKey) KeyFactory.getInstance("RSA")
				.generatePrivate(new PKCS8EncodedKeySpec(decoded));
	}

	public static RSAKey generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(2048, new SecureRandom());
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
		String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
		return new RSAKey(privateKey, privateKeyString, publicKey, publicKeyString);
	}

	@SneakyThrows
	public static String encrypt(String source) {
		byte[] decoded = Base64.getDecoder().decode(PUBLIC_KEY);
		RSAPublicKey rsaPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(decoded));
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(1, rsaPublicKey);
		return Base64.getEncoder().encodeToString(cipher.doFinal(source.getBytes(StandardCharsets.UTF_8)));
	}

	@SneakyThrows
	public static Cipher getCipher() {
		byte[] decoded = Base64.getDecoder().decode(PRIVATE_KEY);
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
				.generatePrivate(new PKCS8EncodedKeySpec(decoded));
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(2, rsaPrivateKey);
		return cipher;
	}

	@SneakyThrows
	public static String decrypt(String text) {
		Cipher cipher = getCipher();
		byte[] inputByte = Base64.getDecoder().decode(text.getBytes(StandardCharsets.UTF_8));
		return new String(cipher.doFinal(inputByte));
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RSAKey {

		private RSAPrivateKey privateKey;
		private String privateKeyString;
		private RSAPublicKey publicKey;
		public String publicKeyString;

	}
}