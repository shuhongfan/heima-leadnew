package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.article.service.ApCollectionService;
import com.heima.common.redis.CacheService;
import com.heima.model.article.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApCollectionServiceImpl implements ApCollectionService {

    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseResult collection(CollectionBehaviorDto dto) {
        //条件判断
        if(dto == null || dto.getEntryId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //判断是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if(user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        //查询
        String collectionJson = (String) cacheService.hGet("COLLECTION-BEHAVIOR-" + dto.getEntryId(), user.getId().toString());
        if(StringUtils.isNotBlank(collectionJson) && dto.getOperation() == 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"已收藏");
        }

        //收藏
        if(dto.getOperation() == 0){
            log.info("文章收藏，保存key:{},{},{}",dto.getEntryId(),user.getId().toString(), JSON.toJSONString(dto));
            cacheService.hPut("COLLECTION-BEHAVIOR-"+dto.getEntryId(),user.getId().toString(), JSON.toJSONString(dto));
        }else {
            //取消收藏
            log.info("文章收藏，删除key:{},{},{}",dto.getEntryId(),user.getId().toString(), JSON.toJSONString(dto));
            cacheService.hDelete("COLLECTION-BEHAVIOR-"+dto.getEntryId(),user.getId().toString());
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}