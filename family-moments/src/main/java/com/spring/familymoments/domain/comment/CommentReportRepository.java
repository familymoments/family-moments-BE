package com.spring.familymoments.domain.comment;

import com.spring.familymoments.domain.comment.entity.Comment;
import com.spring.familymoments.domain.comment.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long>  {
    List<CommentReport> findCommentReportByComment(Comment comment);
}
