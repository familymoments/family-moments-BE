package com.spring.familymoments.domain.chat.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Setter
public class MessageTemplate {
    @NotNull
    private MessageType type;
    @NotNull
    private Object contents;

    public enum MessageType {
        MESSAGE, NOTICE;
    }
}
