package com.spring.familymoments.domain.fcm;

import com.spring.familymoments.config.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarms")
@Tag(name = "Alarm", description = "알림 API Document")
public class FCMController {

    private final FCMService fcmService;

    /**
     * [개발용] 업로드 알림 전송 API
     * [GET] /uploadAlarm
     * @return BaseResponse<String>
     */
    @Operation(summary = "업로드 알림 전송 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
    })
    @GetMapping("/uploadAlarm")
    public BaseResponse<String> createUploadAlarm() {
        fcmService.sendUploadAlram();
        return new BaseResponse<>("모든 등록된 유저에게 업로드 알림이 전송되었습니다.");
    }
}
