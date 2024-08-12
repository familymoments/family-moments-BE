package com.spring.familymoments.utils;

import com.spring.familymoments.config.BaseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.spring.familymoments.config.BaseResponseStatus.FIND_FAIL_DATE;
import static com.spring.familymoments.config.BaseResponseStatus.INVALID_TIME_FORMAT;

public class CustomDateTimeUtils {

    // 포맷터 정의
    private static final DateTimeFormatter FORMATTER_INPUT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
    private static final DateTimeFormatter FORMATTER_yyyyMMddHHmmss = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FORMATTER_yyyyMMddHHmm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter FORMATTER_yyyyMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private CustomDateTimeUtils() {
        // 인스턴스 생성 방지
    }

    // 공통 포맷팅 메서드
    private static String formatWithFormatter(String dateTimeStr, DateTimeFormatter outputFormatter) {
        if (dateTimeStr == null) {
            throw new BaseException(FIND_FAIL_DATE);
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, FORMATTER_INPUT);
            return dateTime.format(outputFormatter);
        } catch (DateTimeParseException e) {
            throw new BaseException(INVALID_TIME_FORMAT);
        }
    }

    // 문자열 -> yyyy-MM-dd HH:mm:ss
    public static String format_yyyyMMddHHmmss(String dateTimeStr) {
        return formatWithFormatter(dateTimeStr, FORMATTER_yyyyMMddHHmmss);
    }

    // 문자열 -> yyyy-MM-dd HH:mm
    public static String format_yyyyMMddHHmm(String dateTimeStr) {
        return formatWithFormatter(dateTimeStr, FORMATTER_yyyyMMddHHmm);
    }

    // 문자열 -> yyyy-MM-dd
    public static String format_yyyyMMdd(String dateTimeStr) {
        return formatWithFormatter(dateTimeStr, FORMATTER_yyyyMMdd);
    }
}