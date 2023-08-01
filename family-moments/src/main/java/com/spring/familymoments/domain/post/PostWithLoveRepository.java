package com.spring.familymoments.domain.post;

import com.spring.familymoments.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostWithLoveRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByPostId(Long postId);

}
