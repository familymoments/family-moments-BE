package com.spring.familymoments.domain.post;

import com.spring.familymoments.domain.post.entity.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
}
