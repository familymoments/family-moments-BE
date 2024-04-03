package com.spring.familymoments.domain.alarmSetting;

import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/alarms")
@Tag(name = "AlarmSetting", description = "알림 설정 API Document")
public class AlarmSettingController {

    private final AlarmSettingService alarmSettingService;

    /**
     * 업로드 주기 알림 활성화 API
     * [PATCH] /alarms/cycle/active
     * @return BaseResponse<String>
     */
    @Operation(summary = "업로드 주기 알림 활성화")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
    })
    @PatchMapping(value ="/cycle/active")
    public BaseResponse<String> updateCycleActive(@AuthenticationPrincipal @Parameter(hidden = true) User user) {
        alarmSettingService.updateCycleActive(user);
        return new BaseResponse<>("업로드 주기 알림이 활성화됐습니다.");
    }

    /**
     * 업로드 주기 알림 비활성화 API
     * [PATCH] /alarms/cycle/inactive
     * @return BaseResponse<String>
     */
    @Operation(summary = "업로드 주기 알림 비활성화")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
    })
    @PatchMapping(value ="/cycle/inactive")
    public BaseResponse<String> updateCycleInactive(@AuthenticationPrincipal @Parameter(hidden = true) User user) {
        alarmSettingService.updateCycleInactive(user);
        return new BaseResponse<>("업로드 주기 알림이 비활성화됐습니다.");
    }
}
