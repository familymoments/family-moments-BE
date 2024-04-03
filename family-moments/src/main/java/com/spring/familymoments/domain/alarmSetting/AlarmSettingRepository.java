package com.spring.familymoments.domain.alarmSetting;

import com.spring.familymoments.domain.alarmSetting.entity.AlarmSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmSettingRepository extends JpaRepository<AlarmSetting, Long> {
}
