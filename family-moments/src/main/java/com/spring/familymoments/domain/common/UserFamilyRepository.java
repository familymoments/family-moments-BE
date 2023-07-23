package com.spring.familymoments.domain.common;

import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserFamilyRepository extends JpaRepository<UserFamily, Long> {

    Optional<UserFamily> findByUserId(Optional<User> user);
    Optional<UserFamily> findByUserIdAndFamilyId(User userId, Family familyId);
}
