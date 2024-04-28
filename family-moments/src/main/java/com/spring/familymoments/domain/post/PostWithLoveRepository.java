package com.spring.familymoments.domain.post;

import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostWithLoveRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByPostId(Long postId);

    @Query("SELECT p FROM Post p " +
            "JOIN PostLove pl " +
                "ON p = pl.postId " +
            "WHERE pl.userId = :user " +
            "AND pl.status = 'ACTIVE' " +
            "AND p.familyId.familyId = :familyId " +
            "ORDER BY pl.updatedAt DESC")
    List<Post> findPostsByUserAndFamilyId(@Param("user") User user, @Param("familyId") long familyId, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "JOIN PostLove pl " +
            "ON p = pl.postId " +
            "WHERE pl.userId = :user  " +
            "AND pl.status = 'ACTIVE' " +
            "AND p.familyId.familyId = :familyId " +
            "AND p.postId < :postId " +
            "ORDER BY pl.updatedAt DESC")
    List<Post> findByUserAndFamilyIdAfterPostId(@Param("user") User user, @Param("familyId") long familyId, @Param("postId") long postId, Pageable pageable);

}
