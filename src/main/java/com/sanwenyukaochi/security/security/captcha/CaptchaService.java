package com.sanwenyukaochi.security.security.captcha;

import com.wf.captcha.base.Captcha;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public abstract class CaptchaService {

    protected int width = 130;
    protected int height = 48;
    protected int length = 5;

    // 模板方法：统一调用逻辑
    public CaptchaPairCodeAndBase generate(int width, int height, int length) {
        Captcha captcha = getCaptchaInstance(width, height, length);
        String code = captcha.text().toLowerCase();
        String base64 = captcha.toBase64();
        return new CaptchaPairCodeAndBase(code, base64);
    }

    public CaptchaPairCodeAndBase generate() {
        Captcha captcha = getCaptchaInstance(width, height, length);
        String code = captcha.text().toLowerCase();
        String base64 = captcha.toBase64();
        return new CaptchaPairCodeAndBase(code, base64);
    }

    // 子类实现不同验证码生成策略
    protected abstract Captcha getCaptchaInstance(int width, int height, int length);

}
