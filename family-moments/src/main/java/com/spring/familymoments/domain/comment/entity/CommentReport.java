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
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentId", nullable = false)
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reportReason;

    private String details;

    public static CommentReport createCommentReport(User fromUser, Comment reportedComment, ReportReason reportReason, String details) {
        return CommentReport.builder()
                .user(fromUser)
                .comment(reportedComment)
                .reportReason(reportReason)
                .details(details)
                .build();
    }

    /***
     * SET NULL -> 추후 변경 예정
     */
    public void updateUser() {
        this.user = null;
    }

}
