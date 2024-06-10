package com.spring.familymoments.domain.socialInfo;

import com.spring.familymoments.domain.socialInfo.entity.SocialInfo;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialUserRepository extends JpaRepository<SocialInfo, Long> {
    //rest api/sdk version
    @Query("SELECT u FROM User u LEFT JOIN SocialInfo si ON u = si.user WHERE u.email = :email AND si.type = :userType")
    Optional<User> findUserByEmailAndUserType(@Param("email") String email, @Param("userType") UserType userType);

    List<SocialInfo> findSocialInfoByUser(User user);
}
