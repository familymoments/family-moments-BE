package com.spring.familymoments.domain.post.document;

import com.spring.familymoments.domain.common.MongoBaseTime;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "post")
@Getter
@AllArgsConstructor
@Builder
public class PostDocument extends MongoBaseTime {
    @Id
    private ObjectId docId;
    private Long entityId;
    private String content;
    private List<String> urls;
}
