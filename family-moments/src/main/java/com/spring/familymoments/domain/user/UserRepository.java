package com.spring.familymoments.domain.user;

import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUuid(String uuid);

    // Optional<User> findByEmailAndStatus(String email, BaseEntity.Status status);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    Optional<User> findByPassword(String password);
    boolean existsById(String id);
    boolean existsByEmail(String email);
}
