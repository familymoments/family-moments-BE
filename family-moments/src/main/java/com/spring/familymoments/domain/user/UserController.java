package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.secret.jwt.JwtService;
import com.spring.familymoments.domain.awsS3.AwsS3Service;
import com.spring.familymoments.domain.redis.RedisService;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
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
    private final AuthService authService;
    private final RedisService redisService;
    private final JwtService jwtService;

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
        if(postUserReq.getId() == null || postUserReq.getId().isEmpty()) {
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
        if(postUserReq.getName() == null || postUserReq.getName().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        //이메일
        if(postUserReq.getEmail() == null || postUserReq.getEmail().isEmpty()) {
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
        if(postUserReq.getStrBirthDate() == null || postUserReq.getStrBirthDate().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_BIRTH);
        }
        if(!isRegexBirth(postUserReq.getStrBirthDate())) {
            return new BaseResponse<>(POST_USERS_INVALID_BIRTH);
        }
        //닉네임
        if(postUserReq.getNickname() == null || postUserReq.getNickname().isEmpty()) {
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
     * @return BaseResponse<String>
     */
    @PostMapping("/users/check-id")
    public BaseResponse<String> checkDuplicateId(@RequestBody GetDuplicateUserIdReq getDuplicateUserIdReq) throws BaseException {
        try{
            if(!userService.checkDuplicateId(getDuplicateUserIdReq.getId())) {
                return new BaseResponse<>("사용 가능한 아이디입니다.");
            } else {
                return new BaseResponse<>(POST_USERS_EXISTS_ID);
            }
        } catch (NoSuchElementException e){
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
    }

    /**
     * 이메일 중복 확인 API
     * [GET] /users/check-email
     * @return BaseResponse<String>
     */
    @PostMapping("/users/check-email")
    public BaseResponse<String> checkDuplicateEmail(@RequestBody GetDuplicateUserEmailReq getDuplicateUserEmailReq) throws BaseException {
        try{
            if(!userService.checkDuplicateEmail(getDuplicateUserEmailReq.getEmail())) {
                return new BaseResponse<>("사용 가능한 이메일입니다.");
            } else {
                return new BaseResponse<>(POST_USERS_EXISTS_EMAIL);
            }
        } catch (NoSuchElementException e){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
    }
    /**
     * 아이디 찾기 API
     * [POST] /users/auth/find-id
     * @return BaseResponse<GetUserIdRes>
     */
    @PostMapping("/users/auth/find-id")
    public BaseResponse<GetUserIdRes> findUserId(@RequestBody PostEmailReq.sendVerificationEmail sendEmailReq)
            throws MessagingException, BaseException {

        //이름
        if(sendEmailReq.getName() == null || sendEmailReq.getName().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        //이메일
        if(sendEmailReq.getEmail() == null || sendEmailReq.getEmail().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(!isRegexEmail(sendEmailReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        try {
            if(emailService.checkVerificationCode(sendEmailReq) && emailService.checkNameAndEmail(sendEmailReq)) {
                GetUserIdRes getUserIdRes = emailService.findUserId(sendEmailReq);
                return new BaseResponse<>(getUserIdRes);
            } else if(emailService.checkVerificationCode(sendEmailReq) && !emailService.checkNameAndEmail(sendEmailReq)) {
                return new BaseResponse<>(FIND_FAIL_USER_NAME_EMAIL);
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
    @PostMapping("/users/auth/check-id")
    public BaseResponse<String> findUserIdBeforeUpdatePwd(@RequestBody GetUserIdReq getUserIdReq)
            throws MessagingException, BaseException {
        try {
            if (userService.checkDuplicateId(getUserIdReq.getUserId())) {
                // GetUserIdRes getUserIdRes = new GetUserIdRes(id);
                // return new BaseResponse<>(getUserIdRes);
                return new BaseResponse<>("입력하신 아이디로 가입을 확인했습니다. 본인 확인을 위하여 이메일로 인증해주세요.");
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
            throws MessagingException, BaseException {

        //이름
        if(sendEmailReq.getName() == null || sendEmailReq.getName().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        //이메일
        if(sendEmailReq.getEmail() == null || sendEmailReq.getEmail().isEmpty()) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(!isRegexEmail(sendEmailReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        try {
            if(emailService.checkVerificationCode(sendEmailReq) && emailService.checkNameAndEmail(sendEmailReq)) {
                GetUserIdRes getUserIdRes = emailService.findUserId(sendEmailReq);
                return new BaseResponse<String>("이메일이 인증되었습니다. 새로운 비밀번호를 입력해주세요.");
            } else if(emailService.checkVerificationCode(sendEmailReq) && !emailService.checkNameAndEmail(sendEmailReq)) {
                return new BaseResponse<>(false, FIND_FAIL_USER_NAME_EMAIL.getMessage(), HttpStatus.NOT_FOUND.value());
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
    @RequestMapping("/users/profile")
    public BaseResponse<GetProfileRes> readProfile(@RequestParam(value = "familyId", required = false) Long familyId,
                                                   @AuthenticationPrincipal User user, @RequestHeader("X-AUTH-TOKEN") String requestAccessToken) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }
        try {
            GetProfileRes getProfileRes = userService.readProfile(user, familyId);
            return new BaseResponse<>(getProfileRes);
        } catch (NoSuchElementException e) {
            return new BaseResponse<>(false, e.getMessage(), HttpStatus.NOT_FOUND.value());
        }
    }

    /**
     * 유저 검색 API (가족 생성시 + 가족원 추가시)
     * [GET] /users
     * @param keyword null 가능
     * @param familyId null 가능
     * @return BaseResponse<List<GetSearchUserRes>>
     */
    @GetMapping("/users")
    public BaseResponse<List<GetSearchUserRes>> searchUser(@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "familyId", required = false) Long familyId,
                                                           @AuthenticationPrincipal User user, @RequestHeader("X-AUTH-TOKEN") String requestAccessToken) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }
        List<GetSearchUserRes> getSearchUserRes = userService.searchUserById(keyword, familyId, user);
        return new BaseResponse<>(getSearchUserRes);
    }
    /**
     * 초대 리스트 확인 API
     * [GET] /users/invitation
     * @return BaseResponse<List<GetInvitationRes>>
     */
    @GetMapping("/users/invitation")
    public BaseResponse<List<GetInvitationRes>> getInvitationList(@AuthenticationPrincipal User user,
                                                                  @RequestHeader("X-AUTH-TOKEN") String requestAccessToken){
        if (authService.validate(requestAccessToken)) {
            return new BaseResponse<>(INVALID_JWT);
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT);
        }

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
     * [PATCH] /users
     * @param profileImg
     * @return BaseResponse<PatchProfileReqRes>
     */
    @PatchMapping("/users")
    public BaseResponse<PatchProfileReqRes> updateProfile(@RequestPart(name = "profileImg") MultipartFile profileImg,
                                                          @RequestPart PatchProfileReqRes patchProfileReqRes,
                                                          @AuthenticationPrincipal User user, @RequestHeader("X-AUTH-TOKEN") String requestAccessToken) throws BaseException {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }
        String fileUrl = awsS3Service.uploadImage(profileImg);
        patchProfileReqRes.setProfileImg(fileUrl);

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
    @PostMapping("/users/auth/compare-pwd")
    public BaseResponse<String> authenticate(@RequestBody GetPwdReq getPwdReq,
                                             @AuthenticationPrincipal User user, @RequestHeader("X-AUTH-TOKEN") String requestAccessToken) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }
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
    public BaseResponse<String> updatePassword(@RequestBody PatchPwdReq patchPwdReq,
                                               @AuthenticationPrincipal User user, @RequestHeader("X-AUTH-TOKEN") String requestAccessToken) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨. -> 461
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }
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
        try {
            authService.logout(requestAccessToken);
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
            return new BaseResponse<>(INVALID_USER_JWT);
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
                                                           @RequestParam String id) throws BaseException{

        String memberId = emailService.getUserId(id);

        try {
            if(userService.checkDuplicateId(memberId)) {
                if(!isRegexPw(patchPwdWithoutLoginReq.getPasswordA()) || !isRegexPw(patchPwdWithoutLoginReq.getPasswordB())) {
                    return new BaseResponse<>(POST_USERS_INVALID_PW);
                }
                if(patchPwdWithoutLoginReq.getPasswordA() == "" || patchPwdWithoutLoginReq.getPasswordA().isEmpty()) {
                    return new BaseResponse<>(EMPTY_PASSWORD);
                }
                if(patchPwdWithoutLoginReq.getPasswordB() == "" || patchPwdWithoutLoginReq.getPasswordB().isEmpty()) {
                    return new BaseResponse<>(EMPTY_PASSWORD);
                }
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
    public BaseResponse<String> deleteUser(@AuthenticationPrincipal User user, @RequestHeader("X-AUTH-TOKEN") String requestAccessToken) {
        if (authService.validate(requestAccessToken)) { //유효한 사용자라 true가 반환됩니다 !!
            return new BaseResponse<>(INVALID_JWT); //401 error : 유효한 사용자이지만, 토큰의 유효 기간이 만료됨.
        }
        if(user == null) {
            return new BaseResponse<>(INVALID_USER_JWT); //403 error : 유효한 사용자가 아님.
        }
        try {
            userService.deleteUser(user);
            //Redis에 저장되어 있는 RT 삭제
            String refreshTokenInRedis = redisService.getValues("RT(" + "SERVER" + "):" + user);
            if(refreshTokenInRedis != null) {
                redisService.deleteValues("RT(" + "SERVER" + "):" + user);
            }
            //Redis에 탈퇴 처리한 AT 저장
            long expiration = jwtService.getTokenExpirationTime(requestAccessToken) - new Date().getTime();
            redisService.setValuesWithTimeout(requestAccessToken, "delete", expiration);

            return new BaseResponse<>("계정을 삭제했습니다.");
        } catch (IllegalAccessException e) {
            return new BaseResponse<>(false, e.getMessage(), 500);
        }
    }
}