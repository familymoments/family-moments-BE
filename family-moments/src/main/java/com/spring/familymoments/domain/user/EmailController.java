package com.spring.familymoments.domain.user;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.user.model.PostEmailReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/users/auth/send-email")
    public String sendVerificationEmail(@RequestBody PostEmailReq.sendVerificationEmail sendEmailReq)
            throws MessagingException, BaseException {
        return emailService.sendEmail(sendEmailReq.getName(), sendEmailReq.getEmail());
    }
}
