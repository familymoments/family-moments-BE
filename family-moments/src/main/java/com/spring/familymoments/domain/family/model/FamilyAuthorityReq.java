package com.spring.familymoments.domain.family.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "가족 권한 수정")
public class FamilyAuthorityReq {
    @NonNull
    private String userId;
}
