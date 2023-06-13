package com.heima.model.user.dtos;

import com.heima.model.common.annotation.IdEncrypt;
import lombok.Data;

@Data
public class UserRelationDto {

    // 文章作者ID
    @IdEncrypt
    Integer authorId;

    // 文章id
    @IdEncrypt
    Long articleId;
    /**
     * 操作方式
     * 0  关注
     * 1  取消
     */
    Short operation;
}