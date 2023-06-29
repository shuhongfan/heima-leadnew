package com.heima.article.service;

import com.heima.model.article.pojos.ApArticle;

public interface ArticleFreemarkerService {
    /**
     * 生成静态文件上传到minIO中
     * @param apArticle
     * @param content
     */
    public void buildArticleToMinIO(ApArticle apArticle, String content);

    /**
     * 送消息，创建索引
     * @param apArticle
     * @param content
     * @param path
     */
    public void createArticleESIndex(ApArticle apArticle, String content, String path);
}
