package com.spring.familymoments.domain.fcm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UploadaAlramDto {
    private String id;
    private String nickname;
    private String familyName;
    private MessageTemplate template;
}
