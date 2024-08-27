package com.spring.familymoments.domain.user;

import com.spring.familymoments.domain.common.BaseEntity;
import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.userId = :userId " +
            "AND u.status = 'ACTIVE' ")
    Optional<User> findUserByUserId(Long userId);

    @Query("SELECT u FROM User u WHERE u.uuid = :uuid " +
            "AND u.status = 'ACTIVE' ")
    Optional<User> findUserByUuid(String uuid);

    // Optional<User> findByEmailAndStatus(String email, BaseEntity.Status status);
    @Query("SELECT u FROM User u WHERE u.id = :id " +
            "AND u.status = 'ACTIVE' ")
    Optional<User> findById(@Param("id") String id);
//    Optional<User> findByEmail(String email);

//    boolean existsById(String id);
//    boolean existsByEmail(String email);
//    boolean existsByIdAndStatus(String id, User.Status Status);

    @Query("SELECT u FROM User u WHERE u.email = :email " +
            "AND u.status = 'ACTIVE' ")
    Optional<User> findByEmail(@Param("email") String email);

    //유저 검색
    @Query("SELECT u FROM User u WHERE u.id LIKE :keyword% AND u.status = 'ACTIVE' ORDER BY u.id ASC ")
    List<User> searchUserByKeyword(String keyword);
    @Query("SELECT u, uf FROM User u LEFT JOIN UserFamily uf ON u.userId = uf.userId.userId " +
            "WHERE (uf.familyId.familyId = :familyId) AND (u.userId = :userId) AND u.status = 'ACTIVE' AND uf.status = 'ACTIVE' ")
    List<Object[]> findUsersByFamilyIdAndUserId(Long familyId, Long userId);

    User findByNickname(String nickname);
}