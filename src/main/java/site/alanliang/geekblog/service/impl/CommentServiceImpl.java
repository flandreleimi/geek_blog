package site.alanliang.geekblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.alanliang.geekblog.dao.CommentMapper;
import site.alanliang.geekblog.dao.UserMapper;
import site.alanliang.geekblog.dao.VisitorMapper;
import site.alanliang.geekblog.model.Comment;
import site.alanliang.geekblog.model.User;
import site.alanliang.geekblog.model.Visitor;
import site.alanliang.geekblog.query.CommentQuery;
import site.alanliang.geekblog.service.CommentService;
import site.alanliang.geekblog.utils.LinkedListUtil;
import site.alanliang.geekblog.utils.StringUtils;
import site.alanliang.geekblog.vo.AuditVO;

import java.util.List;

/**
 * @Descriptin TODO
 * @Author AlanLiang
 * Date 2020/5/17 9:33
 * Version 1.0
 **/
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(Comment comment) {
        commentMapper.insert(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdList(List<Long> idList) {
        commentMapper.deleteBatchIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        commentMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reply(Comment comment) {
        if (comment.getVisitorId() != null) {
            QueryWrapper<Comment> commentWrapper = new QueryWrapper<>();
            commentWrapper.select("visitor_id").eq("id", comment.getPid());
            Comment parentComment = commentMapper.selectOne(commentWrapper);
            QueryWrapper<Visitor> visitorWrapper = new QueryWrapper<>();
            visitorWrapper.select("nickname").eq("id", parentComment.getVisitorId());
            Visitor visitor = visitorMapper.selectOne(visitorWrapper);
            comment.setParentNickname(visitor.getNickname());
        } else {
            QueryWrapper<User> userWrapper = new QueryWrapper<>();
            userWrapper.select("nickname").eq("id", comment.getUserId());
            User user = userMapper.selectOne(userWrapper);
            comment.setParentNickname(user.getNickname());
        }
        commentMapper.insert(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(AuditVO auditVO) {
        Comment comment = new Comment();
        comment.setId(auditVO.getId());
        comment.setStatus(auditVO.getStatus());
        commentMapper.updateById(comment);
    }

    @Override
    public Page<Comment> listTableByPage(Integer current, Integer size, CommentQuery commentQuery) {
        Page<Comment> page = new Page<>(current, size);
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        if (commentQuery.getStartDate() != null && commentQuery.getEndDate() != null) {
            wrapper.between("create_time", commentQuery.getStartDate(), commentQuery.getEndDate());
        }
        if (commentQuery.getStatus() != null) {
            wrapper.eq("status", commentQuery.getStatus());
        }
        return commentMapper.listTableByPage(page, wrapper);
    }

    @Override
    public Page<Comment> listByArticleId(Long articleId, Page<Comment> page) {
        Page<Comment> pageInfo = commentMapper.listRootPageByArticleId(page, articleId);
        List<Comment> comments = commentMapper.listByArticleId(articleId);
        LinkedListUtil.toCommentLinkedList(pageInfo.getRecords(), comments);
        return pageInfo;
    }
}
