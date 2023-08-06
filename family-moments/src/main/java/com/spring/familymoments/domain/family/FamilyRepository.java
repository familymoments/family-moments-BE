package com.spring.familymoments.domain.family;

import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FamilyRepository extends JpaRepository<Family, Long> {
    Optional<Family> findById(Long familyId);

    Optional<Family> findByInviteCode(String inviteCode);

    //회원 탈퇴 시, 가족 생성자 권한 여부를 확인하기 위한 조회
    List<Family> findByOwner(User user);
}
