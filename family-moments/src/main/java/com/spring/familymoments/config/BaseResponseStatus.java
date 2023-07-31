package com.spring.familymoments.config;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 200 : 요청 성공
     */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),



    /**
     * 400 : Request 오류, Response 오류
     */
    // Common
    REQUEST_ERROR(false, HttpStatus.BAD_REQUEST.value(), "입력값을 확인해주세요."),
    EMPTY_JWT(false, HttpStatus.UNAUTHORIZED.value(), "JWT를 입력해주세요."),
    INVALID_JWT(false, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,HttpStatus.FORBIDDEN.value(),"권한이 없는 유저의 접근입니다."),
    RESPONSE_ERROR(false, HttpStatus.NOT_FOUND.value(), "값을 불러오는데 실패하였습니다."),
    EXPIRED_JWT(false, HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다."),

    // [POST] /users
    USERS_EMPTY_USER_ID(false, HttpStatus.BAD_REQUEST.value(), "유저 아이디 값을 확인해주세요."),
    POST_USERS_INVALID_ID(false, HttpStatus.BAD_REQUEST.value(), "아이디 형식을 확인해주세요."),
    POST_USERS_EXISTS_ID(false,HttpStatus.BAD_REQUEST.value(),"중복된 아이디입니다."),
    POST_USERS_INVALID_PW(false, HttpStatus.BAD_REQUEST.value(), "비밀번호 형식을 확인해주세요."),
    POST_USERS_EMPTY_NAME(false, HttpStatus.BAD_REQUEST.value(), "이름을 입력해주세요."),
    POST_USERS_EMPTY_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,HttpStatus.BAD_REQUEST.value(),"중복된 이메일입니다."),
    POST_USERS_INVALID_BIRTH(false, HttpStatus.BAD_REQUEST.value(), "생년월일 형식을 확인해주세요."),
    POST_USERS_EMPTY_NICKNAME(false, HttpStatus.BAD_REQUEST.value(), "닉네임을 입력해주세요."),
    POST_USERS_INVALID_NICKNAME(false, HttpStatus.BAD_REQUEST.value(), "닉네임 형식을 확인해주세요."),
    FAILED_TO_LOGIN(false,HttpStatus.NOT_FOUND.value(),"아이디와 비밀번호가 일치하지 않습니다."),

    FIND_FAIL_FAMILY(false, HttpStatus.NOT_FOUND.value(), "존재하지 않는 가족입니다."),
    FAILED_INVITE_USER_FAMILY(false, HttpStatus.CONFLICT.value(), "이미 가족에 가입된 회원입니다."),
    EMPTY_PASSWORD(false, HttpStatus.BAD_REQUEST.value(), "비밀번호를 입력해주세요."),
    FAILED_AUTHENTICATION(false, HttpStatus.FORBIDDEN.value(), "비밀번호가 올바르지 않습니다."),
    EQUAL_NEW_PASSWORD(false, HttpStatus.BAD_REQUEST.value(), "기존 비밀번호와 같습니다."),
    FAILED_USERSS_UNATHORIZED(false, HttpStatus.BAD_REQUEST.value(), "권한이 없는 사용자입니다."),
    FIND_FAIL_POST(false, HttpStatus.BAD_REQUEST.value(), "비활성화된 게시글입니다."),
    NOT_EQUAL_NEW_PASSWORD(false, HttpStatus.BAD_REQUEST.value(), "입력한 비밀번호와 일치하지 않습니다."),

    /**
     * 500 : Database, Server 오류
     */
    DATABASE_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버와의 연결에 실패하였습니다."),

    //[GET] /users
    FIND_FAIL_USERNAME(false,HttpStatus.NOT_FOUND.value(),"가입되지 않은 회원입니다."),
    FIND_FAIL_USER_EMAIL(false,HttpStatus.NOT_FOUND.value(),"가입되지 않은 이메일입니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 복호화에 실패하였습니다."),

    POST_FAIL_S3(false,HttpStatus.NOT_FOUND.value(),"사진 업로드에 실패하였습니다."),
    DELETE_FAIL_S3(false,HttpStatus.NOT_FOUND.value(),"사진 삭제에 실패하였습니다.");



    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
