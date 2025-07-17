package com.sanwenyukaochi.security.security.captcha;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;

public class SpecCaptchaService extends CaptchaService {
    public SpecCaptchaService() {
        super();
    }

    @Override
    protected Captcha getCaptchaInstance(int width, int height, int length) {
        return new SpecCaptcha(width, height, length);
    }
}
