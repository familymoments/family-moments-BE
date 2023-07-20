package com.spring.familymoments.domain.user;

import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUuid(String uuid);

    // Optional<User> findByEmailAndStatus(String email, BaseEntity.Status status);
    Optional<User> findByEmail(String email);
}
