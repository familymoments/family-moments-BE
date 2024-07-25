package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.NoAuthCheck;
import com.spring.familymoments.config.secret.jwt.JwtService;
import com.spring.familymoments.domain.awsS3.AwsS3Service;
import com.spring.familymoments.domain.fcm.FCMService;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.validation.Valid;

import java.util.List;
import java.util.NoSuchElementException;

import static com.spring.familymoments.config.BaseResponseStatus.*;
import static com.spring.familymoments.utils.ValidationRegex.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "회원 API Document")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final AwsS3Service awsS3Service;
    private final AuthService authService;
    private final JwtService jwtService;
    private final FCMService fcmService;

    /**
     * 회원 가입 API
     * [POST] /users/sign-up
     * @param postUserReq 프로필 이미지 URL 저장
     * @param profileImage 프로필 이미지 원본 저장
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping(value = "/users/sign-up",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "회원 가입", description = "회원 가입에 사용되는 API 입니다.")
    public BaseResponse<String> createUser(@Parameter(description = "새로운 회원의 가입 정보")
                                                    @Valid @RequestPart("newUser") PostUserReq.joinUser postUserReq,
                                           @Parameter(description = "새로운 회원의 프로필 이미지")
                                                    @RequestPart("profileImg") MultipartFile profileImage) {
        //아이디
        if (!isRegexId(postUserReq.getId())) {
            return new BaseResponse<>(POST_USERS_INVALID_ID);
        }
        // TODO: 아이디 중복 체크
        if (userService.checkDuplicateIdByStatus(postUserReq.getId())) {
            // log.info("[createUser]: 이미 존재하는 아이디입니다!");
            return new BaseResponse<>(POST_USERS_EXISTS_ID);
        }
        //비밀번호
        if (!isRegexPw(postUserReq.getPassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_PW);
        }
        //이메일
        if (!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        // TODO: 이메일 중복 체크
        if (userService.checkDuplicateEmailByStatus(postUserReq.getEmail())) {
            // log.info("[createUser]: 이미 존재하는 이메일입니다!");
            return new BaseResponse<>(POST_USERS_EXISTS_EMAIL);
        }
        //생년월일
        if (!isRegexBirth(postUserReq.getStrBirthDate())) {
            return new BaseResponse<>(POST_USERS_INVALID_BIRTH);
        }
        //닉네임
        if (!isRegexNickName(postUserReq.getNickname())) {
            return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
        }

        String fileUrl = null;

        if (postUserReq.getProfileImg() == null) {
            fileUrl = awsS3Service.uploadImage(profileImage);
        }

        postUserReq.setProfileImg(fileUrl);

        PostUserRes postUserRes = userService.createUser(postUserReq, profileImage);
        // log.info("[createUser]: PostUserRes 생성 완료!");
        return new BaseResponse<>("회원가입을 성공했습니다.");
    }

    /**
     * 아이디 중복 확인 API
     * [GET] /users/check-id
     * @return BaseResponse<String>
     */
    @NoAuthCheck
    @PostMapping("/users/check-id")
    @Operation(summary = "아이디 중복 확인", description = "회원 가입 단계에서 아이디 중복 확인에 사용되는 API입니다.")
    public BaseResponse<String> checkDuplicateId(@Parameter(description = "회원 가입할 때 중복 검사를 할 아이디")
                                                     @Valid @RequestBody GetDuplicateUserIdReq getDuplicateUserIdReq) {

        if(!userService.checkDuplicateIdByStatus(getDuplicateUserIdReq.getId())) {
            return new BaseResponse<>("사용 가능한 아이디입니다.");
        } else {
            return new BaseResponse<>(POST_USERS_EXISTS_ID);
        }
    }

    /**
     * 이메일 중복 확인 API
     * [GET] /users/check-email
     * @return BaseResponse<String>
     */
    @NoAuthCheck
    @PostMapping("/users/check-email")
    @Operation(summary = "이메일 중복 확인", description = "회원 가입 단계에서 이메일 중복 확인에 사용되는 API입니다.")
    public BaseResponse<String> checkDuplicateEmail(@Parameter(description = "회원 가입할 때 중복 검사를 할 이메일")
                                                        @Valid @RequestBody GetDuplicateUserEmailReq getDuplicateUserEmailReq) {

        if(!userService.checkDuplicateEmailByStatus(getDuplicateUserEmailReq.getEmail())) {
            return new BaseResponse<>("사용 가능한 이메일입니다.");
        } else {
            return new BaseResponse<>(POST_USERS_EXISTS_EMAIL);
        }
    }
    /**
     * 아이디 찾기 API
     * [POST] /users/auth/find-id
     * @return BaseResponse<GetUserIdRes>
     */
    @NoAuthCheck
    @PostMapping("/users/auth/find-id")
    @Operation(summary = "아이디 찾기", description = "아이디 찾기에 사용되는 API이며, 중간에 이메일 인증 과정을 거칩니다.")
    public BaseResponse<GetUserIdRes> findUserId(@Parameter(description = "이메일 전송을 위해 아이디를 찾을 계정의 이름과 이메일을 입력")
                                                     @Valid @RequestBody PostEmailReq.sendVerificationEmail sendEmailReq)
            throws MessagingException {

        //이메일
        if(!isRegexEmail(sendEmailReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        try {
            if(emailService.checkVerificationCode(sendEmailReq)) {
                GetUserIdRes getUserIdRes = emailService.findUserId(sendEmailReq);
                return new BaseResponse<>(getUserIdRes);
            } else {
                return new BaseResponse<>(NOT_EQUAL_VERIFICATION_CODE);
            }
        } catch (NoSuchElementException e) {
            return new BaseResponse<>(false, e.getMessage(), HttpStatus.NOT_FOUND.value());
        }
    }
    /**
     * 비밀번호 찾기 - 아이디 존재 여부 확인
     * [POST] /users/auth/check-id
     * @return BaseResponse<String>
     */
    @NoAuthCheck
    @PostMapping("/users/auth/check-id")
    @Operation(summary = "비밀번호 찾기(아이디 존재 여부 확인)", description = "비밀번호 찾기 단계 중 아이디 존재 여부 확인 API입니다.")
    public BaseResponse<String> findUserIdBeforeUpdatePwd(@Parameter(description = "비밀번호를 찾을 계정의 아이디를 입력")
                                                              @Valid @RequestBody GetUserIdReq getUserIdReq)
            throws MessagingException, BaseException {

        if (userService.checkDuplicateIdByStatus(getUserIdReq.getUserId())) {
            // GetUserIdRes getUserIdRes = new GetUserIdRes(id);
            // return new BaseResponse<>(getUserIdRes);
            return new BaseResponse<>("입력하신 아이디로 가입을 확인했습니다. 본인 확인을 위하여 이메일로 인증해주세요.");
        } else {
            return new BaseResponse<>(false, FIND_FAIL_ID.getMessage(), HttpStatus.NOT_FOUND.value());
        }
    }
    /**
     * 비밀번호 찾기 API - 이메일 인증 확인
     * [POST] /users/auth/find-pwd
     * @return BaseResponse<String>
     */
    @NoAuthCheck
    @PostMapping("/users/auth/find-pwd")
    @Operation(summary = "비밀번호 찾기(이메일 인증 확인)", description = "비밀번호 찾기 단계 중 이메일 인증 API입니다.")
    public BaseResponse<String> findUserPwd(@Parameter(description = "이메일 전송을 위해 비밀번호를 찾을 계정의 이름과 이메일을 입력")
                                                @Valid @RequestBody PostEmailReq.sendVerificationEmail sendEmailReq)
            throws MessagingException {

        if(!isRegexEmail(sendEmailReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        try {
            if(emailService.checkVerificationCode(sendEmailReq)) {
                GetUserIdRes getUserIdRes = emailService.findUserId(sendEmailReq);
                return new BaseResponse<String>("이메일이 인증되었습니다. 새로운 비밀번호를 입력해주세요.");
            } else {
                return new BaseResponse<>(false, NOT_EQUAL_VERIFICATION_CODE.getMessage(), HttpStatus.BAD_REQUEST.value());
            }
        } catch (NoSuchElementException e) {
            return new BaseResponse<>(false, e.getMessage(), HttpStatus.NOT_FOUND.value());
        }
    }
    /**
     * 회원정보 조회 API
     * [GET] /users/profile
     * @param familyId null 가능
     * @return BaseResponse<GetProfileRes>
     */
    @GetMapping(value = "/users/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "회원 정보 조회", description = "회원 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = GetProfileRes.class)))
    })
    public BaseResponse<GetProfileRes> readProfile(@RequestParam(value = "familyId", required = false) Long familyId,
                                                   @AuthenticationPrincipal @Parameter(hidden = true) User user) {
        GetProfileRes getProfileRes = userService.readProfile(user, familyId);
        return new BaseResponse<>(getProfileRes);
    }

    /**
     * 유저 검색 API (가족 생성시 + 가족원 추가시)
     * [GET] /users
     * @param keyword null 가능
     * @param familyId null 가능
     * @return BaseResponse<List<GetSearchUserRes>>
     */
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "회원 최대 5명 검색", description = "회원을 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = GetSearchUserRes.class)))
    })
    public BaseResponse<List<GetSearchUserRes>> searchUser(@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "familyId", required = false) Long familyId,
                                                           @AuthenticationPrincipal @Parameter(hidden = true) User user) {
        List<GetSearchUserRes> getSearchUserRes = userService.searchUserById(keyword, familyId, user);
        return new BaseResponse<>(getSearchUserRes);
    }
    /**
     * 초대 리스트 확인 API
     * [GET] /users/invitation
     * @return BaseResponse<List<GetInvitationRes>>
     */
    @GetMapping("/users/invitation")
    @Operation(summary = "초대 리스트 확인", description = "사용자가 아직 수락하지 않은 초대 리스트를 확인할 수 있는 API입니다.")
    public BaseResponse<List<GetInvitationRes>> getInvitationList(@AuthenticationPrincipal @Parameter(hidden=true) User user){
        try {
            List<GetInvitationRes> getInvitationRes = userService.getInvitationList(user);

            // TODO: 초대 요청이 없을 경우 빈 리스트 반환
//            if (getInvitationRes.isEmpty()) {
//                return new BaseResponse<>(false, FIND_FAIL_INVITATION.getMessage(), HttpStatus.NOT_FOUND.value());
//            }

            return new BaseResponse<>(getInvitationRes);
        } catch (NoSuchElementException e) {
            return new BaseResponse<>(false, e.getMessage(), 400);
        }
    }
    /**
     * 회원 정보 수정 API
     * [POST] /users/edit
     * @param profileImg
     * @return BaseResponse<PatchProfileReqRes>
     */
    @PostMapping(value = "/users/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = PatchProfileReqRes.class)))
    })
    public BaseResponse<PatchProfileReqRes> updateProfile(@RequestPart(name = "profileImg", required = false) MultipartFile profileImg,
                                                          @RequestPart(name = "PatchProfileReqRes") PatchProfileReqRes patchProfileReqRes,
                                                          @AuthenticationPrincipal @Parameter(hidden = true) User user) throws BaseException {
        if(profileImg == null || profileImg.isEmpty()) { //이미지 비어있으면 원래 이미지 넣어주기
            patchProfileReqRes.setProfileImg(user.getProfileImg());
        } else {
            String fileUrl = awsS3Service.uploadImage(profileImg);
            patchProfileReqRes.setProfileImg(fileUrl);
        }
        if(patchProfileReqRes.getName() == null || patchProfileReqRes.getName().isEmpty()) { //이름 비어있으면
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        if(patchProfileReqRes.getBirthdate() == null || patchProfileReqRes.getBirthdate().isEmpty()) { //생년월일 비어있으면
            return new BaseResponse<>(POST_USERS_EMPTY_BIRTH);
        }
        if(!isRegexBirth(patchProfileReqRes.getBirthdate())) { //생년월일 형식 다르면
            return new BaseResponse<>(POST_USERS_INVALID_BIRTH);
        }
        if(patchProfileReqRes.getNickname() == null || patchProfileReqRes.getNickname().isEmpty()) { //닉네임 비어있으면
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        }
        if(!isRegexNickName(patchProfileReqRes.getNickname())) { //닉네임 형식 다르면
            return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
        }

        PatchProfileReqRes updatedUser = userService.updateProfile(patchProfileReqRes, user);
        return new BaseResponse<>(updatedUser);
    }
    /**
     * 비밀번호 인증 API
     * [POST] /users/auth/compare-pwd
     * @return BaseResponse<String>
     */
    @PostMapping(value = "/users/auth/compare-pwd", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "비밀번호 인증", description = "비밀번호를 인증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(examples = {@ExampleObject(value = "[{\"isSuccess\": \"true\", \"code\":\"200\", \"message\":\"요청에 성공하였습니다.\", \"result\":\"비밀번호가 일치합니다.\"}]")})),
    })
    public BaseResponse<String> authenticate(@RequestBody GetPwdReq getPwdReq,
                                             @AuthenticationPrincipal @Parameter(hidden = true) User user, @RequestHeader("X-AUTH-TOKEN") String requestAccessToken) {
        if(!userService.authenticate(getPwdReq, user)) {
            return new BaseResponse<>(FAILED_AUTHENTICATION);
        }
        return new BaseResponse<>("비밀번호가 일치합니다.");
    }
    /**
     * 비밀번호 변경 API
     * [PATCH] /users/modify-pwd
     * @return BaseResponse<String>
     */
    @Transactional
    @PatchMapping(value = "/users/modify-pwd", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "비밀번호 변경", description = "비밀번호를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(examples = {@ExampleObject(value = "[{\"isSuccess\": \"true\", \"code\":\"200\", \"message\":\"요청에 성공하였습니다.\", \"result\":\"비밀번호가 변경되고 로그아웃 됐습니다.\"}]")})),
    })
    public BaseResponse<String> updatePassword(@RequestBody PatchPwdReq patchPwdReq,
                                               @AuthenticationPrincipal @Parameter(hidden = true) User user, @RequestHeader("X-AUTH-TOKEN") String requestAccessToken) {
        //비밀번호 변경
        if(!authenticate(new GetPwdReq(patchPwdReq.getPassword()), user, requestAccessToken).getIsSuccess()) { //비밀번호 인증
            return new BaseResponse<>(false, "비밀번호가 올바르지 않습니다.", 4000); //<- 403
        }
        //새 비밀번호 빈 입력
        if(patchPwdReq.getNewPassword_first().isEmpty() || patchPwdReq.getNewPassword().isEmpty()) {
            return new BaseResponse<>(false, "비밀번호를 입력하세요.", 4001); //<- 400 EMPTY_PASSWORD
        }
        //새 비밀번호 재확인
        if(!patchPwdReq.getNewPassword_first().equals(patchPwdReq.getNewPassword())) {
            return new BaseResponse<>(false, "새 비밀번호가 일치하지 않습니다.", 4002); //<- 400
        }
        //기존 비밀번호와 새 비밀번호 일치
        if(patchPwdReq.getPassword().equals(patchPwdReq.getNewPassword())) {
            return new BaseResponse<>(false, "기존 비밀번호와 같습니다.", 4003); //<- 400 EQUAL_NEW_PASSWORD
        }
        //새 비밀번호 형식
        if(!isRegexPw(patchPwdReq.getNewPassword())) {
            return new BaseResponse<>(false, "비밀번호 형식을 확인해주세요.", 4004); //<- 400 POST_USERS_INVALID_PW
        }
        userService.updatePassword(patchPwdReq, user);

        //2. 보안을 위해 로그아웃
        authService.logout(requestAccessToken);
        return new BaseResponse<>("비밀번호가 변경되고 로그아웃 됐습니다.");
    }
    /**
     * 비밀번호 찾기 - 재설정
     * [POST] /users/auth/modify-pwd
     * @return BaseResponse<String>
     */
    @Transactional
    @NoAuthCheck
    @PatchMapping("/users/auth/modify-pwd")
    @Operation(summary = "비밀번호 재설정", description = "로그인을 하지 않은 상태에서 비밀번호를 재설정하는 API입니다.")
    public BaseResponse<String> updatePasswordWithoutLogin(@Parameter(description = "비밀번호 재설정을 위해 입력한 두 비밀번호의 일치 여부를 확인합니다.")
                                                               @Valid @RequestBody PatchPwdWithoutLoginReq patchPwdWithoutLoginReq,
                                                           @RequestParam String id) throws BaseException{

        String memberId = emailService.getUserId(id);
        try {
            if(userService.checkDuplicateIdByStatus(memberId)) {
                // 새 비밀번호 형식
                if(!isRegexPw(patchPwdWithoutLoginReq.getPasswordA()) || !isRegexPw(patchPwdWithoutLoginReq.getPasswordB())) {
                    return new BaseResponse<>(POST_USERS_INVALID_PW);
                }
                // 새 비밀번호 입력 불일치
                if(!patchPwdWithoutLoginReq.getPasswordA().equals(patchPwdWithoutLoginReq.getPasswordB())) {
                    return new BaseResponse<>(NOT_EQUAL_NEW_PASSWORD);
                }

                userService.updatePasswordWithoutLogin(patchPwdWithoutLoginReq, memberId);

                return new BaseResponse<>("비밀번호가 변경되었습니다. 다시 로그인을 진행해주세요.");
            } else {
                return new BaseResponse<>(false, FIND_FAIL_USER_ID.getMessage(), HttpStatus.NOT_FOUND.value());
            }
        } catch (NoSuchElementException e) {
            return new BaseResponse<>(false, e.getMessage(), HttpStatus.NOT_FOUND.value());
        }

    }

    /**
     * 전체 회원정보 조회 API / 화면 외 API
     * [GET] /users/all
     * @return BaseResponse<List<User>>
     */
    @NoAuthCheck
    @GetMapping("/users/all")
    public BaseResponse<List<User>> getAllUser() {
        List<User> userList = userService.getAllUser();
        return new BaseResponse<>(userList);
    }

    /**
     * 회원 탈퇴 API
     * [DELETE] /users
     * @return BaseResponse<String>
     */
    @Transactional
    @DeleteMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(examples = {@ExampleObject(value = "[{\"isSuccess\": \"true\", \"code\":\"200\", \"message\":\"요청에 성공하였습니다.\", \"result\":\"계정을 삭제했습니다.\"}]")})),
            //@ApiResponse(responseCode = "500", description = "가족 생성자는 탈퇴할 수 없습니다.", content = @Content(examples = {@ExampleObject(value = "[{\"isSuccess\": \"false\", \"code\":\"500\", \"message\":\"가족 생성자 권한을 다른 사람에게 넘기고 탈퇴해야 합니다.\"}]")})),
            //@ApiResponse(responseCode = "461", description = "유효한 사용자이지만, 토큰의 유효기간이 만료됐습니다.", content = @Content(examples = {@ExampleObject(value = "[{\"isSuccess\": \"false\", \"code\":\"461\", \"message\":\"Access Token의 기한이 만료되었습니다. 재발급 API를 호출해주세요\"}]")})),
            //@ApiResponse(responseCode = "403", description = "유효한 사용자가 아닙니다.", content = @Content(examples = {@ExampleObject(value = "[{\"isSuccess\": \"false\", \"code\":\"403\", \"message\":\"권한이 없는 유저의 접근입니다.\"}]")}))
    })
    public BaseResponse<String> deleteUser(@AuthenticationPrincipal @Parameter(hidden = true) User user, @RequestHeader("X-AUTH-TOKEN") String requestAccessToken) {
        userService.deleteUserWithRedisProcess(user, requestAccessToken);
        return new BaseResponse<>("계정을 삭제했습니다.");
    }

    /**
     * 유저 신고 API
     * [POST] /users/report
     */
    @PostMapping("/users/report/{userId}")
    public BaseResponse<String> reportUser(@PathVariable Long userId) {
        userService.reportUser(userId);
        return new BaseResponse<>("유저를 신고했습니다.");
    }

}