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
    @Query("SELECT " +
            "new com.spring.familymoments.domain.post.model.MultiPostRes" +
            "(p.postId, u.nickname, u.profileImg, p.content, CONCAT(COALESCE(p.img1, ''), ',', COALESCE(p.img2, ''), ',', COALESCE(p.img3, ''), ',', COALESCE(p.img4, '')), p.createdAt, pl.status) " +
            "FROM Post p JOIN p.writer u " +
            "LEFT JOIN PostLove pl On p = pl.postId AND pl.userId.userId = :userId " +
            "WHERE p.familyId.familyId = :familyId " +
            "AND p.status = 'ACTIVE' " +
            "ORDER BY p.createdAt DESC")
    List<MultiPostRes> findByFamilyId(@Param("familyId") long familyId, @Param("userId") long userId, Pageable pageable);

    @Query("SELECT " +
            "new com.spring.familymoments.domain.post.model.MultiPostRes" +
            "(p.postId, u.nickname, u.profileImg, p.content, CONCAT(COALESCE(p.img1, ''), ',', COALESCE(p.img2, ''), ',', COALESCE(p.img3, ''), ',', COALESCE(p.img4, '')), p.createdAt, pl.status) " +
            "FROM Post p JOIN p.writer u " +
            "LEFT JOIN PostLove pl On p = pl.postId AND pl.userId.userId = :userId " +
            "WHERE p.familyId.familyId = :familyId AND p.postId < :postId " +
            "AND p.status = 'ACTIVE' " +
            "ORDER BY p.createdAt DESC")
    List<MultiPostRes> findByFamilyId(@Param("familyId") long familyId, @Param("userId") long userId, @Param("postId") long postId, Pageable pageable);

    @Query("SELECT " +
            "new com.spring.familymoments.domain.post.model.MultiPostRes" +
            "(p.postId, u.nickname, u.profileImg, p.content, CONCAT(COALESCE(p.img1, ''), ',', COALESCE(p.img2, ''), ',', COALESCE(p.img3, ''), ',', COALESCE(p.img4, '')), p.createdAt, pl.status) " +
            "FROM Post p JOIN p.writer u " +
            "LEFT JOIN PostLove pl On p = pl.postId AND pl.userId.userId = :userId " +
            "WHERE p.familyId.familyId = :familyId " +
            "AND p.status = 'ACTIVE' " +
            "AND FUNCTION('DATE', p.createdAt)  = FUNCTION('DATE', :date) " +
            "ORDER BY p.createdAt DESC")
    List<MultiPostRes> findByFamilyIdWithDate(@Param("familyId") long familyId, @Param("userId") long userId, @Param("date") LocalDateTime date, Pageable pageable);

    @Query("SELECT " +
            "new com.spring.familymoments.domain.post.model.MultiPostRes" +
            "(p.postId, u.nickname, u.profileImg, p.content, CONCAT(COALESCE(p.img1, ''), ',', COALESCE(p.img2, ''), ',', COALESCE(p.img3, ''), ',', COALESCE(p.img4, '')), p.createdAt, pl.status) " +
            "FROM Post p JOIN p.writer u " +
            "LEFT JOIN PostLove pl On p = pl.postId AND pl.userId.userId = :userId " +
            "WHERE p.familyId.familyId = :familyId " +
            "AND p.status = 'ACTIVE' " +
            "AND FUNCTION('DATE', p.createdAt)  = FUNCTION('DATE', :date) " +
            "AND p.postId < :postId " +
            "ORDER BY p.createdAt DESC")
    List<MultiPostRes> findByFamilyIdWithDateAfterPostId(@Param("familyId") long familyId, @Param("userId") long userId, @Param("date") LocalDateTime date, @Param("postId") long postId, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Post p " +
            "SET p.countLove = (SELECT COUNT(*) FROM PostLove pl WHERE pl.postId = :postId AND pl.status = 'ACTIVE') " +
            "WHERE p.postId = :postId", nativeQuery = true)
    void updateCountLove(@Param("postId") long postId);

    @Query("SELECT " +
            "new com.spring.familymoments.domain.post.model.SinglePostRes" +
            "(p.postId, u.nickname, u.profileImg, p.content, CONCAT(COALESCE(p.img1, ''), ',', COALESCE(p.img2, ''), ',', COALESCE(p.img3, ''), ',', COALESCE(p.img4, '')), p.createdAt, p.countLove, pl.status) " +
            "FROM Post p JOIN p.writer u " +
            "LEFT JOIN PostLove pl On p = pl.postId AND pl.userId.userId = :userId " +
            "WHERE p.postId = :postId " +
            "AND p.status = 'ACTIVE'")
    SinglePostRes findByPostId(@Param("userId") long userId, @Param("postId") long postId);

    Post findByPostIdAndStatus(long postId, BaseEntity.Status status);

    @Query("SELECT distinct p.createdAt " +
            "FROM Post p " +
            "WHERE p.familyId.familyId = :familyId AND p.status = :status " +
            "AND p.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY p.createdAt ASC ")
    List<LocalDateTime> getDateExistPost(@Param("familyId") long familyId, @Param("status") BaseEntity.Status status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<Post> findByFamilyIdAndStatusOrderByPostIdDesc(Family family, BaseEntity.Status status, Pageable pageable);

    List<Post> findByFamilyIdAndPostIdLessThanAndStatusOrderByPostIdDesc(Family family, long postId, BaseEntity.Status status, Pageable pageable);
}
