package com.spring.familymoments.domain.family;

import com.spring.familymoments.domain.family.entity.Family;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamilyRepository extends JpaRepository<Family, Long> {
    Optional<Family> findById(Long aLong);

    Optional<Family> findByInviteCode(String inviteCode);

}
