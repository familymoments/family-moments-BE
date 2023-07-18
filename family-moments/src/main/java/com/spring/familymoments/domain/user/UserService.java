package com.spring.familymoments.domain.user;

import com.spring.familymoments.domain.user.model.PostUserReq;
import com.spring.familymoments.domain.user.model.PostUserRes;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public PostUserRes createUser(PostUserReq postUserReq) {
        // TODO: 비밀번호 암호화
        User saveUser = userRepository.save(postUserReq.toEntity());
        //return new PostUserRes(saveUser.getUserId());
        return new PostUserRes(saveUser.getId(), saveUser.getPassword());
    }
}
