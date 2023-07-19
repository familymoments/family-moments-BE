package com.spring.familymoments.domain.family.entity;

import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.user.entity.User;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "Family")
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class Family extends BaseEntity {

    @Id
    @Column(name = "familyId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long familyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", nullable = false)
    private User owner;

    @Column(nullable = false, length = 20)
    private String familyName;

    @Column(columnDefinition = "int unsigned")
    private String uploadCycle;

    @Column(nullable = false, length = 10)
    private String inviteCode;

    @Column(columnDefinition = "TEXT")
    private String representImg;
}

