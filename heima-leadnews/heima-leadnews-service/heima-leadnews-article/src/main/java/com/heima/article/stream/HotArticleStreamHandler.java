package com.heima.article.stream;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.HotArticleConstants;
import com.heima.model.mess.ArticleVisitStreamMess;
import com.heima.model.mess.UpdateArticleMess;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class HotArticleStreamHandler {

    @Bean
    public KStream<String, String> KStream(StreamsBuilder streamsBuilder) {
//        接收消息
        KStream<String, String> stream = streamsBuilder.stream(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC);

//        聚合流式处理
        stream.map((key, value) -> {
            UpdateArticleMess mess = JSON.parseObject(value, UpdateArticleMess.class);
//            重置消息的key:1223456 和value  likes:1
            return new KeyValue<>(mess.getArticleId().toString(), mess.getType().name() + ":" + mess.getAdd());
        }).groupBy((key, value) -> key) // 按照文章id进行聚合
                .windowedBy(TimeWindows.of(Duration.ofSeconds(10)))  // 时间滑动窗口
                /**
                 * 自行的完成聚合的计算
                 */
                .aggregate(new Initializer<String>() {
                    /**
                     * 初始方法，返回值是消息的value
                     * @return
                     */
                    @Override
                    public String apply() {
                        return "COLLECTION:0,COMMENT:0,VIEWS:0";
                    }
                }, new Aggregator<String, String, String>() {
                    /**
                     * 真正的聚合，返回值是消息的value
                     * @param key
                     * @param value
                     * @param aggValue
                     * @return
                     */
                    @Override
                    public String apply(String key, String value, String aggValue) {
                        if (StringUtils.isBlank(value)) {
                            return aggValue;
                        }
                        String[] aggAry = aggValue.split(",");
                        int col = 0, com = 0, like = 0, vie = 0;
                        for (String agg : aggAry) {
                            String[] split = agg.split(":");

//                            获取初始值，也是时间窗口内计算之后的值
                            switch (UpdateArticleMess.UpdateArticleType.valueOf(split[0])) {
                                case COLLECTION:
                                    col = Integer.parseInt(split[1]);
                                    break;
                                case COMMENT:
                                    com = Integer.parseInt(split[1]);
                                    break;
                                case LIKES:
                                    like = Integer.parseInt(split[1]);
                                    break;
                                case VIEWS:
                                    vie = Integer.parseInt(split[1]);
                                    break;
                            }
                        }

//                        累加操作
                        String[] valAry = value.split(":");
                        switch (UpdateArticleMess.UpdateArticleType.valueOf(valAry[0])) {
                            case COLLECTION:
                                col += Integer.parseInt(valAry[1]);
                                break;
                            case COMMENT:
                                com += Integer.parseInt(valAry[1]);
                                break;
                            case LIKES:
                                like += Integer.parseInt(valAry[1]);
                                break;
                            case VIEWS:
                                vie += Integer.parseInt(valAry[1]);
                                break;
                        }

                        String formatStr = String.format("COLLECTION:%d,COMMENT:%d,LIKES:%d,VIEWS:%d", col, com, like, vie);
                        System.out.println("文章的id:"+key);
                        System.out.println("当前时间窗口内的消息处理结果："+formatStr);
                        return formatStr;
                    }
                }, Materialized.as("hot-article-stream-count-001"))
                .toStream()
                .map((key,value)->{
                    return new KeyValue<>(key.key().toString(),formatObj(key.key().toString(), value));
                })
                .to(HotArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC); // 发送消息
        return stream;
    }

    public String formatObj(String articleId, String value) {
        ArticleVisitStreamMess articleVisitStreamMess = new ArticleVisitStreamMess();
        articleVisitStreamMess.setArticleId(Long.valueOf(articleId));

        String[] valAry = value.split(",");
        for (String val : valAry) {
            String[] split = val.split(":");
            switch (UpdateArticleMess.UpdateArticleType.valueOf(split[0])) {
                case COLLECTION:
                   articleVisitStreamMess.setCollect(Integer.parseInt(valAry[1]));
                    break;
                case COMMENT:
                    articleVisitStreamMess.setComment(Integer.parseInt(valAry[1]));
                    break;
                case LIKES:
                    articleVisitStreamMess.setLike(Integer.parseInt(valAry[1]));
                    break;
                case VIEWS:
                    articleVisitStreamMess.setView(Integer.parseInt(valAry[1]));
                    break;
            }
        }
        log.info("聚合消息处理之后的结果为:{}",JSON.toJSONString(articleVisitStreamMess));
        return JSON.toJSONString(articleVisitStreamMess);
    }


}
