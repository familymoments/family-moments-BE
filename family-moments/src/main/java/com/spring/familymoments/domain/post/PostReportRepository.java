package com.spring.familymoments.domain.post;

import com.spring.familymoments.domain.post.entity.PostReport;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    List<PostReport> findPostReportByUser(User user);
}
