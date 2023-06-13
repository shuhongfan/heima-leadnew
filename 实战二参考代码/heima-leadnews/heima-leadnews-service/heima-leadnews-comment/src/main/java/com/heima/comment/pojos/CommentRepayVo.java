package com.heima.comment.pojos;

import lombok.Data;

@Data
public class CommentRepayVo extends ApCommentRepay {

    /**
     * 0：点赞
     * 1：取消点赞
     */
    private Short operation;
}