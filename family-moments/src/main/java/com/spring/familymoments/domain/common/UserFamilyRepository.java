package com.spring.familymoments.domain.common;

import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserFamilyRepository extends JpaRepository<UserFamily, Long> {

    Optional<UserFamily> findByUserId(Optional<User> user);

    List<UserFamily> findUserFamilyByUserId(Optional<User> user);

    Optional<UserFamily> findByUserIdAndFamilyId(User userId, Family familyId);

    @Query(value = "SELECT uf FROM UserFamily uf WHERE uf.userId = ?1 "
            + "ORDER BY uf.createdAt DESC")
    List<UserFamily> findAllByUserIdOrderByCreatedAtDesc(User userId);

    //회원 탈퇴 시, UserFamily 매핑 테이블 해제를 위한 조회
    @Query("SELECT uf FROM UserFamily uf WHERE uf.userId.userId = :userId")
    List<UserFamily> findUserFamilyByUserId(@Param("userId") Long userId);

    @Query("SELECT u FROM User u " +
            "INNER JOIN UserFamily m ON u.userId = m.userId.userId " +
            "INNER JOIN Family f ON m.familyId.familyId = f.familyId " +
            "WHERE m.status = 'ACTIVE' " +
            "AND f.familyId = :familyId")
    List<User> findActiveUsersByFamilyId(@Param("familyId") Long familyId);
}
