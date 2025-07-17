package com.sanwenyukaochi.security.security.captcha;

public class CaptchaFactory {
    public static CaptchaService getCaptchaService(CaptchaType type) {
        return switch (type) {
            case SPEC -> new SpecCaptchaService();
            case GIF -> new GifCaptchaService();
            case CHINESE -> new ChineseCaptchaService();
            case CHINESE_GIF -> new ChineseGifCaptchaService();
        };
    }
}
