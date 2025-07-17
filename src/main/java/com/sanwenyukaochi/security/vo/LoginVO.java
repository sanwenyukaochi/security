package com.sanwenyukaochi.security.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginVO {
    private String accessToken;
    private String refreshToken;
}
