package com.spring.familymoments.domain.comment;

import com.spring.familymoments.domain.comment.entity.CommentReport;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long>  {
    List<CommentReport> findCommentReportByUser(User user);
}
