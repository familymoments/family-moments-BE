package com.spring.familymoments.domain.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.spring.familymoments.domain.family.FamilyRepository;
import com.spring.familymoments.domain.fcm.model.MessageTemplate;
import com.spring.familymoments.domain.fcm.model.UploadaAlramDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMService implements NotificationService {
    private final FirebaseMessaging firebaseMessaging;
    private final FamilyRepository familyRepository;
    private final FCMTokenDao fcmTokenDao;

    public void sendUploadAlram() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        // 현재 날짜를 기준으로 업로드 사이클이 되었다면 알림 전송
        familyRepository.findFamiliesWithUploadCycle(today)
                .stream()
                .forEach(user -> {
                    sendMessage(new UploadaAlramDto(
                            (String) user.get("id"),
                            (String) user.get("nickname"),
                            (String) user.get("familyName"),
                            MessageTemplate.UPLOAD_ALARM)
                    );
                });
        log.info("Upload Alram successfully sent.");
    }

    private void sendMessage(UploadaAlramDto dto) {
        // FCM 토큰 존재 여부 확인. 로그아웃(FCM 토큰 삭제)된 경우 알람 전송하지 않음.
        if (!hasKey(dto.getId())) {
            log.warn("FCM token not found for user with ID: " + dto.getId());
            return;
        }

        // 메시지 전송
        try {
            firebaseMessaging.send(createMessage(dto));
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                // FCM 토큰이 더 이상 유효하지 않은 경우에 대한 처리
                // 예: 토큰을 데이터베이스에서 삭제하거나 다시 등록 요청을 보내는 등의 작업 수행
                log.error("FCM token for user {} is invalid or unregistered", dto.getId());
                deleteToken(dto.getId());     // FCM Token 삭제
            } else {
                // 그 외의 FCM 예외 처리
                log.error("Failed to send FCM message to user {}", dto.getId());
            }
        }
    }

    private Message createMessage(UploadaAlramDto dto) {
        return Message.builder()
                .setToken(getToken(dto.getId()))
                .setNotification(createNotification(dto))
                .build();
    }

    private Notification createNotification(UploadaAlramDto dto) {
        return Notification.builder()
                .setBody(createMessageBody(dto))
                .build();
    }

    private String createMessageBody(UploadaAlramDto dto) {
        return String.format(dto.getTemplate().getTemplate(), dto.getNickname(), dto.getFamilyName());
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

}
