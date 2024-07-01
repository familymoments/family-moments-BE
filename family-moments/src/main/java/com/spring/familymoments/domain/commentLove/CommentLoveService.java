package com.spring.familymoments.domain.commentLove;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.comment.CommentWithUserRepository;
import com.spring.familymoments.domain.comment.entity.Comment;
import com.spring.familymoments.domain.commentLove.entity.CommentLove;
import com.spring.familymoments.domain.commentLove.model.CommentLoveReq;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spring.familymoments.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class CommentLoveService {

    private final CommentLoveRepository commentLoveRepository;
    private final CommentWithUserRepository commentWithUserRepository;

    @Transactional
    public void createLove(User user, CommentLoveReq commentLoveReq) {
        Comment comment = commentWithUserRepository.findById(commentLoveReq.getCommentId())
                .orElseThrow(() -> new BaseException(FIND_FAIL_COMMENT));

        // 이미 좋아요를 누른 경우
        if(checkDuplicateCommentLove(comment, user)){
            throw new BaseException(COMMENTLOVE_ALREADY_EXISTS);
        }

        CommentLove commentLove = CommentLove.builder()
                .commentId(comment)
                .userId(user)
                .build();

        comment.increaseCountLove();
        commentLoveRepository.save(commentLove);
    }

    @Transactional
    public void deleteLove(User user, CommentLoveReq commentLoveReq) {
        Comment comment = commentWithUserRepository.findById(commentLoveReq.getCommentId())
                .orElseThrow(() -> new BaseException(FIND_FAIL_COMMENT));

        CommentLove commentLove = commentLoveRepository.findByCommentIdAndUserId(comment, user)
                .orElseThrow(() -> new BaseException(FIND_FAIL_COMMENTLOVE));

        comment.decreaseCountLove();
        commentLoveRepository.delete(commentLove);
    }


    /**
     * 댓글 좋아요 중복 확인
     */
    private boolean checkDuplicateCommentLove(Comment comment, User user) throws BaseException {
        return commentLoveRepository.existsByCommentIdAndUserId(comment, user);
    }
}
