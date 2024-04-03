package com.spring.familymoments.domain.fcm.entity;

import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "AlarmSetting")
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class AlarmSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarmSettingId", nullable = false, updatable = false)
    private Long alarmSettingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "alarmType", nullable = false, length = 10)
    private AlarmSetting.AlarmType alarmType;

    public enum AlarmType {
        CYCLE, POSTING, CHAT;
    }

}
