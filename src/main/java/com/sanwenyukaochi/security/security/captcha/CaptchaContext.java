package com.sanwenyukaochi.security.security.captcha;

import java.util.Random;

public class CaptchaContext {

    private static final CaptchaType[] TYPES = CaptchaType.values();
    private static final Random RANDOM = new Random();

    // 随机策略
    public static CaptchaPairCodeAndBase generateRandomCaptcha(int width, int height, int length) {
        CaptchaType randomType = TYPES[RANDOM.nextInt(TYPES.length)];
        CaptchaService service = CaptchaFactory.getCaptchaService(randomType);
        return service.generate(width, height, length);
    }

    // 指定策略
    public static CaptchaPairCodeAndBase generateCaptcha(CaptchaType type) {
        CaptchaService service = CaptchaFactory.getCaptchaService(type);
        return service.generate();
    }
}
