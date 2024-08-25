package com.spring.familymoments.domain.comment.entity;

import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.post.entity.ReportReason;
import com.spring.familymoments.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "CommentReport")
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class CommentReport extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentReportId", nullable = false, updatable = false)
    private Long commentReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentId")
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reportReason;

    private String details;

    @Column(nullable = false, length = 45)
    private String offenderEmail;

    public static CommentReport createCommentReport(User fromUser, Comment reportedComment, ReportReason reportReason, String details, String offenderEmail) {
        return CommentReport.builder()
                .user(fromUser)
                .comment(reportedComment)
                .reportReason(reportReason)
                .details(details)
                .offenderEmail(offenderEmail)
                .build();
    }

    /***
     * 댓글 신고 NULL 처리
     */
    public void updateComment() {this.comment = null;}
}
