package com.sanwenyukaochi.security;

import com.sanwenyukaochi.security.security.captcha.CaptchaContext;
import com.sanwenyukaochi.security.security.captcha.CaptchaPairCodeAndBase;
import com.sanwenyukaochi.security.security.captcha.CaptchaType;
import com.wf.captcha.*;
import org.junit.jupiter.api.Test;

public class CaptchaTests {
    
    @Test
    public void testRandomCaptcha() {
        CaptchaPairCodeAndBase result = CaptchaContext.generateRandomCaptcha(130,48,5);
        String verCode = result.getCode();
        String base64 = result.getBase64();
        System.out.println("VerCode验证码: " + verCode);
        System.out.println("Base64编码: " + base64);
    }

    @Test
    public void testArithmetic() {
        CaptchaPairCodeAndBase result = CaptchaContext.generateCaptcha(CaptchaType.SPEC);
        String verCode = result.getCode();
        String base64 = result.getBase64();
        System.out.println("VerCode验证码: " + verCode);
        System.out.println("Base64编码: " + base64);
    }
    
    @Test
    public void testSpecCaptchaPng() {
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 5);
        String verCode = captcha.text().toLowerCase();
        String base64 = captcha.toBase64();
        System.out.println("Spec验证码: " + verCode);
        System.out.println("SpecBase64编码: " + base64);
    }

    @Test
    public void testGifCaptcha() {
        GifCaptcha captcha = new GifCaptcha(130, 48, 5);
        String verCode = captcha.text().toLowerCase();
        String base64 = captcha.toBase64();
        System.out.println("Gif验证码: " + verCode);
        System.out.println("GifBase64编码: " + base64);
    }

    @Test
    public void testChineseCaptchaPng() {
        ChineseCaptcha captcha = new ChineseCaptcha(130, 48);
        String verCode = captcha.text().toLowerCase();
        String base64 = captcha.toBase64();
        System.out.println("中文Png验证码: " + verCode);
        System.out.println("中文PngBase64编码: " + base64);
    }

    @Test
    public void testChineseCaptchaGif() {
        ChineseGifCaptcha captcha = new ChineseGifCaptcha(130, 48);
        String verCode = captcha.text().toLowerCase();
        String base64 = captcha.toBase64();
        System.out.println("中文Gif验证码: " + verCode);
        System.out.println("中文GifBase64编码: " + base64);
    }

}