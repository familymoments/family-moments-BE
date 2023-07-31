package com.spring.familymoments.domain.family;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.secret.jwt.JwtService;
import com.spring.familymoments.domain.awsS3.AwsS3Service;
import com.spring.familymoments.domain.family.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

import static com.spring.familymoments.config.BaseResponseStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/families")
public class FamilyController {

    private final FamilyService familyService;
    private final JwtService jwtService;
    @Autowired
    private final AwsS3Service awsS3Service;


    /**
     * 가족 생성 API
     * [POST] /family/:familyId
     * @return BaseResponse<PostFamilyRes>
     */
    @ResponseBody
    @PostMapping("/family/{userId}")
    public BaseResponse<PostFamilyRes> createFamily(@PathVariable Long userId,
                                                    @RequestParam(name = "representImg") MultipartFile representImg,
                                                    @RequestPart PostFamilyReq postFamilyReq) {
        try{
//        int owner = jwtService.getUserIdx();
            // 대표 이미지 넣기
            String fileUrl = awsS3Service.uploadImage(representImg);
            postFamilyReq.setRepresentImg(fileUrl);             // 이미지 파일 객체에 추가

            PostFamilyRes postFamilyRes = familyService.createFamily(userId, postFamilyReq);
            return new BaseResponse<>(postFamilyRes);
        }catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }

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
     * 닉네임 및 가족 생성일 조회 API
     * [GET] /:familyId/created/:userId
     * @return BaseResponse<FamilyDto>
     */
    @ResponseBody
    @GetMapping("/{familyId}/created/{userId}")
    public BaseResponse<GetFamilyCreatedNicknameRes> getFamilyCreatedNickname(@PathVariable Long familyId,
                                                                              @PathVariable Long userId){
        try {
            GetFamilyCreatedNicknameRes getFamilyCreatedNicknameRes = familyService.getFamilyCreatedNickname(familyId, userId);
            return new BaseResponse<>(getFamilyCreatedNicknameRes);
        } catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }

    /**
     * 가족원 전체 조회 API
     * [GET] /:familyId/users
     * @return BaseResponse<FamilyDto>
     */
    @GetMapping("/{familyId}/users")
    public BaseResponse<List<GetFamilyAllRes>> getFamilyAll(@PathVariable Long familyId){
        try {
            List<GetFamilyAllRes> getFamilyAllRes = familyService.getFamilyAll(familyId);
            return new BaseResponse<>(getFamilyAllRes);
        } catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
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
    @PostMapping("/ab/{familyId}")
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

    /**
     * 업로드 주기 수정 API
     * [PATCH] /:familyId/:userId?uploadCycle={업로드주기}
     * @return BaseResponse<String>
     */
    @PatchMapping("/{familyId}/{userId}")
    public BaseResponse<String> updateUploadCycle(@PathVariable Long familyId,
                                                  @PathVariable Long userId,
                                                  @RequestParam("uploadCycle") int uploadCycle){
        try {
            familyService.updateUploadCycle(familyId, userId, uploadCycle);
            return new BaseResponse<>("업로드 주기가 수정되었습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }

    /**
     * 가족 삭제 API
     * [DELETE] /:familyId/:userId
     * @return BaseResponse<String>
     */
    @DeleteMapping("/{familyId}/{userId}")
    public BaseResponse<String> deleteFamily(@PathVariable Long familyId, @PathVariable Long userId) {
        try {
            familyService.deleteFamily(familyId, userId);
            return new BaseResponse<>("가족이 삭제되었습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }
}
