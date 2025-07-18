package com.sanwenyukaochi.security.ao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginAO {
    @NotBlank(message = "请输入用户名")
    @Size(min = 3, max = 20, message = "用户名长度3-20字符")
    private String username;
    
    @NotBlank(message = "请输入密码")
    @Size(min = 6, max = 40, message = "密码长度6-40字符")
    private String password;

    @NotBlank(message = "请输入验证码Key")
    private String captchaKey;

    @NotBlank(message = "请输入验证码")
    private String captcha;
}
