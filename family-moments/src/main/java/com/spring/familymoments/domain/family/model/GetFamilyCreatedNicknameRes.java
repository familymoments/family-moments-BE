package com.spring.familymoments.domain.family.model;

import com.spring.familymoments.domain.family.entity.Family;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메인 화면 관련 Response")
public class GetFamilyCreatedNicknameRes {
    @Schema(description = "유저 닉네임" , example = "몰리")
    private String nickname;
    @Schema(description = "가족 생성일 디데이" , example = "10")
    private String dday;

}
