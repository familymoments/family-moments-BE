package com.spring.familymoments.domain.chat;

import com.spring.familymoments.domain.chat.document.ChatDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatDocumentRepository extends MongoRepository<ChatDocument, ObjectId> {

}
