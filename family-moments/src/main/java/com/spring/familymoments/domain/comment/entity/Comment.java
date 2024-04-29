package com.spring.familymoments.domain.comment.entity;

import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.user.entity.User;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "Comment")
@Getter
@NoArgsConstructor(force = true)
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentId", nullable = false, updatable = false)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer", nullable = false)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId", nullable = false)
    private Post postId;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "countLove", columnDefinition = "int unsigned")
    @ColumnDefault("0")
    private int countLove;

    /**
     * 댓글 삭제 API 관련 메소드
     */
    public void updateStatus(Status status) {
        this.status = status;
    }

    /**
     * 댓글 수정 API 관련 메소드
     */
    public void updateContent(String content) {
        this.content = content;
    }
}

