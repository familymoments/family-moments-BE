package com.spring.familymoments.domain.family.model;

import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostFamilyRes {
    private Long familyId;
    private String ownerNickName;
    private String inviteCode;
}
