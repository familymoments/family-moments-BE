package com.spring.familymoments.domain.post;

import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostWithUserRepository extends JpaRepository<Post, Long> {
    //현재 가족에서의 내 게시글 업로드 수 (active)
    @Query("SELECT count(*) FROM Post p WHERE p.writer = :user AND p.familyId = :family AND p.status = 'ACTIVE'")
    Long countActivePostsByWriterAndFamily(@Param("user") User user, @Param("family") Family family);

    //유저가 작성한 모든 게시글 조회
    @Query("SELECT p FROM Post p WHERE p.writer.userId = :userId")
    List<Post> findPostByUserId(@Param("userId")Long userId);

    // 가족 내에 속한 모든 게시글 조회
    List<Post> findByFamilyId(Family family);
}
