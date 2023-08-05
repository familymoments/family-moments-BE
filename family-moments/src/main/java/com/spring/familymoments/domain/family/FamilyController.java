package com.spring.familymoments.domain.family;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.secret.jwt.JwtService;
import com.spring.familymoments.domain.awsS3.AwsS3Service;
import com.spring.familymoments.domain.family.model.*;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PostMapping("/family")
    public BaseResponse<PostFamilyRes> createFamily(@AuthenticationPrincipal User user,
                                                    @RequestParam(name = "representImg") MultipartFile representImg,
                                                    @RequestPart PostFamilyReq postFamilyReq) {
        try{
//        int owner = jwtService.getUserIdx();
            // 대표 이미지 넣기
            String fileUrl = awsS3Service.uploadImage(representImg);
//            postFamilyReq.setRepresentImg(fileUrl);             // 이미지 파일 객체에 추가

            PostFamilyRes postFamilyRes = familyService.createFamily(user, postFamilyReq, fileUrl);
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
     * [GET] /:familyId/created
     * @return BaseResponse<FamilyDto>
     */
    @ResponseBody
    @GetMapping("/{familyId}/created")
    public BaseResponse<GetFamilyCreatedNicknameRes> getFamilyCreatedNickname(@AuthenticationPrincipal User user,
                                                                              @PathVariable Long familyId){
        try {
            GetFamilyCreatedNicknameRes getFamilyCreatedNicknameRes = familyService.getFamilyCreatedNickname(user, familyId);
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
    @PostMapping("/inviteCode")
    public BaseResponse<FamilyDto> getFamilyByInviteCode(@RequestBody String inviteCode){
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
                                           @RequestParam List<String> userIds,
                                           @AuthenticationPrincipal User user) throws BaseException{
        try {
            familyService.inviteUser(user, userIds, familyId);
            return new BaseResponse<>("초대 요청이 완료되었습니다.");
        } catch (IllegalAccessException e) {
            return new BaseResponse<>(false, e.getMessage(), HttpStatus.CONFLICT.value());
        } catch (NoSuchElementException e){
            return new BaseResponse<>(FIND_FAIL_USERNAME);
        }
    }

    /**
     * 초대 승락 API
     * [GET] /{familyId}/invite-accept
     * @return BaseResponse<String>
     */
    @PatchMapping("/{familyId}/invite-accept")
    public BaseResponse<String> acceptFamily(@PathVariable Long familyId,
                                             @AuthenticationPrincipal User user) throws BaseException{
        try {
            familyService.acceptFamily(user, familyId);
            return new BaseResponse<>("초대가 수락되었습니다.");
        }catch (NoSuchElementException e){
            return new BaseResponse<>(false, e.getMessage(), HttpStatus.NOT_FOUND.value());
        }
    }

    /**
     * 업로드 주기 수정 API
     * [PATCH] /:familyId?uploadCycle={업로드주기}
     * @return BaseResponse<String>
     */
    @PatchMapping("/{familyId}")
    public BaseResponse<String> updateUploadCycle(@AuthenticationPrincipal User user,
                                                  @PathVariable Long familyId,
                                                  @RequestParam("uploadCycle") int uploadCycle){
        try {
            familyService.updateUploadCycle(user, familyId, uploadCycle);
            return new BaseResponse<>("업로드 주기가 수정되었습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }

    /**
     * 가족 삭제 API
     * [DELETE] /:familyId
     * @return BaseResponse<String>
     */
    @DeleteMapping("/{familyId}")
    public BaseResponse<String> deleteFamily(@AuthenticationPrincipal User user, @PathVariable Long familyId) {
        try {
            familyService.deleteFamily(user, familyId);
            return new BaseResponse<>("가족이 삭제되었습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }

    /** 가족 정보수정 API
     * [GET] /families/{familyId}
     * @return BaseResponse<FamilyDto>
     */
    @PatchMapping("/{familyId}/update")
    public BaseResponse<FamilyDto> updateFamily(@PathVariable Long familyId,
                                                @AuthenticationPrincipal User user,
                                                @RequestBody FamilyUpdateDto familyUpdateDto){
        try {
            FamilyDto resFamilyDto = familyService.updateFamily(user, familyId, familyUpdateDto);
            return new BaseResponse<>(resFamilyDto);
        } catch (NoSuchElementException e) {
            return new BaseResponse<>(FIND_FAIL_USERNAME);
        }catch (IllegalAccessException e){
            return new BaseResponse<>(FAILED_USERSS_UNATHORIZED);
        }
    }

    /** 가족 탈퇴 API
     * [DELETE] /families/{familyId}/withdraw
     * @return BaseResponse<String>
     */
    @DeleteMapping("/{familyId}/withdraw")
    public BaseResponse<String> withdrawFamily(@PathVariable Long familyId,
                                               @AuthenticationPrincipal User user){
        try {
            familyService.withdrawFamily(user, familyId);
            return new BaseResponse<>("가족에서 탈퇴되었습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }

    /** 가족 강제 탈퇴 API
     * [DELETE] /families/{familyId}/emission
     * @return BaseResponse<String>
     */
    @DeleteMapping("/{familyId}/emission")
    public BaseResponse<String> emissionFamily(@PathVariable Long familyId,
                                               @AuthenticationPrincipal User user,
                                               @RequestParam List<String> userIds){
        try {
            familyService.emissionFamily(user, familyId, userIds);
            return new BaseResponse<>("가족에서 탈퇴되었습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>((e.getStatus()));
        }
    }
}