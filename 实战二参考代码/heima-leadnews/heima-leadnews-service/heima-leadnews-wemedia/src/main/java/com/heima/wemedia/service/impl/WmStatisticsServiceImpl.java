package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.article.IArticleClient;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.StatisticsDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.DateUtils;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.service.WmStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
@Transactional
public class WmStatisticsServiceImpl implements WmStatisticsService {

    @Autowired
    private WmNewsService wmNewsService;

    @Autowired
    private IArticleClient articleClient;

    /**
     * 图文统计
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    @Override
    public ResponseResult newsDimension(String beginDate, String endDate) {

        Map<String, Object> resultMap = new HashMap<>();

        //类型转换   字符串转换为date类型
        Date beginDateTime = DateUtils.stringToDate(beginDate);
        Date endDateTime = DateUtils.stringToDate(endDate);


        WmUser user = WmThreadLocalUtil.getUser();

        //图文发布量
        /*int publishNum = wmNewsService.count(Wrappers.<WmNews>lambdaQuery()
                .eq(WmNews::getUserId, user.getId())
                .eq(WmNews::getStatus, WmNews.Status.PUBLISHED.getCode())
                .eq(WmNews::getEnable, 1)
                .between(WmNews::getPublishTime, beginDateTime, endDateTime));
        resultMap.put("publishNum", publishNum);*/

        //点赞数量&收藏数量
        ResponseResult responseResult = articleClient.queryLikesAndConllections(user.getId(), beginDateTime, endDateTime);
        if (responseResult.getCode().equals(200)) {
            String res_json = JSON.toJSONString(responseResult.getData());
            Map map = JSON.parseObject(res_json, Map.class);
            resultMap.put("likesNum", map.get("likes") == null ? 0 : map.get("likes"));
            resultMap.put("collectNum", map.get("collections") == null ? 0 : map.get("collections"));
            resultMap.put("publishNum", map.get("newsCount") == null ? 0 : map.get("newsCount"));
        }


        return ResponseResult.okResult(resultMap);
    }

    /**
     * 分页查询图文统计
     *
     * @param dto
     * @return
     */
    @Override
    public PageResponseResult newsPage(StatisticsDto dto) {
        WmUser user = WmThreadLocalUtil.getUser();
        dto.setWmUserId(user.getId());
        PageResponseResult responseResult = articleClient.newPage(dto);
        return responseResult;
    }
}
