package com.sanwenyukaochi.security.ao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginAO {
    @Setter
    @NotBlank(message = "请输入用户名")
    private String username;

    @Setter
    @NotBlank(message = "请输入密码")
    private String password;

    @NotBlank(message = "请输入验证码Key")
    private String captchaKey;

    @NotBlank(message = "请输入验证码")
    private String captcha;
}
