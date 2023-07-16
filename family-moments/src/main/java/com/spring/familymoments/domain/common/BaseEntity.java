package com.spring.familymoments.domain.common;

import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity extends BaseTime{
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    protected Status status = Status.ACTIVE;

    public enum Status {
        ACTIVE, INACTIVE;
    }
}
