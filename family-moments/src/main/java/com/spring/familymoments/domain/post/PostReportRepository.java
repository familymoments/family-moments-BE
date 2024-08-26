package com.spring.familymoments.domain.post;

import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.post.entity.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    List<PostReport> findPostReportByPost(Post post);
}
