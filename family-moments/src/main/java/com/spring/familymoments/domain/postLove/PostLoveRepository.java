package com.spring.familymoments.domain.postLove;

import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.postLove.entity.PostLove;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.CommentRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLoveRepository extends JpaRepository<PostLove, Long> {

    Optional<PostLove> findByPostIdAndUserId(Post post, User user);

    @Query("SELECT new com.spring.familymoments.domain.user.model.CommentRes(u.nickname, u.profileImg) " +
            "FROM PostLove pl JOIN pl.userId u " +
            "WHERE pl.postId = :post " +
            "AND pl.status = 'ACTIVE' " +
            "order by pl.updatedAt ASC")
    List<CommentRes> findByPost(Post post);

    @Query("SELECT count(*) FROM PostLove p WHERE p.userId = :user AND p.status = 'ACTIVE'")
    Long countLovedPostsByWriter(@Param("user") User user);

    boolean existsByPostIdAndUserId(Post post, User user);

    @Query("SELECT pl FROM PostLove pl WHERE pl.userId.userId = :userId")
    List<PostLove> findPostLovesByUserId(@Param("userId") Long userId);
    @Query("SELECT pl FROM PostLove pl WHERE pl.postId IN (SELECT p FROM Post p WHERE p.writer.userId = :userId)")
    List<PostLove> findPostLovesByPostUserId(Long userId);
}
