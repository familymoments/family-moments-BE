package com.spring.familymoments.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 정보 조회 관련 Response")
public class GetProfileRes {
    @Schema(description = "이름", example = "김철수")
    private String name;
    @Schema(description = "생년월일", example = "20000101")
    private String birthDate;
    @Schema(description = "회원이미지", example = "https://familymoments-image-bucket.s3.ap-northeast-2.amazonaws.com") //임의로 지정
    private String profileImg;
    @Schema(description = "닉네임", example = "영희좋아철수")
    private String nickName;
    @Schema(description = "이메일", example = "family@gmail.com")
    private String email;
    @Schema(description = "게시물 개수", example = "1")
    private Long totalUpload;
    @Schema(description = "댓글 개수", example = "1")
    private Long totalComments;
    @Schema(description = "앱에 가입한 날짜로부터 경과 시간", example = "7")
    private Long duration;
}
