package com.heima.comment.service.impl;

import com.heima.apis.user.IUserClient;
import com.heima.comment.pojos.ApComment;
import com.heima.comment.pojos.ApCommentLike;
import com.heima.comment.pojos.CommentVo;
import com.heima.comment.service.CommentHotService;
import com.heima.comment.service.CommentService;
import com.heima.model.comment.dtos.CommentDto;
import com.heima.model.comment.dtos.CommentLikeDto;
import com.heima.model.comment.dtos.CommentSaveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private IUserClient userClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseResult saveComment(CommentSaveDto dto) {
        //1.检查参数
        if(dto == null || StringUtils.isBlank(dto.getContent()) || dto.getArticleId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if(dto.getContent().length() > 140){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"评论内容不能超过140字");
        }

        //2.判断是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if(user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        //3.安全检查 自行实现

        //4.保存评论
        ApUser dbUser = userClient.findUserById(user.getId());
        if(dbUser == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"当前登录信息有误");
        }
        ApComment apComment = new ApComment();
        apComment.setAuthorId(user.getId());
        apComment.setContent(dto.getContent());
        apComment.setCreatedTime(new Date());
        apComment.setEntryId(dto.getArticleId());
        apComment.setImage(dbUser.getImage());
        apComment.setAuthorName(dbUser.getName());
        apComment.setLikes(0);
        apComment.setReply(0);
        apComment.setType((short)0);
        apComment.setFlag((short)0);
        mongoTemplate.save(apComment);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Autowired
    private CommentHotService commentHotService;

    @Override
    public ResponseResult like(CommentLikeDto dto) {
        //1.检查参数
        if(dto == null || dto.getCommentId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.判断是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if(user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);

        //3.点赞
        if(apComment != null && dto.getOperation() == 0){
            //更新评论点赞数量
            apComment.setLikes(apComment.getLikes()+1);
            mongoTemplate.save(apComment);

            //计算热点评论
//            if(apComment.getFlag().shortValue() == 0 && apComment.getLikes() >= 5){
//                commentHotService.findHotComment(apComment.getEntryId(),apComment);
//            }

            //保存评论点赞数据
            ApCommentLike apCommentLike = new ApCommentLike();
            apCommentLike.setCommentId(apComment.getId());
            apCommentLike.setAuthorId(user.getId());
            mongoTemplate.save(apCommentLike);
        }else {
            //更新评论点赞数量
            int tmp = apComment.getLikes()-1;
            tmp = tmp < 1 ? 0 : tmp;
            apComment.setLikes(tmp);
            mongoTemplate.save(apComment);

            //删除评论点赞
            Query query = Query.query(Criteria.where("commentId").is(apComment.getId()).and("authorId").is(user.getId()));
            mongoTemplate.remove(query,ApCommentLike.class);
        }

        //4.取消点赞
        Map<String,Object> result = new HashMap<>();
        result.put("likes",apComment.getLikes());
        return ResponseResult.okResult(result);
    }

    @Override
    public ResponseResult findByArticleId(CommentDto dto) {
        //1.检查参数
        if(dto == null || dto.getArticleId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        int size = 10;

        //2.加载数据
        Query query = Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("createdTime").lt(dto.getMinDate()));
        query.with(Sort.by(Sort.Direction.DESC,"createdTime")).limit(size);
        List<ApComment> list = mongoTemplate.find(query, ApComment.class);

        /*List<ApComment> list = null;
        if(dto.getIndex() == 1){
            //首页
            Query query = Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("flag").is(1));
            query.with(Sort.by(Sort.Direction.DESC,"likes"));
            list = mongoTemplate.find(query, ApComment.class);
            if(list != null && list.size() > 0){
                //补全首页的数据
                size = size - list.size();
                Query query1 = Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("flag").is(0).and("createdTime").lt(dto.getMinDate()));
                query.with(Sort.by(Sort.Direction.DESC,"createdTime")).limit(size);
                List<ApComment> list2 = mongoTemplate.find(query1, ApComment.class);
                list.addAll(list2);
            }else {
                Query query2 = Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("flag").is(0).and("createdTime").lt(dto.getMinDate()));
                query.with(Sort.by(Sort.Direction.DESC,"createdTime")).limit(size);
                list = mongoTemplate.find(query2, ApComment.class);
            }

        }else {
            //不是首页
            Query query = Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("flag").is(0).and("createdTime").lt(dto.getMinDate()));
            query.with(Sort.by(Sort.Direction.DESC,"createdTime")).limit(size);
            list = mongoTemplate.find(query, ApComment.class);
        }*/

        //3.数据封装返回
        //3.1 用户未登录
        ApUser user = AppThreadLocalUtil.getUser();
        if(user == null){
            return ResponseResult.okResult(list);
        }

        //3.2 用户已登录

        //需要查询当前评论中哪些数据被点赞了
        List<String> idList = list.stream().map(x -> x.getId()).collect(Collectors.toList());
        Query query1 = Query.query(Criteria.where("commentId").in(idList).and("authorId").is(user.getId()));
        List<ApCommentLike> apCommentLikes = mongoTemplate.find(query1, ApCommentLike.class);
        if(apCommentLikes == null){
            return ResponseResult.okResult(list);
        }

        List<CommentVo> resultList = new ArrayList<>();
        list.forEach(x->{
            CommentVo vo = new CommentVo();
            BeanUtils.copyProperties(x,vo);
            for (ApCommentLike apCommentLike : apCommentLikes) {
                if(x.getId().equals(apCommentLike.getCommentId())){
                    vo.setOperation((short)0);
                    break;
                }
            }
            resultList.add(vo);
        });

        return ResponseResult.okResult(resultList);
    }
}
