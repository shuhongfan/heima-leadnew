package com.heima.comment.service.impl;

import com.heima.comment.pojos.ApComment;
import com.heima.comment.service.CommentHotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentHotServiceImpl implements CommentHotService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    @Async//使用异步线程池
    public void findHotComment(Long entryId, ApComment apComment) {
        Query query = Query.query(Criteria.where("entryId").is(entryId).and("flag").is(1)).with(Sort.by(Sort.Direction.DESC, "likes"));
        List<ApComment> list = mongoTemplate.find(query, ApComment.class);
        if (list != null && list.size() > 0) {
            ApComment hotComment = list.get(list.size() - 1);
            if (apComment.getLikes() > hotComment.getLikes()) {
                //修改当前数据为热点数据
                apComment.setFlag((short) 1);
                mongoTemplate.save(apComment);
                if(list.size() >= 5){
                    //修改最后一条热点数据为普通评论
                    hotComment.setFlag((short) 0);
                    mongoTemplate.save(hotComment);
                }
            }

        } else {
            apComment.setFlag((short) 1);
            mongoTemplate.save(apComment);
        }


        /*if (list != null && list.size() == 5) {
            ApComment hotComment = list.get(list.size() - 1);
            if (apComment.getLikes() > hotComment.getLikes()) {
                //修改当前数据为热点数据
                apComment.setFlag((short) 1);
                mongoTemplate.save(apComment);
                //修改最后一条热点数据为普通评论
                hotComment.setFlag((short) 0);
                mongoTemplate.save(hotComment);
            }

        } else {
            apComment.setFlag((short) 1);
            mongoTemplate.save(apComment);
        }*/

    }
}
