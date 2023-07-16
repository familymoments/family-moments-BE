package com.spring.familymoments.domain.commentLove.entity;

import com.spring.familymoments.domain.comment.entity.Comment;
import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.user.entity.User;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "CommentLove")
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class CommentLove extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentLoveId", nullable = false, updatable = false)
    private Long commentLoveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="commentId", nullable = false)
    private Comment commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId", nullable = false)
    private User userId;
}
