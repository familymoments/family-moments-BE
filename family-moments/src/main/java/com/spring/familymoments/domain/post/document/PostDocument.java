package com.spring.familymoments.domain.post.document;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.List;

@Document(collection = "post")
@Getter
@AllArgsConstructor
@Builder
public class PostDocument {
    @Id
    private ObjectId docId;
    private Long entityId;
    private String content;
    private List<String> urls;

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public void updateUrls(List<String> newUrls) {
        this.urls = newUrls;
    }

}
