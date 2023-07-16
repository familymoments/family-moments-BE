package com.spring.familymoments.domain.post.entity;

import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.common.BaseTime;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.user.entity.User;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "Post")
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postId", nullable = false, updatable = false)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User writerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "familyId", nullable = false)
    private Family familyId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String img1;

    @Column(columnDefinition = "TEXT")
    private String img2;

    @Column(columnDefinition = "TEXT")
    private String img3;

    @Column(columnDefinition = "TEXT")
    private String img4;

    @Column(columnDefinition = "int unsigned")
    @ColumnDefault("0")
    private int reported;

    @Column(columnDefinition = "int unsigned")
    @ColumnDefault("0")
    private int countLove;

}
