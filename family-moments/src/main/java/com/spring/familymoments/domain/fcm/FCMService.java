package com.spring.familymoments.domain.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.fcm.model.FCMReq;
import com.spring.familymoments.domain.user.UserRepository;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.spring.familymoments.config.BaseResponseStatus.FIND_FAIL_FAMILY;
import static com.spring.familymoments.config.BaseResponseStatus.FIND_FAIL_FCMTOKEN;
import static com.spring.familymoments.config.BaseResponseStatus.FIND_FAIL_USER;

@Service
@RequiredArgsConstructor
public class FCMService  implements NotificationService {
    private final FirebaseMessaging firebaseMessaging;
    private final UserRepository userRepository;
    private final FCMTokenDao fcmTokenDao;

    public String sendUploadAlarm(FCMReq requestDto) {
        User user = userRepository.findById(requestDto.getTargetUserld())   // User 존재 여부 확인
                .orElseThrow(() -> new BaseException(FIND_FAIL_USER));
        if (!hasKey(user.getId())) {                                        // FCM 토큰 존재 여부 확인
            throw new BaseException(FIND_FAIL_FCMTOKEN);
        }

        String token = getToken(user.getId());

        if (token != null) {
            // 메시지 생성
            Notification notification = Notification.builder()
                    .setTitle(requestDto.getTitle())
                    .setBody(requestDto.getBody())
                    .build();
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(notification)
                    .build();
            // 메시지 전송
            try {
                firebaseMessaging.send(message);
                return "알림 전송 성공. targetUserId=" + requestDto.getTargetUserld();
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
                return "알림 전송 실패. targetUserId=" + requestDto.getTargetUserld();
            }
        } else {
            throw new BaseException(FIND_FAIL_FCMTOKEN);
        }

    }

    public void saveToken(String userId, String fcmToken) {
        fcmTokenDao.saveToken(userId, fcmToken);
    }

    public void deleteToken(String userId) {
        fcmTokenDao.deleteToken(userId);
    }

//    private void send(Message message) {
//        FirebaseMessaging.getInstance().sendAsync(message);
//    }

    private String getToken(String userId) {
        return fcmTokenDao.getToken(userId);
    }

    private boolean hasKey(String userId) {
        return fcmTokenDao.hasKey(userId);
    }

}
