package com.spring.familymoments.domain.post;

import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostWithUserRepository extends JpaRepository<Post, Long> {
    //유저가 작성한 모든 게시글 조회
    @Query("SELECT p FROM Post p JOIN FETCH p.writerId u WHERE u.userId = :userId")
    List<Post> findPostByUserId(Long userId);
}
