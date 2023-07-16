package com.spring.familymoments.domain.postLove.entity;

import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.post.entity.Post;
import com.spring.familymoments.domain.user.entity.User;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "PostLove")
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class PostLove extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postLoveId", nullable = false, updatable = false)
    private Long postLoveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="postId", nullable = false)
    private Post postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId", nullable = false)
    private User userId;
}
