package com.spring.familymoments.domain.post.entity;

import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "PostReport")
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class PostReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postReportId", nullable = false, updatable = false)
    private Long postReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reportReason;

    private String details;

    @Column(nullable = false, length = 45)
    private String offenderEmail;

    public static PostReport createPostReport(User fromUser, Post reportedPost, ReportReason reportReason, String details, String offenderEmail) {
        return PostReport.builder()
                .user(fromUser)
                .post(reportedPost)
                .reportReason(reportReason)
                .details(details)
                .offenderEmail(offenderEmail)
                .build();
    }

    /**
     * 게시글 신고 null 처리
     */
    public void updatePost() { this.post = null; }
}
