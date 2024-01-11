package com.spring.familymoments.domain.family.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "가족 생성 관련 Response")
public class PostFamilyRes {
    @Schema(description = "가족 인덱스", example = "1")
    private Long familyId;
    @Schema(description = "가족 생성자 닉네임", example = "molly")
    private String ownerNickName;
    @Schema(description = "가족 초대 코드", example = "http://www.dadaffd.com")
    private String inviteCode;
}
