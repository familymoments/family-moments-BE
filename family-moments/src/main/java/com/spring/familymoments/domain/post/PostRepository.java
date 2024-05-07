package com.spring.familymoments.domain.post;

import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.post.model.MultiPostRes;
import com.spring.familymoments.domain.post.model.SinglePostRes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.familyId.familyId = :familyId " +
            "AND p.status = 'ACTIVE' " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByFamilyIdOrderByCreatedAtDesc(@Param("familyId") long familyId, Pageable pageable);

    // [Post] Paging by postId
    @Query("SELECT p FROM Post p WHERE p.familyId.familyId = :familyId " +
            "AND p.status = 'ACTIVE' " +
            "AND p.postId < :postId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByFamilyIdAfterPostId(@Param("familyId") long familyId, @Param("postId") long postId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.familyId.familyId = :familyId " +
            "AND p.status = 'ACTIVE' " +
            "AND FUNCTION('DATE', p.createdAt) = FUNCTION('DATE', :date) " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByFamilyIdAndCreatedAtDesc(@Param("familyId") long familyId, @Param("date") LocalDateTime date, Pageable pageable);

    // [Post] Paging by date and postId
    @Query("SELECT p FROM Post p WHERE p.familyId.familyId = :familyId " +
            "AND p.status = 'ACTIVE' " +
            "AND FUNCTION('DATE', p.createdAt) = FUNCTION('DATE', :date) " +
            "AND p.postId < :postId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByFamilyIdWithDateAfterPostId(@Param("familyId") long familyId, @Param("date") LocalDateTime date, @Param("postId") long postId, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Post p " +
            "SET p.countLove = (SELECT COUNT(*) FROM PostLove pl WHERE pl.postId = :postId AND pl.status = 'ACTIVE') " +
            "WHERE p.postId = :postId", nativeQuery = true)
    void updateCountLove(@Param("postId") long postId);

    Post findByPostIdAndStatus(long postId, BaseEntity.Status status);

    @Query("SELECT distinct p.createdAt " +
            "FROM Post p " +
            "WHERE p.familyId.familyId = :familyId AND p.status = :status " +
            "AND p.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY p.createdAt ASC ")
    List<LocalDateTime> getDateExistPost(@Param("familyId") long familyId, @Param("status") BaseEntity.Status status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<Post> findByFamilyIdAndStatusOrderByPostIdDesc(Family family, BaseEntity.Status status, Pageable pageable);

    // [Album] Paging by postId
    @Query("SELECT p FROM Post p WHERE p.familyId.familyId = :familyId " +
            "AND p.status = 'ACTIVE' " +
            "AND p.postId > :postId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByFamilyIdAndBeforePostId(@Param("familyId") long familyId, @Param("postId") long postId, Pageable pageable);

}
