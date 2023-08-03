package com.spring.familymoments.domain.user;

import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUuid(String uuid);

    // Optional<User> findByEmailAndStatus(String email, BaseEntity.Status status);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    Optional<User> findByPassword(String password);

    boolean existsById(String id);
    boolean existsByName(String name);
    boolean existsByEmail(String email);
    boolean existsByNameAndEmail(String name, String email);

    //유저 검색
    @Query("SELECT u FROM User u WHERE u.id LIKE :keyword% ORDER BY u.id ASC")
    Page<User> findTop5ByIdContainingKeywordOrderByIdAsc(String keyword, Pageable pageable);
    @Query("SELECT u, uf FROM User u LEFT JOIN UserFamily uf ON u.userId = uf.userId.userId " +
            "WHERE (uf.familyId.familyId = :familyId) AND (u.userId = :userId)")
    List<Object[]> findUsersByFamilyIdAndUserId(Long familyId, Long userId);

    User findByNickname(String nickname);
}