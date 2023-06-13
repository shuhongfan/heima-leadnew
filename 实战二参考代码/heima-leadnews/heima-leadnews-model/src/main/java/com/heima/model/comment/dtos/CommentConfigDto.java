package com.heima.model.comment.dtos;

import lombok.Data;

@Data
public class CommentConfigDto {

    /**
     * 文章id
     */
    private Long articleId;
    /**
     * 操作
     * 0  关闭评论
     * 1  开启评论
     */
    private Short operation;
}
