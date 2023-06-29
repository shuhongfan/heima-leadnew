package com.heima.search.service.impl;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.search.pojos.ApAssociateWords;
import com.heima.search.service.ApAssociateWordsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ApAssociateWordsServiceImpl implements ApAssociateWordsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 联想词
     * @param userSearchDto
     * @return
     */
    @Override
    public ResponseResult findAssociate(UserSearchDto userSearchDto) {
//        1.参数检查
        if (userSearchDto == null || StringUtils.isBlank(userSearchDto.getSearchWords())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

//        2.分页检查
        if (userSearchDto.getPageSize() > 20) {
            userSearchDto.setPageSize(20);
        }

//        3.执行查询 模糊查询
        List<ApAssociateWords> associateWords = mongoTemplate.find(
                Query.query(Criteria.where("associateWords")
                        .regex(".*?\\" + userSearchDto.getSearchWords() + ".*"))
                        .limit(userSearchDto.getPageSize()),
                ApAssociateWords.class);
        return ResponseResult.okResult(associateWords);
    }
}
