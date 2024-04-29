package com.spring.familymoments.domain.family;

import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.family.model.GetFamilyCreatedNicknameRes;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FamilyRepository extends JpaRepository<Family, Long> {
    Optional<Family> findById(Long familyId);

    Optional<Family> findByInviteCode(String inviteCode);

    //회원 탈퇴 시, 가족 생성자 권한 여부를 확인하기 위한 조회
    List<Family> findByOwner(User user);

    @Query("SELECT CASE WHEN COUNT(uf) = 0 THEN false ELSE true END " +
            "FROM UserFamily uf " +
            "WHERE (uf.familyId = :family) AND (uf.userId = :user)")
    Boolean isFamilyMember(@Param("family") Family family, @Param("user") User user);

    @Query("SELECT f FROM Family f JOIN f.userFamilies uf WHERE uf.userId = :user AND uf.status = 'ACTIVE' ORDER BY uf.createdAt ASC")
    List<Family> findActiveFamilyByUserId(@Param("user") User user);

    @Query(value = "SELECT DATEDIFF(:today, f.createdAt)  " +
            "FROM Family f " +
            "WHERE f.familyId = :familyId " +
            "AND f.status = 'ACTIVE' ",
            nativeQuery = true)
    String findCreatedAtNicknameById(@Param("familyId") Long familyId, @Param("today") LocalDateTime today);

    @Query(value = "SELECT u.id, u.nickname, f.familyName  " +
            "FROM Family f " +
            "INNER JOIN UserFamilyMapping m ON f.familyId = m.familyId " +
            "INNER JOIN User u ON m.userId = u.userId " +
            "INNER JOIN AlarmSetting a ON u.userId = a.userId " +
            "WHERE f.status = 'ACTIVE' " +
            "AND u.status = 'ACTIVE' " +
            "AND a.alarmType = 'CYCLE' " +
            "AND a.status = 'ACTIVE' " +
            "AND DATE_ADD(f.latestUploadAt, INTERVAL f.uploadCycle DAY) <= :currentDate",
            nativeQuery = true)
    List<Map<String, Object>> findFamiliesWithUploadCycle(@Param("currentDate") LocalDateTime currentDate);

}
