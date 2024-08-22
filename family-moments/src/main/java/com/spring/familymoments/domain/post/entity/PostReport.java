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
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false)
    private Post post;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reportReason;

    private String details;

    public static PostReport createPostReport(User fromUser, Post reportedPost, ReportReason reportReason, String details) {
        return PostReport.builder()
                .user(fromUser)
                .post(reportedPost)
                .reportReason(reportReason)
                .details(details)
                .build();
    }

    /**
     * 게시글 신고 null 처리
     */
    public void updateUser() {
        this.user = null;
    }

}
