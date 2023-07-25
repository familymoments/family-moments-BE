package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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
        String title = "Family Moments: 아이디 찾기 인증 번호";

        MimeMessage message = emailSender.createMimeMessage();
        message.setFrom(setFrom); //발신자 설정
        message.addRecipients(MimeMessage.RecipientType.TO, emailReceiver); //수신자 설정
        message.setSubject(title); //제목 설정
        message.setText("요청하신 아이디 찾기 인증 번호는 ["+randomVerificationCode+"] 입니다."); //내용 설정

        return message;
    }

    /**
     * createEmailForm
     * 이메일 전송
     * @return String randomVerificationCode
     */
    public String sendEmail(String emailReceiver) throws MessagingException, BaseException {

        if(userRepository.existsByEmail(emailReceiver)) {
            MimeMessage emailForm = createEmailForm(emailReceiver);
            emailSender.send(emailForm);
        } else {
            throw new BaseException(BaseResponseStatus.FIND_FAIL_USER_EMAIL);
        }

        return randomVerificationCode;
    }

}
