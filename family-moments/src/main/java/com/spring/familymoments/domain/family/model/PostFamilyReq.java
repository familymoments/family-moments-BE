package com.spring.familymoments.domain.family.model;

import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostFamilyReq {
    @NotBlank(message = "가족 이름을 입력해주세요.")
    private String familyName;
    @NotBlank(message = "알림 주기를 설정해주세요.")
    private int uploadCycle;
    @NotBlank(message = "대표 이미지를 설정해주세요.")
    private String representImg;

    public Family toEntity() {
        return Family.builder()
                .familyName(this.familyName)
                .uploadCycle(this.uploadCycle)
                .representImg(this.representImg)
                .build();
    }
}
