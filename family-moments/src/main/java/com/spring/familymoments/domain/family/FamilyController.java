package com.spring.familymoments.domain.family;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.family.model.PostFamilyReq;
import com.spring.familymoments.domain.family.model.PostFamilyRes;
import com.spring.familymoments.domain.user.UserService;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/families")
public class FamilyController {

    private final FamilyService familyService;
    private final UserService userService;

    @ResponseBody
    @PostMapping("/{userId}")
    public BaseResponse<PostFamilyRes> createFamily(@PathVariable Long userId, @Validated @RequestBody PostFamilyReq postFamilyReq) throws BaseException {
//        String uuid = jwtService.resolveToken();
        User owner = userService.getUser(userId);

        PostFamilyRes postFamilyRes = familyService.createFamily(owner, postFamilyReq);
        return new BaseResponse<>(postFamilyRes);
    }
}
