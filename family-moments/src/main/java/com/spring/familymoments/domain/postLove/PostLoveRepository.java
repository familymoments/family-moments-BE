package com.spring.familymoments.domain.postLove;

import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.postLove.entity.PostLove;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLoveRepository extends JpaRepository<PostLove, Long> {

    Optional<PostLove> findByPostIdAndUserId(Post post, User user);

    boolean existsByPostIdAndUserId(Post post, User user);
}
