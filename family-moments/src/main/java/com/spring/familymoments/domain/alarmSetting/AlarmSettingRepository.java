package com.spring.familymoments.domain.alarmSetting;

import com.spring.familymoments.domain.alarmSetting.entity.AlarmSetting;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlarmSettingRepository extends JpaRepository<AlarmSetting, Long> {
    Optional<AlarmSetting> findByUserAndAlarmType(User user, AlarmSetting.AlarmType alarmType);
}
