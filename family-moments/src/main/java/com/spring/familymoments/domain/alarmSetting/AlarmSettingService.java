package com.spring.familymoments.domain.alarmSetting;

import com.spring.familymoments.domain.alarmSetting.entity.AlarmSetting;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlarmSettingService {

    private final AlarmSettingRepository alarmSettingRepository;

    // 알림 설정
    @Transactional
    public void createAlarmSetting(User user) {
        for (AlarmSetting.AlarmType alarmType : AlarmSetting.AlarmType.values()) {
            saveAlarmSetting(user, alarmType);
        }
    }

    // 타입별 알림 설정 저장
    private void saveAlarmSetting(User user, AlarmSetting.AlarmType alarmType) {
        AlarmSetting alarmSetting = AlarmSetting.builder()
                .user(user)
                .alarmType(alarmType)
                .build();

        alarmSettingRepository.save(alarmSetting);
    }

}
