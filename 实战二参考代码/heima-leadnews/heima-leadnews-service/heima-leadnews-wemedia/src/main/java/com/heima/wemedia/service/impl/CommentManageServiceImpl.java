package com.heima.wemedia.service.impl;

import com.heima.apis.article.IArticleClient;
import com.heima.apis.user.IUserClient;
import com.heima.model.article.dtos.ArticleCommentDto;
import com.heima.model.comment.dtos.CommentConfigDto;
import com.heima.model.comment.dtos.CommentLikeDto;
import com.heima.model.comment.dtos.CommentManageDto;
import com.heima.model.comment.dtos.CommentRepaySaveDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.pojos.*;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.CommentManageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentManageServiceImpl implements CommentManageService {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IArticleClient articleClient;

    /**
     * 查看文章评论列表
     * @param dto
     * @return
     */
    @Override
    public PageResponseResult findNewsComments(ArticleCommentDto dto) {
        WmUser user = WmThreadLocalUtil.getUser();
        dto.setWmUserId(user.getId());
        return articleClient.findNewsComments(dto);
    }

    /**
     * 打开或关闭评论
     * @param dto
     * @return
     */
    @Override
    public ResponseResult updateCommentStatus(CommentConfigDto dto) {
        WmUser wmUser = WmThreadLocalUtil.getUser();

        //app端用户id
        WmUser dbUser = wmUserMapper.selectById(wmUser.getId());
        Integer apUserId = dbUser.getApUserId();

        //清空该文章的所有评论
        List<ApComment> apCommentList = mongoTemplate.find(Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("authorId").is(apUserId)), ApComment.class);
        for (ApComment apComment : apCommentList) {
            List<ApCommentRepay> commentRepayList = mongoTemplate.find(Query.query(Criteria.where("commentId").is(apComment.getId()).and("authorId").is(apUserId)), ApCommentRepay.class);
            List<String> commentRepayIdList = commentRepayList.stream().map(ApCommentRepay::getId).distinct().collect(Collectors.toList());
            //删除所有的评论回复点赞数据
            mongoTemplate.remove(Query.query(Criteria.where("commentRepayId").in(commentRepayIdList).and("authorId").is(apUserId)), ApCommentRepayLike.class);

            //删除该评论的所有的回复内容
            mongoTemplate.remove(Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("authorId").is(apUserId)), ApCommentRepay.class);

            //删除评论的点赞
            mongoTemplate.remove(Query.query(Criteria.where("commentId").is(apComment.getId()).and("authorId").is(apUserId)), ApCommentLike.class);

        }
        //删除评论
        mongoTemplate.remove(Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("authorId").is(apUserId)),ApComment.class);

        //修改app文章的config配置
        return articleClient.updateCommentStatus(dto);
    }

    /**
     * 查询评论列表
     * @return
     */
    @Override
    public ResponseResult list(CommentManageDto dto) {

        List<CommentRepayListVo> commentRepayListVoList = new ArrayList<>();

        Query query = Query.query(Criteria.where("entryId").is(dto.getArticleId()));
        Pageable pageable =  PageRequest.of(dto.getPage(),dto.getSize());
        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "createdTime"));

        List<ApComment> list = mongoTemplate.find(query, ApComment.class);

        for (ApComment apComment : list) {
            CommentRepayListVo vo = new CommentRepayListVo();
            vo.setApComments(apComment);
            Query query2 = Query.query(Criteria.where("commentId").is(apComment.getId()));
            query2.with(Sort.by(Sort.Direction.DESC, "createdTime"));
            List<ApCommentRepay> apCommentRepays = mongoTemplate.find(query2, ApCommentRepay.class);
            vo.setApCommentRepays(apCommentRepays);

            commentRepayListVoList.add(vo);
        }

        return ResponseResult.okResult(commentRepayListVoList);
    }

    /**
     * 删除评论
     * @param commentId
     * @return
     */
    @Override
    public ResponseResult delComment(String commentId) {
        if(StringUtils.isBlank(commentId)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"评论id不能为空");
        }
        //删除评论
        mongoTemplate.remove(Query.query(Criteria.where("id").is(commentId)),ApComment.class);
        //删除该评论的所有的回复内容
        mongoTemplate.remove(Query.query(Criteria.where("commentId").is(commentId)),ApCommentRepay.class);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 删除评论回复
     * @param commentRepayId
     * @return
     */
    @Override
    public ResponseResult delCommentRepay(String commentRepayId) {
        if(StringUtils.isBlank(commentRepayId)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"评论回复id不能为空");
        }
        mongoTemplate.remove(Query.query(Criteria.where("id").is(commentRepayId)),ApCommentRepay.class);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Autowired
    private IUserClient userClient;

    @Autowired
    private WmUserMapper wmUserMapper;

    /**
     * 回复评论
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveCommentRepay(CommentRepaySaveDto dto) {
        //1.检查参数
        if(dto == null || StringUtils.isBlank(dto.getContent()) || dto.getCommentId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if(dto.getContent().length() > 140){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"评论内容不能超过140字");
        }

        //2.安全检查 自行实现


        //获取自媒体人信息
        WmUser wmUser = WmThreadLocalUtil.getUser();
        WmUser dbUser = wmUserMapper.selectById(wmUser.getId());
        if(dbUser == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        //获取app端用户信息
        ApUser apUser = userClient.findUserById(dbUser.getApUserId());
        //3.保存评论
        ApCommentRepay apCommentRepay = new ApCommentRepay();
        apCommentRepay.setAuthorId(apUser.getId());
        apCommentRepay.setAuthorName(apUser.getName());
        apCommentRepay.setContent(dto.getContent());
        apCommentRepay.setCreatedTime(new Date());
        apCommentRepay.setCommentId(dto.getCommentId());

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
    public ResponseResult like(CommentLikeDto dto) {
        //1.检查参数
        if (dto == null || dto.getCommentId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);

        WmUser wmUser = WmThreadLocalUtil.getUser();
        WmUser dbUser = wmUserMapper.selectById(wmUser.getId());
        if(dbUser == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        //获取app端用户信息
        ApUser apUser = userClient.findUserById(dbUser.getApUserId());

        //3.点赞
        if (apComment != null && dto.getOperation() == 0) {
            //更新评论点赞数量
            apComment.setLikes(apComment.getLikes() + 1);
            mongoTemplate.save(apComment);

            //保存评论点赞数据
            ApCommentLike apCommentLike = new ApCommentLike();
            apCommentLike.setCommentId(apComment.getId());
            apCommentLike.setAuthorId(apUser.getId());
            mongoTemplate.save(apCommentLike);
        } else {
            //更新评论点赞数量
            int tmp = apComment.getLikes() - 1;
            tmp = tmp < 1 ? 0 : tmp;
            apComment.setLikes(tmp);
            mongoTemplate.save(apComment);

            //删除评论点赞
            Query query = Query.query(Criteria.where("commentId").is(apComment.getId()).and("authorId").is(apUser.getId()));
            mongoTemplate.remove(query, ApCommentLike.class);
        }

        //4.取消点赞
        Map<String, Object> result = new HashMap<>();
        result.put("likes", apComment.getLikes());
        return ResponseResult.okResult(result);
    }
}
