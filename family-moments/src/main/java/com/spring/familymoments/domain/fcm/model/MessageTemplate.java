package com.spring.familymoments.domain.fcm.model;

public enum MessageTemplate {
    UPLOAD_ALARM("%s님! '%s' 가족에 사진을 업로드하는 날이에요~"),
    NEW_POSTING("%s님이 게시글을 업로드하였습니다. 지금 바로 확인하세요."),
    NEW_MESSAGE("%s님! 새로운 메시지가 도착했습니다.");

    private final String template;

    MessageTemplate(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }
}
