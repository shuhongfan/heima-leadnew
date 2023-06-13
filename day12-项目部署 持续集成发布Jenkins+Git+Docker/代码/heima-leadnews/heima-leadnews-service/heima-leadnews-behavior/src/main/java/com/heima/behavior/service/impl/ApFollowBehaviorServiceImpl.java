package com.heima.behavior.service.impl;

import com.heima.behavior.service.ApFollowBehaviorService;
import com.heima.model.behavior.dtos.FollowBehaviorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
@Slf4j
public class ApFollowBehaviorServiceImpl implements ApFollowBehaviorService {

    @Override
    public boolean saveFollowBehavior(FollowBehaviorDto dto) {

        //2.保存关注行为
//        ApFollowBehavior apFollowBehavior = new ApFollowBehavior();
//        apFollowBehavior.setFollowId(dto.getFollowId());
//        apFollowBehavior.setCreatedTime(new Date());
//        apFollowBehavior.setArticleId(dto.getArticleId());
//        apFollowBehavior.setEntryId(apBehaviorEntry.getId());
//        return save(apFollowBehavior);
        return false;
    }
}
