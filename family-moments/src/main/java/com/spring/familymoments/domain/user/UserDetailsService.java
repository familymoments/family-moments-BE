package com.spring.familymoments.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    /**
     * springsecurity에서 유저를 찾는 메소드를 제공하는 UserDetailsService를 implements
     * loadUserByUsername 메소드를 오버라이딩 하여 유저를 찾는 방법을 직접 지정함.
     * 이 메소드의 username == '사용자 uuid'
     *
     */
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUuid(username)
                .orElseThrow(() -> new UsernameNotFoundException("[UserDetailService] 사용자를 찾을 수 없습니다."));
    }

}
