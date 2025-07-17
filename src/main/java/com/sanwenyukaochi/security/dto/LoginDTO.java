package com.sanwenyukaochi.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginDTO {
    private String username;
    private String password;
    private String captchaKey;
    private String captcha;
}
