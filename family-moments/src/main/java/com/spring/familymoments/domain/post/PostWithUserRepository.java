package com.spring.familymoments.domain.post;

import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostWithUserRepository extends JpaRepository<Post, Long> {
    //현재 가족에서의 내 게시글 업로드 수 (active)
    @Query("SELECT count(*) FROM Post p WHERE p.writer = :user AND p.familyId = :family AND p.status = 'ACTIVE'")
    Long countActivePostsByWriterAndFamily(@Param("user") User user, @Param("family") Family family);

    @Query("SELECT count(*) FROM Post p WHERE p.writer = :user AND p.status = 'ACTIVE'")
    Long countActivePostsByWriter(@Param("user") User user);

    @Query("SELECT p FROM Post p WHERE p.writer = :user AND p.familyId.familyId = :familyId " +
            "AND p.status = 'ACTIVE' " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByUserAndFamilyId(@Param("user") User user, @Param("familyId") long familyId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.writer = :user AND p.familyId.familyId = :familyId " +
            "AND p.status = 'ACTIVE' " +
            "AND p.postId < :postId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByUserAndFamilyIdAfterPostId(@Param("user") User user, @Param("familyId") long familyId, @Param("postId") long postId, Pageable pageable);

    //유저가 작성한 모든 게시글 조회
    @Query("SELECT p FROM Post p WHERE p.writer.userId = :userId")
    List<Post> findPostByUserId(@Param("userId")Long userId);

    // 가족 내에 속한 모든 게시글 조회
    List<Post> findByFamilyId(Family family);
}
