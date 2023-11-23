package com.spring.familymoments.domain.common;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

public class MongoBaseTime {
    @CreatedDate
    private LocalDateTime createdAT;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
