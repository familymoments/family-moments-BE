package com.spring.familymoments.domain.family;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.family.model.FamilyDto;
import com.spring.familymoments.domain.family.model.PostFamilyReq;
import com.spring.familymoments.domain.family.model.PostFamilyRes;
import com.spring.familymoments.domain.user.UserService;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;

import static com.spring.familymoments.config.BaseResponseStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/families")
public class FamilyController {

    private final FamilyService familyService;
    private final UserService userService;

//    @ResponseBody
//    @PostMapping("/{userId}")
//    public BaseResponse<PostFamilyRes> createFamily(@PathVariable Long userId, @RequestBody PostFamilyReq postFamilyReq) throws BaseException {
////        String uuid = jwtService.resolveToken();
//        System.out.println("여기는 가족 post");
//        User owner = userService.getUserMinjeong(userId);
//
//        PostFamilyRes postFamilyRes = familyService.createFamily(owner, postFamilyReq);
//        return new BaseResponse<>(postFamilyRes);
//    }

    /**
     * 가족 정보 조회 API
     * [GET] /familyId
     * @return BaseResponse<FamilyDto>
     */
    @ResponseBody
    @GetMapping("/{familyId}")
    public BaseResponse<FamilyDto> getFamily(@PathVariable Long familyId) throws BaseException{
        //return new BaseResponse<>(familyService.getFamily(familyId));
        try {
            FamilyDto familyDto = familyService.getFamily(familyId);
            return new BaseResponse<>(familyDto);
        } catch (NoSuchElementException e) {
            return new BaseResponse<>(FIND_FAIL_FAMILY);
        }
    }

    /**
     * 초대코드로 가족 정보 조회 API
     * [GET] /{inviteCode}/inviteCode
     * @return BaseResponse<FamilyDto>
     */
    @GetMapping("/{inviteCode}/inviteCode")
    public BaseResponse<FamilyDto> getFamilyByInviteCode(@PathVariable String inviteCode) throws BaseException{
        //return new BaseResponse<>(familyService.getFamily(familyId));
        try {
            FamilyDto familyDto = familyService.getFamilyByInviteCode(inviteCode);
            return new BaseResponse<>(familyDto);
        } catch (NoSuchElementException e) {
            return new BaseResponse<>(FIND_FAIL_FAMILY);
        }
    }

    /**
     * 초대 API
     * [GET] /familyId
     * @return BaseResponse<String>
     */
    @PostMapping("/{familyId}")
    public BaseResponse<String> inviteUser(@PathVariable Long familyId,
                                           @RequestParam List<Long> userIds) throws BaseException{
        try {
            familyService.inviteUser(userIds, familyId);
            return new BaseResponse<>("초대 요청이 완료되었습니다.");
        } catch (IllegalAccessException e) {
            return new BaseResponse<>(false, e.getMessage(), HttpStatus.CONFLICT.value());
        } catch (NoSuchElementException e){
            return new BaseResponse<>(FIND_FAIL_USERNAME);
        }
    }

    /**
     * 초대 승락 API
     * [GET] /{familyId}/users/{userId}/invite-accept
     * @return BaseResponse<String>
     */
    @PatchMapping("/{familyId}/users/{userId}/invite-accept")
    public BaseResponse<String> acceptFamily(@PathVariable Long familyId,
                                             @PathVariable Long userId) throws BaseException{
        try {
            familyService.acceptFamily(userId, familyId);
            return new BaseResponse<>("초대가 수락되었습니다.");
        }catch (NoSuchElementException e){
            return new BaseResponse<>(false, e.getMessage(), HttpStatus.NOT_FOUND.value());
        }
    }
}
