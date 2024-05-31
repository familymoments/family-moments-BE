package com.spring.familymoments.domain.chat.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MessageRes {
    @NotNull
    private String messageId;
    private Long familyId;
    @NotNull
    private String sender;
    @NotNull
    private String message;
    @NotNull
    private LocalDateTime sendedTime;
}
