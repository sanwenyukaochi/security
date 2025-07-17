package com.sanwenyukaochi.security.security.captcha;

import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.base.Captcha;

public class ChineseCaptchaService extends CaptchaService {
    @Override
    protected Captcha getCaptchaInstance(int width, int height, int length) {
        return new ChineseCaptcha(width, height);
    }
}
