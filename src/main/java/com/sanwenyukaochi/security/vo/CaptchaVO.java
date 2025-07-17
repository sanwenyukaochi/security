package com.sanwenyukaochi.security.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CaptchaVO {
    private String captchaKey;
    private String captcha;
}
