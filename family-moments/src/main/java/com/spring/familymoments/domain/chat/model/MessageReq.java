package com.spring.familymoments.domain.chat.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MessageReq {
    @NotNull
    private String sender;
    @NotNull
    private String message;
}
