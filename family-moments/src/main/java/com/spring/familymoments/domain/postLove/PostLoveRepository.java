package com.spring.familymoments.domain.postLove;

import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.postLove.entity.PostLove;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.CommentRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    boolean existsByPostIdAndUserId(Post post, User user);
}
