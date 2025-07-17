package com.sanwenyukaochi.security.security.captcha;

import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;

public class GifCaptchaService extends CaptchaService {
    public GifCaptchaService() {
        super();
    }

    @Override
    protected Captcha getCaptchaInstance(int width, int height, int length) {
        return new GifCaptcha(width, height, length);
    }
}
