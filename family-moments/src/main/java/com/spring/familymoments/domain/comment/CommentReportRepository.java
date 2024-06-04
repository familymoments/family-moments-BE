package com.spring.familymoments.domain.comment;

import com.spring.familymoments.domain.comment.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long>  {
}
