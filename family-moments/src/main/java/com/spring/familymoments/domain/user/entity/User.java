package com.spring.familymoments.domain.user.entity;

import com.spring.familymoments.domain.common.BaseTime;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "User")
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class User extends BaseTime implements UserDetails {

    //유저 인덱스
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false, updatable = false)
    private Long userId;

    // 아이디
    @Column(nullable = false, length = 45)
    private String id;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false, length = 45)
    private String email;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String password;

    @Column(nullable = false, updatable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false)
    private LocalDateTime birthDate;

    @Column(columnDefinition = "TEXT")
    private String profileImg;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    protected Status status = Status.ACTIVE;

    public enum Status {
        ACTIVE, INACTIVE, BLACK;
    }

    @Column(columnDefinition = "int unsigned")
    @ColumnDefault("0")
    private int reported;

    @Builder
    public User(String id, String uuid, String email, String password, String name, String nickname,
                LocalDateTime birthDate, String profileImg, Status status){
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.profileImg = profileImg;
        this.status = status;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return id; //uuid?
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

}
