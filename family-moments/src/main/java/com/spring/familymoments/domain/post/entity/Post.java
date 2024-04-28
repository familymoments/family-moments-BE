package com.spring.familymoments.domain.post.entity;

import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.post.model.SinglePostDocumentRes;
import com.spring.familymoments.domain.post.model.SinglePostRes;
import com.spring.familymoments.domain.user.entity.User;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "Post")
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postId", nullable = false, updatable = false)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "familyId", nullable = false)
    private Family familyId;

    @Column(columnDefinition = "int unsigned")
    @ColumnDefault("0")
    private int reported;

    @Column(columnDefinition = "int unsigned")
    @ColumnDefault("0")
    private int countLove;

    public void increaseCountLove(int countLove) {
        this.countLove = countLove + 1;
    }

    public void decreaseCountLove(int countLove) {
        this.countLove = countLove - 1;
    }

    public void delete() {
        this.status = Status.INACTIVE;
    }
    /**
     * 가족 삭제 API 관련 메소드
     */
    public void updateStatus(Status status) {
        this.status = status;
    }

    public SinglePostRes toSinglePostRes(SinglePostDocumentRes singlePostDocumentRes, boolean isLoved){
        return SinglePostRes.builder()
                .postId(postId)
                .writer(writer.getNickname())
                .profileImg(writer.getProfileImg())
                .content(singlePostDocumentRes.getContent())
                .imgs(singlePostDocumentRes.getUrls())
                .createdAt(getCreatedAt().toLocalDate())
                .countLove(countLove)
                .loved(isLoved)
                .build();
    }
}
