package com.sanwenyukaochi.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private Long id;
    private String nickName;
    private String phone;
    // TODO 缺租组织(角色)
    // TODO 缺套餐
    private Long createdAt;
}
