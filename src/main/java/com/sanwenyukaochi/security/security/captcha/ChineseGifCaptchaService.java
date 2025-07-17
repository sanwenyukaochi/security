package com.sanwenyukaochi.security.security.captcha;

import com.wf.captcha.ChineseGifCaptcha;
import com.wf.captcha.base.Captcha;

public class ChineseGifCaptchaService extends CaptchaService {
    @Override
    protected Captcha getCaptchaInstance(int width, int height, int length) {
        return new ChineseGifCaptcha(width, height);
    }
}
