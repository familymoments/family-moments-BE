package com.spring.familymoments.domain.socialInfo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KaKaoDeleteDto {
    private String target_id_type;
    private Long target_id;
}
