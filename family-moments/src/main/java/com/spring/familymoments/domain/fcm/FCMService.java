package com.spring.familymoments.domain.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
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
            e.printStackTrace();
            log.error("Failed to send Upload Alram. Target userId = " + dto.getId());
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

    public String getToken(String id) {
        return fcmTokenDao.getToken(id);
    }

    private boolean hasKey(String id) {
        return fcmTokenDao.hasKey(id);
    }

}
