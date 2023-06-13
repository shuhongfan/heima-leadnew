package com.heima.model.user.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

@Data
public class AuthDto  extends PageRequestDto {

    /**
     * 状态
     */
    private Short status;

    private Integer id;

    //驳回的信息
    private String msg;
}
