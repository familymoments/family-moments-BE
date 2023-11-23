package com.spring.familymoments.domain.post;

import com.spring.familymoments.domain.post.document.PostDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostDocumentRepository extends MongoRepository<PostDocument, ObjectId> {
}
