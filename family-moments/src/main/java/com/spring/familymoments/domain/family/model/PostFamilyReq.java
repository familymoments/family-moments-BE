package com.spring.familymoments.domain.family.model;

import com.spring.familymoments.domain.family.entity.Family;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "가족 생성 관련 Request")
public class PostFamilyReq {
    @NotBlank(message = "가족 이름을 입력해주세요.")
    @Schema(description = "가족 이름" , example = "FamilyMoments")
    private String familyName;
    @NotBlank(message = "알림 주기를 설정해주세요.")
    @Schema(description = "업로드 사이클" , example = "1")
    private int uploadCycle;


    public Family toEntity() {
        return Family.builder()
                .familyName(this.familyName)
                .uploadCycle(this.uploadCycle)
                .build();
    }
}
