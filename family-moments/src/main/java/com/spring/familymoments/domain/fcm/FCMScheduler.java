package com.spring.familymoments.domain.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FCMScheduler {
    private final FCMService fcmService;

    /**
     * 업로드 알림
     * Cron 표현식을 사용한 작업 예약
     * 초(0-59) 분(0-59) 시간(0-23) 일(1-31) 월(1-12) 요일(0-7)
     */
//    @Scheduled(initialDelay = 0, fixedDelay = 60000)    // 초기 지연: 0밀리초, 실행 간격: 60초
    @Scheduled(cron = "0 0 10 * * *")
    public void sendUploadAlarm() {
        log.info("=== UPLOAD ALRAM START ===");
        fcmService.sendUploadAlram();
        log.info("=== UPLOAD ALRAM END ===");
    }
}
