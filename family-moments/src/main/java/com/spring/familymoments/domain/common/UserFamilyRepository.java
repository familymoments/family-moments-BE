package com.spring.familymoments.domain.common;

import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.family.model.GetFamilyAllResInterface;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserFamilyRepository extends JpaRepository<UserFamily, Long> {

    @Query("SELECT uf FROM UserFamily uf WHERE uf.status = 'ACTIVE' AND uf.userId.id = :userId ORDER BY uf.createdAt ASC")
    List<UserFamily> findFirstActiveUserFamilyByUserId(@Param("userId") String userId, Pageable pageable);

    Optional<UserFamily> findByUserIdAndFamilyId(User userId, Family familyId);

    @Query(value = "SELECT uf FROM UserFamily uf WHERE uf.userId = ?1 AND uf.status = 'DEACCEPT'"
            + "ORDER BY uf.createdAt DESC")
    List<UserFamily> findAllByUserIdOrderByCreatedAtDesc(User userId);

    //회원 탈퇴 시, UserFamily 매핑 테이블 해제를 위한 조회
    @Query("SELECT uf FROM UserFamily uf WHERE uf.userId.userId = :userId")
    List<UserFamily> findUserFamilyByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT u.userId AS userId, u.id AS id, u.nickname AS nickname, u.profileImg AS profileImg " +
            "FROM User u " +
            "INNER JOIN UserFamilyMapping m ON u.userId = m.userId " +
            "INNER JOIN Family f ON m.familyId = f.familyId " +
            "WHERE m.status = 'ACTIVE' " +
            "AND f.familyId = :familyId",
            nativeQuery = true)
    List<GetFamilyAllResInterface> findActiveUsersByFamilyId(@Param("familyId") Long familyId);
  
    boolean existsByUserIdAndFamilyId(User userId, Family familyId);

    @Query("SELECT f.familyName FROM UserFamily uf JOIN Family f ON uf.familyId.familyId = f.familyId WHERE uf.userId.userId = :userId")
    String findFamilyNameByUserId(@Param("userId") Long userId);
  
    @Query("SELECT uf FROM UserFamily uf " +
            "WHERE uf.userId = :user " +
            "AND uf.status = 'ACTIVE'")
    List<UserFamily> findAllActiveUserFamilyByUser(@Param("user") User user);

    @Query("SELECT uf.userId FROM UserFamily uf " +
            "WHERE uf.familyId.familyId = :familyId " +
            "AND uf.userId.uuid = :uuid " +
            "AND uf.status = 'ACTIVE'" )
    Optional<User> findActiveUserByFamilyIdAndUuid(@Param("familyId") Long familyId, @Param("uuid") String uuid);

    @Query("SELECT uf FROM UserFamily uf " +
            "WHERE uf.familyId = :family " +
            "AND uf.userId = :user " +
            "AND uf.status = 'ACTIVE'")
    Optional<UserFamily> findActiveUserFamilyByFamilyAndUser(@Param("family") Family family, @Param("user") User user);
}
