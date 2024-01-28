package com.spring.familymoments.domain.socialInfo;

import com.spring.familymoments.domain.socialInfo.entity.SocialInfo;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialUserRepository extends JpaRepository<SocialInfo, Long> {
    @Query("SELECT u FROM User u LEFT JOIN SocialInfo si ON u = si.user WHERE u.email = :email AND si.type = :userType")
    User findUserByEmailAndUserType(@Param("email") String email, @Param("userType") UserType userType);
}
