package com.spring.familymoments.domain.common.entity;

import com.spring.familymoments.domain.common.BaseTime;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.user.entity.User;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "UserFamilyMapping")
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class UserFamily extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mappingId" , nullable = false, updatable = false)
    private Long mappingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "familyId", nullable = false)
    private Family familyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    protected Status status = Status.DEACCEPT;

    private enum Status {
        ACTIVE, INACTIVE, DEACCEPT, REJECT;
    }

}
