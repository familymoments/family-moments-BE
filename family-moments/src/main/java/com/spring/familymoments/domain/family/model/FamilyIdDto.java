package com.spring.familymoments.domain.family.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "가족 관련 dto - 가족 아이디 포함")
public class FamilyIdDto {

    @Schema(description = "가족 id")
    private Long familyId;

    @Schema(description = "가족 권한자")
    private String owner;

    @Schema(description = "가족 이름")
    private String familyName;

    @Schema(description = "업로드 주기")
    private int uploadCycle;

    @Schema(description = "초대 코드", example = "http://www.dadaffd.com")
    private String inviteCode;

    @Schema(description = "대표 이미지")
    private String representImg;

}
