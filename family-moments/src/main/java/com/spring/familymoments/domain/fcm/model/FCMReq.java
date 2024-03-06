package com.spring.familymoments.domain.fcm.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FCMReq {
//    private Long targetUserld;
    private String title;
    private String body;

    @Builder
//    public FCMReq(Long targetUserld, String title, String body) {
    public FCMReq(String title, String body) {
//        this.targetUserld = targetUserld;
        this.title = title;
        this.body = body;
    }
}
