package com.sanwenyukaochi.security.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountVO {
    private Long id;
    private String userName;
    private String nickName;
    private String phone;
    private String avatar;
    // TODO 缺租组织(角色)
    // TODO 缺套餐
    private Long updatedAt;
    private Long createdAt;
}
