package com.sanwenyukaochi.security.security.captcha;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CaptchaPairCodeAndBase {
    private String code;
    private String base64;
}
