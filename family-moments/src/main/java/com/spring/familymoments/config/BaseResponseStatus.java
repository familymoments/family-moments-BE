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
    INVALID_JWT(false, 461, "Access Token의 기한이 만료되었습니다. 재발급 API를 호출해주세요"),
    INVALID_USER_JWT(false,HttpStatus.FORBIDDEN.value(),"권한이 없는 유저의 접근입니다."),
    TOKEN_RESPONSE_ERROR(false, HttpStatus.NOT_FOUND.value(), "값을 불러오는데 실패하였습니다."),
    EXPIRED_JWT(false, HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다."),

    // users
    USERS_EMPTY_USER_ID(false, HttpStatus.BAD_REQUEST.value(), "아이디를 입력해주세요."),

    POST_USERS_INVALID_ID(false, HttpStatus.BAD_REQUEST.value(), "아이디 형식을 확인해주세요."),
    POST_USERS_EXISTS_ID(false,HttpStatus.BAD_REQUEST.value(),"이미 가입한 아이디가 존재합니다."),
    POST_USERS_INVALID_PW(false, HttpStatus.BAD_REQUEST.value(), "비밀번호 형식을 확인해주세요."),
    POST_USERS_EMPTY_NAME(false, HttpStatus.BAD_REQUEST.value(), "이름을 입력해주세요."),
    POST_USERS_EMPTY_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, HttpStatus.BAD_REQUEST.value(), "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,HttpStatus.BAD_REQUEST.value(),"이미 가입한 이메일이 존재합니다."),
    POST_USERS_EMPTY_BIRTH(false, HttpStatus.BAD_REQUEST.value(), "생년월일을 입력해주세요"),
    POST_USERS_INVALID_BIRTH(false, HttpStatus.BAD_REQUEST.value(), "생년월일 형식을 확인해주세요."),
    POST_USERS_EMPTY_NICKNAME(false, HttpStatus.BAD_REQUEST.value(), "닉네임을 입력해주세요."),
    POST_USERS_INVALID_NICKNAME(false, HttpStatus.BAD_REQUEST.value(), "닉네임 형식을 확인해주세요."),
    NOT_EQUAL_VERIFICATION_CODE(false, HttpStatus.BAD_REQUEST.value(), "인증 번호가 일치하지 않습니다."),
    FIND_FAIL_USER_NAME_EMAIL(false,HttpStatus.NOT_FOUND.value(), "이름과 이메일이 정확히 입력되었는지 확인해주세요."),
    FIND_FAIL_ID(false, HttpStatus.NOT_FOUND.value(), "입력한 아이디와 일치하는 회원정보가 없습니다."),
    FIND_FAIL_INVITATION(false, HttpStatus.NOT_FOUND.value(), "초대 요청 목록이 존재하지 않습니다."),
    FAILED_TO_LOGIN_ID(false,HttpStatus.NOT_FOUND.value(),"아이디가 일치하지 않습니다."),
    FAILED_TO_LOGIN_PWD(false,HttpStatus.NOT_FOUND.value(),"비밀번호가 일치하지 않습니다."),
    FAILED_TO_LOGIN(false,HttpStatus.NOT_FOUND.value(), "탈퇴하거나 신고당한 유저입니다."),
    FIND_FAIL_FAMILY(false, HttpStatus.NOT_FOUND.value(), "존재하지 않는 가족입니다."),
    NEED_TO_JOIN_AS_THIS_SOCIAL(false, 481, "해당 소셜로 회원 가입을 해야 합니다."),
    FAILED_INVITE_USER_FAMILY(false, HttpStatus.CONFLICT.value(), "이미 가족에 가입된 회원입니다."),
    FAMILY_LIMIT_EXCEEDED(false, HttpStatus.CONFLICT.value(), "가족은 최대 5개까지만 가능합니다."),
    FAILED_SOCIAL_JOIN(false, HttpStatus.CONFLICT.value(), "이미 해당 소셜 계정이 있는 회원입니다."),
    EMPTY_PASSWORD(false, HttpStatus.BAD_REQUEST.value(), "비밀번호를 입력해주세요."),
    FAILED_AUTHENTICATION(false, HttpStatus.FORBIDDEN.value(), "비밀번호가 올바르지 않습니다."),
    EQUAL_NEW_PASSWORD(false, HttpStatus.BAD_REQUEST.value(), "기존 비밀번호와 같습니다."),
    FAILED_USERSS_UNATHORIZED(false, HttpStatus.BAD_REQUEST.value(), "권한이 없는 사용자입니다."),
    FIND_FAIL_POST(false, HttpStatus.BAD_REQUEST.value(), "비활성화된 게시글입니다."),
    NOT_EQUAL_NEW_PASSWORD(false, HttpStatus.BAD_REQUEST.value(), "입력한 비밀번호와 일치하지 않습니다."),
    EXPIRED_AT_ERROR(false, 471, "탈퇴를 위해 가입했던 소셜 계정으로 재로그인 하세요"),
    /**
     * 500 : Database, Server 오류
     */
    DATABASE_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버와의 연결에 실패하였습니다."),


    FIND_FAIL_USER_ID(false,HttpStatus.NOT_FOUND.value(),"존재하지 않는 아이디입니다."),
    FIND_FAIL_USERNAME(false,HttpStatus.NOT_FOUND.value(),"가입되지 않은 회원입니다."),
    FIND_FAIL_USER_EMAIL(false,HttpStatus.NOT_FOUND.value(),"존재하지 않는 이메일입니다."),
    FIND_FAIL_USER_NAME_AND_EMAIL(false,HttpStatus.NOT_FOUND.value(), "일치하는 회원 정보가 없습니다."),
    FAILED_TO_LEAVE(false, 409, "생성자 권한을 다른 사람에게 넘기고 탈퇴해야 합니다." ),

    FIND_FAIL_USER(false,HttpStatus.NOT_FOUND.value(),"존재하지 않는 사용자입니다."),
    MODIFY_FAIL_USERNAME(false,HttpStatus.INTERNAL_SERVER_ERROR.value(),"유저네임 수정 실패"),
    PASSWORD_ENCRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "비밀번호 복호화에 실패하였습니다."),

    POST_FAIL_S3(false,HttpStatus.NOT_FOUND.value(),"사진 업로드에 실패하였습니다."),
    DELETE_FAIL_S3(false,HttpStatus.NOT_FOUND.value(),"사진 삭제에 실패하였습니다."),
    NO_ACTIVE_COMMENTS(false, HttpStatus.NOT_FOUND.value(), "댓글이 존재하지 않습니다."),
    FIND_FAIL_COMMENT(false, HttpStatus.NOT_FOUND.value(), "존재하지 않는 댓글입니다."),
    ALREADY_DELETE_COMMENT(false, HttpStatus.NOT_FOUND.value(), "이미 삭제된 댓글입니다."),
    COMMENTS_EMPTY_CONTENT(false, HttpStatus.NOT_FOUND.value(), "댓글 내용을 입력해주세요."),

    minnie_POSTS_INVALID_USER(false, HttpStatus.FORBIDDEN.value(), "수정 권한이 없습니다."),
    minnie_POSTS_INVALID_POST_ID(false, HttpStatus.NOT_FOUND.value(), "유효하지 않은 postId 입니다."),
    minnie_POSTS_NON_EXISTS_POST(false, HttpStatus.NOT_FOUND.value(), "post가 존재하지 않습니다."),
    minnie_POSTS_EMPTY_UPDATE(false, HttpStatus.BAD_REQUEST.value(), "수정할 내용을 보내주세요."),
    minnie_POSTS_EMPTY_CONTENT(false, HttpStatus.BAD_REQUEST.value(), "내용을 입력해주세요."),
    minnie_POSTS_EMPTY_IMAGE(false, HttpStatus.BAD_REQUEST.value(), "img1에 이미지를 지정해주세요."),
    minnie_POSTS_EMPTY_POST_INFO(false, HttpStatus.BAD_REQUEST.value(), "postInfo가 포함되어야 합니다."),
    minnie_POST_SAVE_FAIL(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "게시물 저장에 실패했습니다."),
    minnie_POSTLOVES_NON_EXISTS_LOVE(false, HttpStatus.NOT_FOUND.value(), "좋아요가 존재하지 않습니다."),
    minnie_FAMILY_INVALID_USER(false, HttpStatus.FORBIDDEN.value(), "해당 가족의 멤버가 아닙니다"),

    POSTLOVE_ALREADY_EXISTS(false, HttpStatus.BAD_REQUEST.value(), "이미 좋아요를 누른 게시물입니다."),
    FIND_FAIL_POSTLOVE(false, HttpStatus.NOT_FOUND.value(), "좋아요를 누르지 않아 취소할 수 없습니다."),

    COMMENTLOVE_ALREADY_EXISTS(false, HttpStatus.BAD_REQUEST.value(), "이미 좋아요를 누른 게시물입니다."),
    FIND_FAIL_COMMENTLOVE(false, HttpStatus.NOT_FOUND.value(), "좋아요를 누르지 않아 취소할 수 없습니다."),

    NOT_FAMILY_OWNER(false, HttpStatus.FORBIDDEN.value(), "가족 권한이 없습니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}