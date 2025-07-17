package com.sanwenyukaochi.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CaptchaPair {
    private String captchaKey;
    private String captcha;
}
