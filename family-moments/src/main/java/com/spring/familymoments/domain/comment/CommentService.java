package com.spring.familymoments.domain.comment;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.comment.entity.Comment;
import com.spring.familymoments.domain.comment.model.GetCommentsRes;
import com.spring.familymoments.domain.comment.model.PostCommentReq;
import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.post.PostWithUserRepository;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.spring.familymoments.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
// @Transactional
public class CommentService {
    private final CommentWithUserRepository commentWithUserRepository;
    private final PostWithUserRepository postWithUserRepository;

    // 댓글 생성하기
    @Transactional
    public void createComment(User user, Long postId, PostCommentReq postCommentReq) throws BaseException {

        // 사용자
//        User writer = userRepository.findById(userId)
//                .orElseThrow(() -> new BaseException(FAILED_USERSS_UNATHORIZED));

        // 유저 존재 확인
        if(user==null){
            throw new BaseException(FIND_FAIL_USERNAME);
        }

        // 게시글
        Post post = postWithUserRepository.findById(postId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_POST));

        // 게시글 상태 확인
        if (post.getStatus() == Post.Status.ACTIVE) {
            // 댓글 생성
            Comment comment = Comment.builder()
                    .writer(user)
                    .postId(post)
                    .content(postCommentReq.getContent())
                    .build();

            // 댓글 저장
            commentWithUserRepository.save(comment);
        } else {
            // 게시글이 INACTIVE일 경우
            throw new BaseException(FIND_FAIL_POST);
        }
    }

    // 특정 게시물의 댓글 목록 조회
    @Transactional
    public List<GetCommentsRes> getCommentsByPostId(Long postId) throws BaseException{

        // 게시글 존재 확인
        postWithUserRepository.findById(postId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_POST));

        List<Comment> activeComments = commentWithUserRepository.findActiveCommentsByPostId(postId);

        if(activeComments.isEmpty() || activeComments == null) {
            throw new BaseException(NO_ACTIVE_COMMENTS);
        }

        List<GetCommentsRes> getCommentsResList = activeComments.stream()
                .map(comment -> new GetCommentsRes(
                        comment.getPostId().getPostId(),
                        comment.getCommentId(),
                        comment.getWriter().getNickname(),
                        comment.getWriter().getProfileImg(),
                        comment.getContent(),
                        comment.getCountLove() != 0,
                        comment.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return getCommentsResList;
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(User user, Long commentId) throws BaseException{

        // 유저 존재 확인
        if(user==null){
            throw new BaseException(FIND_FAIL_USERNAME);
        }

        // 댓글 존재 확인
        Comment comment = commentWithUserRepository.findById(commentId)
                .orElseThrow(() -> new BaseException(FIND_FAIL_FAMILY));

        // 생성자 권한 확인
        if (!comment.getWriter().getUserId().equals(user.getUserId())) {
            throw new BaseException(FAILED_USERSS_UNATHORIZED);
        }

        // 기존 삭제 여부 확인
        if (comment.getStatus().equals(BaseEntity.Status.INACTIVE)) {
            throw new BaseException(ALREADY_DELETE_COMMENT);
        }

        // 댓글 삭제
        comment.updateStatus(BaseEntity.Status.INACTIVE);
        commentWithUserRepository.save(comment);

    }
}
