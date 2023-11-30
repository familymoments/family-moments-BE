package com.spring.familymoments.domain.common;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class MongoBaseTime {

    @CreationTimestamp
    @Field("createdAt")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Field("updatedAt")
    private LocalDateTime updatedAt;
}
