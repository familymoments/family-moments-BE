package com.spring.familymoments.domain.post;

import com.spring.familymoments.domain.post.document.PostDocument;
import com.spring.familymoments.domain.post.model.SinglePostDocumentRes;
import org.bson.types.ObjectId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostDocumentRepository extends MongoRepository<PostDocument, ObjectId> {

    @Query("SELECT pd FROM PostDocument pd INNER JOIN Post p ON pd.entityId = p.postId WHERE p.postId = :postId")
    SinglePostDocumentRes findByEntityId(@Param("entityId") Long postId);

    @Query("SELECT pd FROM PostDocument pd WHERE pd.entityId = :postId")
    Optional<PostDocument> findPostDocumentByEntityId(@Param("entityId") Long postId);

}
