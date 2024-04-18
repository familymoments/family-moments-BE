package com.spring.familymoments.domain.socialInfo;

import com.spring.familymoments.domain.socialInfo.entity.SocialInfo;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocialUserRepository extends JpaRepository<SocialInfo, Long> {
    //rest api version
    @Query("SELECT u FROM User u LEFT JOIN SocialInfo si ON u = si.user WHERE u.email = :email AND si.type = :userType")
    User findUserByEmailAndUserType(@Param("email") String email, @Param("userType") UserType userType);

    //sdk version
    @Query("SELECT u FROM User u LEFT JOIN SocialInfo si ON u= si.user WHERE u.email = :email AND si.snsUserId = :snsId")
    User findUserByEmailAndSnsId(@Param("email") String email, @Param("snsId") String snsId);

    List<SocialInfo> findSocialInfoByUser(User user);
}
