package com.heima.model.admin.dtos;

import lombok.Data;

@Data
public class AdUserDto {

    /**
     * 用户名
     */
    private String name;
    /**
     * 密码
     */
    private String password;
}
