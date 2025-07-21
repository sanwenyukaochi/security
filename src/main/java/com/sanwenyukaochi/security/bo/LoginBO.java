package com.sanwenyukaochi.security.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginBO {
    private String username;
    private String password;
    private String captchaKey;
    private String captcha;
}
