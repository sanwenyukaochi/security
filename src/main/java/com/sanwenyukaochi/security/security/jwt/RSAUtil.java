package com.sanwenyukaochi.security.security.jwt;

import cn.hutool.crypto.CryptoException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

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

	private static String PUBLIC_KEY = """
            MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnSMkGflg+dMngoR5vvs0
            oPopRLAyw1vOMO9BPMnokuJEFn/JQOn7Jzs3Ezr5/sem1F49kpc4na/kqdyxO/YC
            cwJMJ4rvZR0UroKFavgZlyarMpT7zYkuKCB8YoCW57edC8oZEGyx16AxkwQvku12
            ufHCqlyf3MyCLD6BnfwsbHPBjMXVfx2XZBMjJgtAnXLZ0BGd6AZYV/DIvz0BdXd2
            6IFg76YrPFvv7c2x4eHexV1rnO0Az4coZGvckaiho3ZU8fsgf3PYVei2Lp6TRTQe
            yGpbc0/deDeKpWXgfWPnI73iYwIdBTfRkNl+7tX+MvneRJbFAtS9275WdMDGxxQ0
            gwIDAQAB""";

	private static String PRIVATE_KEY = """
            MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCdIyQZ+WD50yeC
            hHm++zSg+ilEsDLDW84w70E8yeiS4kQWf8lA6fsnOzcTOvn+x6bUXj2Slzidr+Sp
            3LE79gJzAkwniu9lHRSugoVq+BmXJqsylPvNiS4oIHxigJbnt50LyhkQbLHXoDGT
            BC+S7Xa58cKqXJ/czIIsPoGd/Cxsc8GMxdV/HZdkEyMmC0CdctnQEZ3oBlhX8Mi/
            PQF1d3bogWDvpis8W+/tzbHh4d7FXWuc7QDPhyhka9yRqKGjdlTx+yB/c9hV6LYu
            npNFNB7IaltzT914N4qlZeB9Y+cjveJjAh0FN9GQ2X7u1f4y+d5ElsUC1L3bvlZ0
            wMbHFDSDAgMBAAECggEAO0sq0LhGrQ5N3tSRQgz1T3cGAnrANPJb7CAjzEXeAirq
            CVlfviRsVmHkRtfexJJes8z5y+pO/UWFcckqgZczVmV8CgHFkrB2AOGUaYhD08LO
            H9CS8Xw9k/uqI9sk1jv1QSEZ8xYox1YVzsVqFDWRstl0nKdF1WHADuTpMQ2aT7OI
            W96H4EfPiNMVOj04Mjz+RRNUvWbZHzFS/QercuecH9708dVZLarM7pWEnTJNjuSZ
            WWnPIcVYYKqNkFghLMUDKBIi81tlXE/ajus/T/I8eugpdHWPWAnFmG3/bcaoGoE/
            k11xVVuqSGscTvI2dp6zsmvnWCLzYIMowU5cFvtbXQKBgQDap7pmxtAs+h9dwX6G
            VxbplVs3R8noQExMii+g028jW2xUN5l3Xp40S0A5WaPeAmuevHjEwz+k+P0Arsgd
            4a168kfusXVbZ6d8jF5a2sz69R8+Tbcz6HOz6KM/Dh6uMXO+YOkQSl3OBYyMaXE8
            We4kxGQ2GQ4dDU9wMhjP70WpLwKBgQC3+altcDA5lQFI+p+S+qMs+BNZKniBUVMQ
            lDN7wnWnVIH1vAblHvGIizg+8RaUc5rogEBNoUlReJn8+j1PVIxZmhzY0E3ewUiP
            lW0+eYgnPCBxvgJBP6D/bIAMRl+4O6qK+88OgzMA4Ky4xufy/EGmblwlCvN2alA5
            Q10xWAWs7QKBgHq/hEYkQEOvmqIweM4D4An7Xby34WIvjmW9gaVgbNjFfxf8Knw3
            ssqaoBBSAUslwrLXDVkwXu9HFAkpFZCo25HUp4sZNk+87epehq2sfTw5FXQNftZ2
            HVYXOGWykIirnFV44/3QMb7xoIhGvVnrBmS/l55DDIhEq57JkOJSaaHjAoGADUrF
            efm0EkT4vcwVwWnIZlRaGYQfBsDYboc2nQd0tq09YoK+QMmWi9X7L1j916XumEPD
            4Zf4kyFShtuRmGy0YzjmQqfyKFjwpCcYqH3hX2xRr8YZpZsyR6IQMlLXUZlF/hqp
            YBQMmFCjSgpc8cpV9+9bSvXal0ChITIDxq/kYRUCgYEAoxSSU5Pe0yo1/BGngzv7
            nrMVefQa6wYtvkboZdpS9Fr/LPkkO6gWRIqvCxy9LXEpVkwccWtAYsJexYS2rcGj
            sD458rCsuK6anF+T2/1Pmeh06WIqwen9hEnMBelPhi1hw+VAgfBSJhxLUdKSU3KT
            Gqe4cKWXxvpndvuGlbNtV+s=""";

	public static String getPublicKeyStr() {
		return PUBLIC_KEY.replaceAll("\\s", "");
	}

	public static void setPublicKey(String publicKey) {
		PUBLIC_KEY = publicKey.replaceAll("\\s", "");
	}

	public static String getPrivateKeyStr() {
		return PRIVATE_KEY.replaceAll("\\s", "");
	}

	public static void setPrivateKey(String privateKey) {
		PRIVATE_KEY = privateKey.replaceAll("\\s", "");
	}

	@SneakyThrows
	public static RSAPublicKey getPublicKey() {
		byte[] decoded = Base64.getDecoder().decode(PUBLIC_KEY.replaceAll("\\s", ""));
		return (RSAPublicKey) KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(decoded));
	}

	@SneakyThrows
	public static RSAPrivateKey getPrivateKey() {
		byte[] decoded = Base64.getDecoder().decode(PRIVATE_KEY.replaceAll("\\s", ""));
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
		byte[] decoded = Base64.getDecoder().decode(PUBLIC_KEY.replaceAll("\\s", ""));
		RSAPublicKey rsaPublicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(decoded));
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(1, rsaPublicKey);
		return Base64.getEncoder().encodeToString(cipher.doFinal(source.getBytes(StandardCharsets.UTF_8)));
	}

	@SneakyThrows
	public static Cipher getCipher() {
		byte[] decoded = Base64.getDecoder().decode(PRIVATE_KEY.replaceAll("\\s", ""));
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
				.generatePrivate(new PKCS8EncodedKeySpec(decoded));
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(2, rsaPrivateKey);
		return cipher;
	}

	public static String decrypt(String text) {
		try {
			Cipher cipher = getCipher();
			byte[] inputByte = Base64.getDecoder().decode(text.getBytes(StandardCharsets.UTF_8));
			return new String(cipher.doFinal(inputByte));
		} catch (Exception e) {
			throw new CryptoException("RSA 解密失败", e);
		}
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