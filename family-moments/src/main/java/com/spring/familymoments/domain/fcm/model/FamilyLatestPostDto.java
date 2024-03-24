package com.spring.familymoments.domain.fcm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FamilyLatestPostDto {
    private Long familyId;
    private LocalDateTime latestPostDate;
    private Integer uploadCycle;
}
