package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.advice.exception.InternalServerErrorException;
import com.spring.familymoments.domain.awsS3.AwsS3Service;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.NoSuchElementException;

import static com.spring.familymoments.config.BaseResponseStatus.*;
import static com.spring.familymoments.utils.ValidationRegex.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final AwsS3Service awsS3Service;

    /**
     * 회원 가입 API
     * [POST] /users/sign-up
     * @param postUserReq 프로필 이미지 URL 저장
     * @param profileImage 프로필 이미지 원본 저장
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("/users/sign-up")
    public BaseResponse<PostUserRes> createUser(@RequestPart("newUser") PostUserReq.joinUser postUserReq,
                                                @RequestPart("profileImg") MultipartFile profileImage) throws BaseException {
        //아이디
        if(postUserReq.getId() == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        if(!isRegexId(postUserReq.getId())) {
            return new BaseResponse<>(POST_USERS_INVALID_ID);
        }
        // TODO: 아이디 중복 체크
        if(userService.checkDuplicateId(postUserReq.getId())){
            log.info("[createUser]: 이미 존재하는 아이디입니다!");
            return new BaseResponse<>(POST_USERS_EXISTS_ID);
        }
        //비밀번호
        if(!isRegexPw(postUserReq.getPassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_PW);
        }
        //이름
        if(postUserReq.getName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        //이메일
        if(postUserReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        // TODO: 이메일 중복 체크
        if(userService.checkDuplicateEmail(postUserReq.getEmail())){
            log.info("[createUser]: 이미 존재하는 이메일입니다!");
            return new BaseResponse<>(POST_USERS_EXISTS_EMAIL);
        }
        //생년월일
        if(!isRegexBirth(postUserReq.getStrBirthDate())) {
            return new BaseResponse<>(POST_USERS_INVALID_BIRTH);
        }
        //닉네임
        if(postUserReq.getNickname() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        }
        if(!isRegexNickName(postUserReq.getNickname())) {
            return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
        }

        String fileUrl = null;

        if(postUserReq.getProfileImg() == null){
            fileUrl = awsS3Service.uploadImage(profileImage);
        }

        postUserReq.setProfileImg(fileUrl);

        PostUserRes postUserRes = userService.createUser(postUserReq, profileImage);
        log.info("[createUser]: PostUserRes 생성 완료!");
        return new BaseResponse<>(postUserRes);
    }

    /**
     * 아이디 중복 확인 API
     * [GET] /users/check-id
     * @return ResponseEntity<Boolean> -> 이미 가입된 아이디면 true, 그렇지 않으면 false
     */
    @GetMapping("/users/check-id")
    public ResponseEntity<Boolean> checkDuplicateId(@RequestParam String id) throws BaseException {
        return ResponseEntity.ok(userService.checkDuplicateId(id));
    }

    /**
     * 이메일 중복 확인 API
     * [GET] /users/check-email
     * @return ResponseEntity<Boolean> -> 이미 가입된 이메일이면 true, 그렇지 않으면 false
     */
    @GetMapping("/users/check-email")
    public ResponseEntity<Boolean> checkDuplicateEmail(@RequestParam String email) throws BaseException {
        return ResponseEntity.ok(userService.checkDuplicateEmail(email));
    }

    /**
     * 로그인 API
     * [POST] /users/log-in
     * @return BaseResponse<>(postLoginRes)
     */
    @PostMapping("/users/log-in")
    public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq, HttpServletResponse response) {
        try{
            PostLoginRes postLoginRes = userService.createLogin(postLoginReq, response);
            return new BaseResponse<>(postLoginRes);
        } catch(NoSuchElementException e){
            return new BaseResponse<>(FAILED_TO_LOGIN);
        }
    }

    /**
     * 로그아웃 API
     * [POST] /users/log-out
     * @return
     */
    @PostMapping("/users/log-out")
    public BaseResponse<String> logout(HttpServletResponse response) throws BaseException {
        Cookie cookie = new Cookie("X-AUTH-TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        response.addCookie(cookie);
        return new BaseResponse<>("로그아웃 했습니다.");
    }
    /**
     * 아이디 찾기 API
     * [POST] /users/auth/find-id
     * @return BaseResponse<GetUserIdRes>
     */
    @PostMapping("/users/auth/find-id")
    public BaseResponse<GetUserIdRes> findUserId(@RequestBody PostEmailReq.sendVerificationEmail sendEmailReq)
            throws InternalServerErrorException, MessagingException, BaseException {

        //이름
        if(sendEmailReq.getName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        //이메일
        if(sendEmailReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(!isRegexEmail(sendEmailReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        GetUserIdRes getUserIdRes = emailService.findUserId(sendEmailReq);

        return new BaseResponse<>(getUserIdRes);
    }
    /**
     * 비밀번호 찾기 - 아이디 존재 여부 확인
     * [POST] /users/auth/check-id
     * @return BaseResponse<GetUserIdRes>
     */
    @RequestMapping("/users/auth/check-id")
    public BaseResponse<GetUserIdRes> findUserIdBeforeUpdatePwd(@RequestParam String id)
            throws InternalServerErrorException, MessagingException, BaseException {
        try {
            if (userService.checkDuplicateId(id)) {
                GetUserIdRes getUserIdRes = new GetUserIdRes(id);
                return new BaseResponse<>(getUserIdRes);
            } else {
                return new BaseResponse<>(false, FIND_FAIL_ID.getMessage(), HttpStatus.NOT_FOUND.value());
            }
        } catch (NoSuchElementException e) {
            return new BaseResponse<>(false, e.getMessage(), HttpStatus.NOT_FOUND.value());
        }
    }
    /**
     * 비밀번호 찾기 API - 이메일 인증 확인
     * [POST] /users/auth/find-pwd
     * @return BaseResponse<String>
     */
    @PostMapping("/users/auth/find-pwd")
    public BaseResponse<String> findUserPwd(@RequestBody PostEmailReq.sendVerificationEmail sendEmailReq)
            throws InternalServerErrorException, MessagingException, BaseException {

        //이름
        if(sendEmailReq.getName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        //이메일
        if(sendEmailReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(!isRegexEmail(sendEmailReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        GetUserIdRes getUserIdRes = emailService.findUserId(sendEmailReq);

        return new BaseResponse<String>("이메일이 인증되었습니다. 새로운 비밀번호를 입력해주세요.");
    }
    /**
     * 회원정보 조회 API
     * [GET] /users/families/{familyId}/profile
     * @return BaseResponse<GetProfileRes>
     */
    @RequestMapping("/users/families/{familyId}/profile")
    public BaseResponse<GetProfileRes> readProfile(@PathVariable Long familyId, @AuthenticationPrincipal User user) {
        try {
            GetProfileRes getProfileRes = userService.readProfile(user, familyId);
            return new BaseResponse<>(getProfileRes);
        } catch (NoSuchElementException e) {
            return new BaseResponse<>(false, e.getMessage(), HttpStatus.NOT_FOUND.value());
        }
    }

    /**
     * 유저 검색 API / 가족원 추가 API
     * [GET] /users/families/{familyId}?keyword={}
     * @param keyword null 가능
     * @return BaseResponse<List<GetSearchUserRes>>
     */
    @GetMapping("/users/families/{familyId}")
    public BaseResponse<List<GetSearchUserRes>> searchUser(@RequestParam(value = "keyword", required = false) String keyword, @PathVariable Long familyId, @AuthenticationPrincipal User user) {
        List<GetSearchUserRes> getSearchUserRes = userService.searchUserById(keyword, familyId, user);
        return new BaseResponse<>(getSearchUserRes);
    }
    /**
     * 초대 리스트 확인 API
     * [GET] /users/invitation
     * @return BaseResponse<List<GetInvitationRes>>
     */
    @GetMapping("/users/invitation")
    public BaseResponse<List<GetInvitationRes>> getInvitationList(@AuthenticationPrincipal User user){
        try {
            List<GetInvitationRes> getInvitationRes = userService.getInvitationList(user);

            if (getInvitationRes.isEmpty()) {
                return new BaseResponse<>(false, FIND_FAIL_INVITATION.getMessage(), HttpStatus.NOT_FOUND.value());
            }

            return new BaseResponse<>(getInvitationRes);
        } catch (NoSuchElementException e) {
            return new BaseResponse<>(false, e.getMessage(), 400);
        }
    }
    /**
     * 회원 정보 수정 API
     * [PATCH] /users
     * @param profileImg
     * @return BaseResponse<PatchProfileReqRes>
     */
    @PatchMapping("/users")
    public BaseResponse<PatchProfileReqRes> updateProfile(@RequestParam(name = "profileImg") MultipartFile profileImg, @RequestPart PatchProfileReqRes patchProfileReqRes, @AuthenticationPrincipal User user) throws BaseException {
        String fileUrl = awsS3Service.uploadImage(profileImg);
        patchProfileReqRes.setProfileImg(fileUrl);

        PatchProfileReqRes updatedUser = userService.updateProfile(patchProfileReqRes, user);
        return new BaseResponse<>(updatedUser);
    }
    /**
     * 비밀번호 인증 API
     * [GET] /users/auth/compare-pwd
     * @return BaseResponse<String>
     */
    @GetMapping("/users/auth/compare-pwd")
    public BaseResponse<String> authenticate(@RequestBody GetPwdReq getPwdReq, @AuthenticationPrincipal User user) {
        try {
            if(userService.authenticate(getPwdReq, user)) {
                return new BaseResponse<>("비밀번호가 일치합니다.");
            }
            else {
                return new BaseResponse<>(FAILED_AUTHENTICATION);
            }
        } catch(NoSuchElementException e) {
            System.out.println(e.getMessage());
            return new BaseResponse<>(EMPTY_PASSWORD);
        }
    }
    /**
     * 비밀번호 변경 API
     * [PATCH] /users/modify-pwd
     * @return BaseResponse<String>
     */
    @Transactional
    @PatchMapping("/users/modify-pwd")
    public BaseResponse<String> updatePassword(@RequestBody PatchPwdReq patchPwdReq, @AuthenticationPrincipal User user, HttpServletResponse response) {
        //1. 비밀번호 변경
        if(!authenticate(new GetPwdReq(patchPwdReq.getPassword()), user).getIsSuccess()) { //비밀번호 인증
            return new BaseResponse<>(FAILED_AUTHENTICATION);
        }
        if(patchPwdReq.getPassword().equals(patchPwdReq.getNewPassword())) { //newPassword와 password 일치시
            return new BaseResponse<>(EQUAL_NEW_PASSWORD);
        }
        if(patchPwdReq.getNewPassword() == null || patchPwdReq.getNewPassword() == "") { //새 비밀번호 빈 입력
            return new BaseResponse<>(EMPTY_PASSWORD);
        }
        if(!isRegexPw(patchPwdReq.getNewPassword())) { //새 비밀번호 형식
            return new BaseResponse<>(POST_USERS_INVALID_PW);
        }
        userService.updatePassword(patchPwdReq, user);

        //2. 보안을 위해 로그아웃
        try {
            logout(response);
        } catch (BaseException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return new BaseResponse<>("비밀번호가 변경되고 로그아웃 됐습니다.");
    }
    /**
     * 비밀번호 찾기 - 재설정
     * [POST] /users/auth/modify-pwd
     * @return BaseResponse<String>
     */
    @Transactional
    @PatchMapping("/users/auth/modify-pwd")
    public BaseResponse<String> updatePasswordWithoutLogin(@RequestBody PatchPwdWithoutLoginReq patchPwdWithoutLoginReq,
                                                           @RequestParam String id) {

        String memberEmail = emailService.getUserId(id);

        //1. 비밀번호 변경
        if(!patchPwdWithoutLoginReq.getPasswordA().equals(patchPwdWithoutLoginReq.getPasswordB())) { //newPassword와 password 일치시
            return new BaseResponse<>(NOT_EQUAL_NEW_PASSWORD);
        }
        if(patchPwdWithoutLoginReq.getPasswordB() == "") { //새 비밀번호 빈 입력
            return new BaseResponse<>(EMPTY_PASSWORD);
        }
        if(!isRegexPw(patchPwdWithoutLoginReq.getPasswordA())) { //새 비밀번호 형식
            return new BaseResponse<>(POST_USERS_INVALID_PW);
        }
        userService.updatePasswordWithoutLogin(patchPwdWithoutLoginReq, memberEmail);

        return new BaseResponse<>("비밀번호가 변경되었습니다. 다시 로그인을 진행해주세요.");
    }

    /**
     * 전체 회원정보 조회 API / 화면 외 API
     * [GET] /users/all
     * @return BaseResponse<List<User>>
     */
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
    @DeleteMapping("/users")
    public BaseResponse<String> deleteUser(@AuthenticationPrincipal User user) {
        try {
            userService.deleteUser(user);
            return new BaseResponse<>("계정을 삭제했습니다.");
        } catch (IllegalAccessException e) {
            return new BaseResponse(false, e.getMessage(), 500);
        }
    }
}
