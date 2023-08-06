package com.spring.familymoments.domain.commentLove;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.comment.CommentWithUserRepository;
import com.spring.familymoments.domain.comment.entity.Comment;
import com.spring.familymoments.domain.commentLove.entity.CommentLove;
import com.spring.familymoments.domain.commentLove.model.CommentLoveReq;
import com.spring.familymoments.domain.user.UserRepository;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.spring.familymoments.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class CommentLoveService {

    private final CommentLoveRepository commentLoveRepository;
    private final CommentWithUserRepository commentWithUserRepository;
    private final UserRepository userRepository;

    /**
     * createLove
     * [POST]
     * @return
     */
    @Transactional
    public void createLove(User user, CommentLoveReq commentLoveReq) throws BaseException {

        User member = userRepository.findById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("[좋아요 누르기] 존재하지 않는 아이디입니다."));

        Comment comment = commentWithUserRepository.findById(commentLoveReq.getCommentId())
                .orElseThrow(() -> new NoSuchElementException("[좋아요 누르기] 존재하지 않는 댓글입니다."));

        // 이미 좋아요를 누른 경우
        if(commentLoveRepository.findByCommentIdAndUserId(comment, member).isPresent()){
            throw new BaseException(COMMENTLOVE_ALREADY_EXISTS);
        }

        CommentLove commentLove = CommentLove.builder()
                .commentId(comment)
                .userId(member)
                .build();

        commentLoveRepository.save(commentLove);
    }

    /**
     * deleteLove
     * [DELETE]
     * @return
     */
    @Transactional
    public void deleteLove(User user, CommentLoveReq commentLoveReq) throws BaseException {

        User member = userRepository.findById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("[좋아요 취소] 존재하지 않는 아이디입니다."));

        Comment comment = commentWithUserRepository.findById(commentLoveReq.getCommentId())
                .orElseThrow(() -> new NoSuchElementException("[좋아요 누르기] 존재하지 않는 댓글입니다."));


        Optional<CommentLove> commentLove = commentLoveRepository.findByCommentIdAndUserId(comment, member);

        // 좋아요를 누르지 않은 경우
        if(commentLove.isEmpty()){
            throw new BaseException(FIND_FAIL_COMMENTLOVE);
        }

        commentLoveRepository.delete(commentLove.get());
    }
}
