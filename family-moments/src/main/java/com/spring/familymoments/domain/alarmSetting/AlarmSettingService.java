package com.spring.familymoments.domain.alarmSetting;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.alarmSetting.entity.AlarmSetting;
import com.spring.familymoments.domain.user.UserRepository;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spring.familymoments.config.BaseResponseStatus.FIND_FAIL_ALARMSETTING;

@Service
@RequiredArgsConstructor
public class AlarmSettingService {

    private final AlarmSettingRepository alarmSettingRepository;
    private final UserRepository userRepository;

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

    // 업로드 주기 알림 활성화
    public void updateCycleActive(User user) {
        AlarmSetting alarmSetting = alarmSettingRepository.findByUserAndAlarmType(user, AlarmSetting.AlarmType.CYCLE)
                .orElseThrow(() -> new BaseException(FIND_FAIL_ALARMSETTING));

        alarmSetting.setStatus(AlarmSetting.Status.ACTIVE);
        alarmSettingRepository.save(alarmSetting);
    }

    // 업로드 주기 알림 비활성화
    public void updateCycleInactive(User user) {
        AlarmSetting alarmSetting = alarmSettingRepository.findByUserAndAlarmType(user, AlarmSetting.AlarmType.CYCLE)
                .orElseThrow(() -> new BaseException(FIND_FAIL_ALARMSETTING));

        alarmSetting.setStatus(AlarmSetting.Status.INACTIVE);
        alarmSettingRepository.save(alarmSetting);
    }

}
