package com.heima.comment.service.impl;

import com.heima.apis.user.IUserClient;
import com.heima.comment.pojos.ApComment;
import com.heima.comment.pojos.ApCommentRepay;
import com.heima.comment.pojos.ApCommentRepayLike;
import com.heima.comment.pojos.CommentRepayVo;
import com.heima.comment.service.CommentRepayService;
import com.heima.model.comment.dtos.CommentRepayDto;
import com.heima.model.comment.dtos.CommentRepayLikeDto;
import com.heima.model.comment.dtos.CommentRepaySaveDto;
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
public class CommentRepayServiceImpl implements CommentRepayService {

    @Autowired
    private IUserClient userClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseResult loadCommentRepay(CommentRepayDto dto) {
        //1.检查参数
        if(dto == null || dto.getCommentId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        int size = 20;

        //2.加载数据
        Query query = Query.query(Criteria.where("commentId").is(dto.getCommentId()).and("createdTime").lt(dto.getMinDate()));
        query.with(Sort.by(Sort.Direction.DESC,"createdTime")).limit(size);
        List<ApCommentRepay> list = mongoTemplate.find(query, ApCommentRepay.class);

        //3.数据封装返回
        //3.1 用户未登录
        ApUser user = AppThreadLocalUtil.getUser();
        if(user == null){
            return ResponseResult.okResult(list);
        }

        //3.2 用户已登录

        //需要查询当前评论中哪些数据被点赞了
        List<String> idList = list.stream().map(x -> x.getId()).collect(Collectors.toList());
        Query query1 = Query.query(Criteria.where("commentRepayId").in(idList).and("authorId").is(user.getId()));
        List<ApCommentRepayLike> apCommentRepayLikes = mongoTemplate.find(query1, ApCommentRepayLike.class);
        if(apCommentRepayLikes == null || apCommentRepayLikes.size() == 0 ){
            return ResponseResult.okResult(list);
        }

        List<CommentRepayVo> resultList = new ArrayList<>();
        list.forEach(x->{
            CommentRepayVo vo = new CommentRepayVo();
            BeanUtils.copyProperties(x,vo);
            for (ApCommentRepayLike apCommentRepayLike : apCommentRepayLikes) {
                if(x.getId().equals(apCommentRepayLike.getCommentRepayId())){
                    vo.setOperation((short)0);
                    break;
                }
            }
            resultList.add(vo);
        });

        return ResponseResult.okResult(resultList);
    }

    @Override
    public ResponseResult saveCommentRepay(CommentRepaySaveDto dto) {
        //1.检查参数
        if(dto == null || StringUtils.isBlank(dto.getContent()) || dto.getCommentId() == null){
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
        ApCommentRepay apCommentRepay = new ApCommentRepay();
        apCommentRepay.setAuthorId(user.getId());
        apCommentRepay.setContent(dto.getContent());
        apCommentRepay.setCreatedTime(new Date());
        apCommentRepay.setCommentId(dto.getCommentId());
        apCommentRepay.setAuthorName(dbUser.getName());
        apCommentRepay.setUpdatedTime(new Date());
        apCommentRepay.setLikes(0);
        mongoTemplate.save(apCommentRepay);

        //5更新回复数量
        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);
        apComment.setReply(apComment.getReply()+1);
        mongoTemplate.save(apComment);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult saveCommentRepayLike(CommentRepayLikeDto dto) {
        //1.检查参数
        if(dto == null || dto.getCommentRepayId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.判断是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if(user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        ApCommentRepay apCommentRepay = mongoTemplate.findById(dto.getCommentRepayId(), ApCommentRepay.class);

        //3.点赞
        if(apCommentRepay != null && dto.getOperation() == 0){
            //更新评论点赞数量
            apCommentRepay.setLikes(apCommentRepay.getLikes()+1);
            mongoTemplate.save(apCommentRepay);

            //保存评论点赞数据
            ApCommentRepayLike apCommentRepayLike = new ApCommentRepayLike();
            apCommentRepayLike.setCommentRepayId(apCommentRepay.getId());
            apCommentRepayLike.setAuthorId(user.getId());
            mongoTemplate.save(apCommentRepayLike);
        }else {
            //更新评论点赞数量
            int tmp = apCommentRepay.getLikes()-1;
            tmp = tmp < 1 ? 0 : tmp;
            apCommentRepay.setLikes(tmp);
            mongoTemplate.save(apCommentRepay);

            //删除评论点赞
            Query query = Query.query(Criteria.where("commentRepayId").is(apCommentRepay.getId()).and("authorId").is(user.getId()));
            mongoTemplate.remove(query,ApCommentRepayLike.class);
        }

        //4.取消点赞
        Map<String,Object> result = new HashMap<>();
        result.put("likes",apCommentRepay.getLikes());
        return ResponseResult.okResult(result);
    }
}
