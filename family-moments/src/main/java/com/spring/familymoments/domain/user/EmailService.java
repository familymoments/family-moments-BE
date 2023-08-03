package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.advice.exception.InternalServerErrorException;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.GetUserIdRes;
import com.spring.familymoments.domain.user.model.PostEmailReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final UserRepository userRepository;

    private final JavaMailSender emailSender;

    private String randomVerificationCode;

    /**
     * createRandomCode
     *
     * @return String
     */
    public String createRandomCode(){
        randomVerificationCode = RandomStringUtils.random(6, 33, 125, false, true);

        return randomVerificationCode;
    }

    /**
     * createEmailForm
     *
     * @return MimeMessage -> 인증 메일
     */
    public MimeMessage createEmailForm(String email) throws MessagingException {

        createRandomCode();

        String setFrom = "sonshumc75@gmail.com";
        String emailReceiver = email; //받는 사람
        String title = "[Family Moments] 인증 번호를 확인해주세요.";

        MimeMessage message = emailSender.createMimeMessage();
        message.setFrom(setFrom); //발신자 설정
        message.addRecipients(MimeMessage.RecipientType.TO, emailReceiver); //수신자 설정
        message.setSubject(title); //제목 설정
        message.setText("Family Moments를 이용해 주셔서 감사합니다. 요청하신 인증 번호는 ["+randomVerificationCode+"] 입니다."); //내용 설정

        return message;
    }

    /**
     * createEmailForm
     * 이메일 전송
     * @return String randomVerificationCode
     */
    public String sendEmail(String name, String emailReceiver) throws MessagingException, BaseException {

        MimeMessage emailForm = createEmailForm(emailReceiver);
        emailSender.send(emailForm);

        return randomVerificationCode;
    }

    /**
     * 아이디/비밀번호 찾기 -> 입력한 이름, 이메일과 일치하는 회원 정보가 있는지 확인
     * [GET]
     * @return 일치하는 회원 정보가 존재하면 true, 그렇지 않으면 false
     */
    public boolean checkNameAndEmail(PostEmailReq.sendVerificationEmail req) {
        return userRepository.existsByNameAndEmail(req.getName(), req.getEmail());
    }

    /**
     * 아이디/비밀번호 찾기 -> 입력한 코드와 발송한 코드가 서로 같은지 확인
     * [GET]
     * @return 같은 값이면 true, 그렇지 않으면 false
     */
    public boolean checkVerificationCode(PostEmailReq.sendVerificationEmail req) {

        // randomVerificationCode = emailService.sendEmail(req.getName(), req.getEmail());
        return Objects.equals(req.getCode(), randomVerificationCode);
    }

    /**
     * 아이디 찾기 -> 인증코드를 제대로 입력했는지 확인
     * [POST]
     * @return GetUserIdRes
     */
    public GetUserIdRes findUserId(PostEmailReq.sendVerificationEmail req) throws BaseException, MessagingException {

        String userId = null;

        User member = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new InternalServerErrorException("가입되지 않은 이메일입니다."));

        userId = member.getId();

        getUserId(userId);

        return new GetUserIdRes(userId);
    }

    public String getUserId(String userId){
        return userId;
    }
}
