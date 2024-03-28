package com.spring.familymoments.domain.chat.entity;

import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.user.entity.User;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ChatInfo")
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class ChatInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private long chatInfoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "familyId", nullable = false)
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime lastAccessedTime = LocalDateTime.now();

    public void updateLastAccessedTime(LocalDateTime newTime) {
        this.lastAccessedTime = newTime;
    }
}
