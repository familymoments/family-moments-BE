package com.spring.familymoments.domain.post.entity;

import com.spring.familymoments.config.BaseException;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.spring.familymoments.config.BaseResponseStatus.INVALID_REPORT_REASON;

@RequiredArgsConstructor
@Getter
public enum ReportReason {
    COMMERCIAL_PROMOTION("영리목적/홍보성"),
    COPYRIGHT("저작권 침해"),
    OBSCENITY("음란성/선정성"),
    ABUSE("욕설/인신공격"),
    SPAM("같은내용 반복게시"),
    PERSONAL_INFORMATION_EXPOSURE("개인정보노출"),
    OTHER("기타");

    private final String stringReportReason;

    public static ReportReason getEnumTypeFromStringReportReason(String stringReportReason) {
        return Arrays.stream(values())
                .filter(reportReason -> reportReason.stringReportReason.equals(stringReportReason))
                .findFirst()
                .orElseThrow(() -> new BaseException(INVALID_REPORT_REASON));
    }
}
