package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.redis.RedisService;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.GetUserIdRes;
import com.spring.familymoments.domain.user.model.PostEmailReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Optional;

import static com.spring.familymoments.config.BaseResponseStatus.*;
import static com.spring.familymoments.utils.ValidationRegex.isRegexCode;
import static com.spring.familymoments.utils.ValidationRegex.isRegexEmail;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final UserRepository userRepository;

    private final RedisService redisService;

    private final JavaMailSender emailSender;

    private String randomVerificationCode;

    /**
     * createRandomCode
     *
     * @return String
     */
    public String createRandomCode(String email){
        randomVerificationCode = RandomStringUtils.random(6, 33, 125, false, true);
        int CODE_EXPIRATION_TIME = 3 * 60 * 1000; // 인증 코드 유효 시간: 3분

        redisService.setValuesWithTimeout("VC(" + email + "):", randomVerificationCode, CODE_EXPIRATION_TIME);

        return randomVerificationCode;
    }

    /**
     * createEmailForm
     *
     * @return MimeMessage -> 인증 메일
     */
    public MimeMessage createEmailForm(String email) throws MessagingException, UnsupportedEncodingException {

        createRandomCode(email);

        String emailReceiver = email; //받는 사람
        String title = "Family Moments 본인 인증 번호";
        String messageContext = "";
        messageContext += "<div style = \"background-color: #F7E1E3; margin: -8px -8px 50px -8px; height: 100px; padding-top: 50px;\">\n" +
                "<span style = \"color: #5B6380; margin-left:50px; text-align: left; font-weight:900; font-family: Segoe Script; font-size: 25px;\">Family Moments</span>\n" +
                "</div>\n" +
                "<h1 style = \"display: flex; justify-content: flex-start; margin-left:50px; font-weight:900; font-family: Roboto; font-size:35px; margin-bottom: 80px;\">이메일 인증</h1>\n" +
                "<h2 style = \"display: flex; justify-content: flex-start; margin-left:50px; font-weight:900; font-family: Roboto; font-size:20px; margin-bottom: 10px;\">안녕하세요, 고객님</h2>\n" +
                "<p style = \"display: flex; justify-content: flex-start; margin-left:50px; font-weight:900; font-family: Roboto; margin-bottom: 60px;\">아이디 찾기/비밀번호 재설정을 위해 이메일 인증을 진행합니다.\n" +
                "아래 발급된 이메일 인증번호를 복사하거나 직접 입력하여 인증을 완료해주세요.</p>\n" +
                "<span style = \"display: flex; justify-content: flex-start; margin-left:50px; font-weight:900; font-family: Roboto; margin-bottom: 10px;\">인증번호 : " + randomVerificationCode + "</span>\n" +
                "<p style = \"display: flex; justify-content: flex-start; margin-left:50px; font-weight:900; font-family: Roboto; margin-bottom: 100px;\">감사합니다.</p>\n" +
                "<p style = \"color: #96979C; display: flex; justify-content: flex-start; margin-left:50px; font-weight:900; font-family: Roboto; font-size: 10px;\">* 귀하가 한 행동이 아니라면 이 이메일을 무시하셔도 됩니다.</p>\n";

        MimeMessage message = emailSender.createMimeMessage();
        message.setFrom(new InternetAddress("sonshumc75@gmail.com", "Family Moments")); //발신자 설정
        message.addRecipients(MimeMessage.RecipientType.TO, emailReceiver); //수신자 설정
        message.setSubject(title); //제목 설정
        message.setText(messageContext, "utf-8", "html"); //내용 설정

        return message;
    }

    /**
     * createEmailForm
     * 이메일 전송
     * @return String randomVerificationCode
     */
    public String sendEmail(String emailReceiver) throws MessagingException, BaseException, UnsupportedEncodingException {

        MimeMessage emailForm = createEmailForm(emailReceiver);
        emailSender.send(emailForm);

        return randomVerificationCode;
    }

    /**
     * 아이디/비밀번호 찾기 -> 입력한 이름, 이메일과 일치하는 회원 정보가 있는지 확인
     * [GET]
     * @return 일치하는 회원 정보가 존재하면 true, 그렇지 않으면 false
     */
    /*public boolean checkNameAndEmail(PostEmailReq.sendVerificationEmail req) {
        return userRepository.existsByNameAndEmail(req.getName(), req.getEmail());
    }*/

    /**
     * 아이디/비밀번호 찾기 -> 입력한 이름, 이메일과 일치하는 회원 정보가 있는지 확인
     * [GET]
     * @return 일치하는 회원 정보가 존재하면 true, 그렇지 않으면 false
     */
    public boolean checkEmailByStatus(PostEmailReq.sendVerificationEmail req) {
        Optional<User> user = userRepository.findByEmail(req.getEmail());
        return user.isPresent();
    }

    /**
     * 회원 가입 -> 입력한 코드와 발송한 코드가 서로 같은지 확인
     * [GET]
     * @return 같은 값이면 true, 그렇지 않으면 false
     */
    public boolean checkVerificationCodeBeforeSignUp(PostEmailReq.sendVerificationEmail req) throws BaseException {

        // 일치하는 회원 정보가 있는 경우 -> UserController(createUser)의 예외처리와 중복
        /*
        if(checkEmailByStatus(req)) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        */

        //이메일
        if(!isRegexEmail(req.getEmail())) {
            throw new BaseException(POST_USERS_INVALID_EMAIL);
        }

        // 인증 코드를 입력하지 않은 경우
        if(req.getCode().isEmpty()) {
            throw new BaseException(EMPTY_VERIFICATION_CODE);
        }

        // 인증 코드 형식이 잘못된 경우
        if(!isRegexCode(req.getCode())) {
            throw new BaseException(INVALID_VERIFICATION_CODE);
        }

        // 유효 시간이 만료된 경우
        if(!redisService.hasKey("VC("+ req.getEmail() + "):")) {
            throw new BaseException(VERIFICATION_TIME_EXPIRED);
        }

        // randomVerificationCode = emailService.sendEmail(req.getName(), req.getEmail());
        randomVerificationCode = redisService.getValues("VC("+ req.getEmail() + "):");

        if(!Objects.equals(req.getCode(), randomVerificationCode)) {
            return false;
        } else {
            redisService.setValues("VE(" + req.getEmail() + "):", randomVerificationCode);
            return true;
        }
        // return Objects.equals(req.getCode(), randomVerificationCode);
    }

    /**
     * 아이디/비밀번호 찾기 -> 입력한 코드와 발송한 코드가 서로 같은지 확인
     * [GET]
     * @return 같은 값이면 true, 그렇지 않으면 false
     */
    public boolean checkVerificationCode(PostEmailReq.sendVerificationEmail req) throws BaseException {

        // 일치하는 회원 정보가 없는 경우
        if(!checkEmailByStatus(req)) {
            throw new BaseException(FIND_FAIL_USER_EMAIL);
        }

        // 인증 코드를 입력하지 않은 경우
        if(req.getCode().isEmpty()) {
            throw new BaseException(EMPTY_VERIFICATION_CODE);
        }

        // 인증 코드 형식이 잘못된 경우
        if(!isRegexCode(req.getCode())) {
            throw new BaseException(INVALID_VERIFICATION_CODE);
        }

        // 유효 시간이 만료된 경우
        if(!redisService.hasKey("VC("+ req.getEmail() + "):")) {
            throw new BaseException(VERIFICATION_TIME_EXPIRED);
        }

        // randomVerificationCode = emailService.sendEmail(req.getName(), req.getEmail());
        randomVerificationCode = redisService.getValues("VC("+ req.getEmail() + "):");

        return Objects.equals(req.getCode(), randomVerificationCode);
    }

    /**
     * 아이디 찾기 -> 인증코드를 제대로 입력했는지 확인
     * [POST]
     * @return GetUserIdRes
     */
    public GetUserIdRes findUserId(PostEmailReq.sendVerificationEmail req) throws BaseException, MessagingException {

        String userId = null;

        User member = userRepository.findByEmail(req.getEmail()).orElseThrow(() -> new BaseException(FIND_FAIL_USER_EMAIL));

        userId = member.getId();

        getUserId(userId);

        // 인증코드 인증 완료 후 redis 에서 삭제
        redisService.deleteValues("VC("+ req.getEmail() + "):");

        return new GetUserIdRes(userId);
    }

    public String getUserId(String userId){
        return userId;
    }
}
