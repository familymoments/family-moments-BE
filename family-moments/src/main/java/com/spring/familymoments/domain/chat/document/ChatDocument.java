package com.spring.familymoments.domain.chat.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Document(collection = "chat")
public class ChatDocument {
    @Id
    private ObjectId id;
    private Long familyId;
    private String sender;
    private String message;
    private LocalDateTime sendedTime;
}
