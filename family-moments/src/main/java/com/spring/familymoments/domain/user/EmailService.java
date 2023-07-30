package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.advice.exception.InternalServerErrorException;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.GetEmailRes;
import com.spring.familymoments.domain.user.model.GetUserIdRes;
import com.spring.familymoments.domain.user.model.PatchPwdWithoutLoginReq;
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
    public String sendEmail(String name, String emailReceiver) throws MessagingException, BaseException {

        if(userRepository.existsByNameAndEmail(name, emailReceiver)) {
            MimeMessage emailForm = createEmailForm(emailReceiver);
            emailSender.send(emailForm);
        } else if(!userRepository.existsByEmail(emailReceiver) && userRepository.existsByName(name)){
            throw new InternalServerErrorException("입력하신 이메일을 다시 확인해주세요.");
        } else if(userRepository.existsByEmail(emailReceiver) && !userRepository.existsByName(name)){
            throw new InternalServerErrorException("입력하신 성함을 다시 확인해주세요.");
        } else {
            throw new InternalServerErrorException("정확한 정보를 다시 입력해주세요.");
        }

        return randomVerificationCode;
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

        if(checkVerificationCode(req) && userRepository.existsByNameAndEmail(req.getName(), req.getEmail())){
            User member = userRepository.findByEmail(req.getEmail())
                    .orElseThrow(() -> new InternalServerErrorException("가입되지 않은 이메일입니다."));

            userId = member.getId();
        } else {
            throw new InternalServerErrorException("인증코드가 일치하지 않습니다.");
        }

        return new GetUserIdRes(userId);
    }

    public GetEmailRes updateUserPwd(PostEmailReq.sendVerificationEmail req){
        return new GetEmailRes(req.getEmail());
    }
}
