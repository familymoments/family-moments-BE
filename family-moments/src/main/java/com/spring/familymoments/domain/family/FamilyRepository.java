package com.spring.familymoments.domain.family;

import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.fcm.model.FamilyLatestPostDto;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

//    @Query("SELECT f " +
//            "FROM Family f " +
//            "LEFT JOIN Post p ON f.familyId = p.familyId.familyId " +
//            "GROUP BY f.familyId " +
//            "HAVING MAX(p.createdAt) < DATE_ADD(CURRENT_DATE(), INTERVAL -f.uploadCycle DAY)")
//    List<Family> findFamiliesWithRecentPostsBeforeThresholdDate(@Param("today") LocalDate today);


    @Query("SELECT f.familyId as familyId, MAX(p.createdAt) as latestPostDate, f.uploadCycle as uploadCycle " +
            "FROM Family f " +
            "LEFT JOIN Post p ON f.familyId = p.familyId.familyId " +
            "GROUP BY f.familyId")
    List<Map<String, Object>> findLatestPostDate();

}
