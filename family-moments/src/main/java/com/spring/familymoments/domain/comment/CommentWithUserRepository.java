package com.spring.familymoments.domain.comment;

import com.spring.familymoments.domain.comment.entity.Comment;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentWithUserRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findById(Long id);

    //유저가 쓴 모든 댓글들 조회
    @Query("SELECT c FROM Comment c WHERE c.writer.userId = :userId")
    List<Comment> findCommentsByUserId(@Param("userId") Long userId);

    @Query("SELECT count(*) FROM Comment c WHERE c.writer = :user AND c.status = 'ACTIVE'")
    Long countCommentsByUserId(@Param("user") User user);

    @Query("SELECT c FROM Comment c WHERE c.postId IN (SELECT p FROM Post p WHERE p.writer.userId = :userId)")
    List<Comment> findByPostUserID(Long userId);

    // 게시글의 ACTIVE 댓글 목록 조회
    @Query("SELECT c FROM Comment c WHERE c.postId.postId = :postId AND c.status = 'ACTIVE'")
    List<Comment> findActiveCommentsByPostId(Long postId);

    // 게시글 내의 모든 댓글 조회
    List<Comment> findByPostId(Post post);
}