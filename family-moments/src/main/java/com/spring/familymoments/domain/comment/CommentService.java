package com.spring.familymoments.domain.comment;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.comment.entity.Comment;
import com.spring.familymoments.domain.comment.model.PostCommentReq;
import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.family.model.PostFamilyRes;
import com.spring.familymoments.domain.post.PostWithUserRepository;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.user.UserRepository;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.NoSuchElementException;

import static com.spring.familymoments.domain.common.entity.UserFamily.Status.ACTIVE;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final UserRepository userRepository;
    private final CommentWithUserRepository commentWithUserRepository;
    private final PostWithUserRepository postWithUserRepository;

    // 댓글 생성하기
    public void createComment(Long userId, Long postId, PostCommentReq postCommentReq) throws BaseException {

        // 사용자
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

        // 게시글
        Post post = postWithUserRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 게시글입니다."));

        // 댓글 생성
        Comment comment = Comment.builder()
                .writer(writer)
                .postId(post)
                .content(postCommentReq.getContent())
                .build();

        // 댓글 저장
        commentWithUserRepository.save(comment);
    }
}
