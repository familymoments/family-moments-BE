package com.spring.familymoments.domain.family.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.user.entity.User;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.*;
import javax.validation.constraints.*;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner", nullable = false)
    private User owner;

    @Column(nullable = false, length = 20)
    private String familyName;

    @Column(columnDefinition = "int unsigned")
    private int uploadCycle;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String inviteCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String representImg;

    public Family(Long familyId) {
        this.familyId = familyId;
    }

    /**
     * 가족 삭제 API 관련 메소드
     */
    public void updateStatus(Status status) {
        this.status = status;
    }

    /**
     * 가족 업로드 주기 수정 API 관련 메소드
     */
    public void updateUploadCycle(int uploadCycle) {
        this.uploadCycle = uploadCycle;
    }

    /**
     * 가족 정보 수정 API 관련 메소드
     */
    public void updateFamily(User owner, String familyName) {
        this.owner = owner;
        this.familyName = familyName;
    }

    /**
     * 가족 정보 수정 API 관련 메소드
     */
    public void updateFamily(User owner) {
        this.owner = owner;
    }
}

