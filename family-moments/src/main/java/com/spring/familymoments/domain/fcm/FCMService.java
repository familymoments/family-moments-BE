package com.spring.familymoments.domain.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.common.UserFamilyRepository;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.fcm.model.FCMReq;
import com.spring.familymoments.domain.fcm.model.MessageTemplate;
import com.spring.familymoments.domain.user.UserRepository;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.spring.familymoments.config.BaseResponseStatus.FIND_FAIL_FCMTOKEN;

@Service
@RequiredArgsConstructor
public class FCMService  implements NotificationService {
    private final FirebaseMessaging firebaseMessaging;
    private final UserRepository userRepository;
    private final UserFamilyRepository userFamilyRepository;
    private final FCMTokenDao fcmTokenDao;

    public void sendUploadAlarm(FCMReq requestDto) {
//        User user = userRepository.findById(requestDto.getTargetUserld())   // User 존재 여부 확인
//                .orElseThrow(() -> new BaseException(FIND_FAIL_USER));

        List<User> receiveUsers = userRepository.findAll();
        for (User user : receiveUsers) {
            sendMessage(user, MessageTemplate.UPLOAD_ALARM);    // 템플릿 선택: 업로드 알림
        }

//        return null;
    }

    private void sendMessage(User user, MessageTemplate template) {
        // FCM 토큰 존재 여부 확인
        if (!hasKey(user.getId())) {
            throw new BaseException(FIND_FAIL_FCMTOKEN);
        }
        String token = getToken(user.getId());

        // 메시지 전송
        if (token != null) {
            try {
                firebaseMessaging.send(createMessage(user, template, token));
                System.out.println("알림 전송 성공. targetUserId=" + user.getId());
//                    return "알림 전송 성공. targetUserId=" + user.getId();
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
                System.out.println("알림 전송 실패. targetUserId=" + user.getId());
//                    return "알림 전송 실패. targetUserId=" + user.getId();
            }
        } else {
            throw new BaseException(FIND_FAIL_FCMTOKEN);
        }
    }

    private Message createMessage(User user, MessageTemplate template, String token) {
        return Message.builder()
                .setToken(token)
                .setNotification(createNotification(user, template))
                .build();
    }

    private Notification createNotification(User user, MessageTemplate template) {
        return Notification.builder()
                .setBody(createMessageBody(user, template))
                .build();
    }

    private String createMessageBody(User user, MessageTemplate template) {
        return String.format(
                template.getTemplate(),
                user.getNickname(),
                getFamilyNameByUserId(user.getUserId()));
    }

    public void saveToken(String id, String fcmToken) { fcmTokenDao.saveToken(id, fcmToken); }

    public void deleteToken(String id) {
        fcmTokenDao.deleteToken(id);
    }

    private String getToken(String id) {
        return fcmTokenDao.getToken(id);
    }

    private boolean hasKey(String id) {
        return fcmTokenDao.hasKey(id);
    }

    public String getFamilyNameByUserId(Long userId) {
        return userFamilyRepository.findFamilyNameByUserId(userId);
    }

}
