package com.spring.familymoments.domain.post.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시물 신고 Request")
public class ContentReportReq {
    @Schema(description = "신고 사유", example = "영리목적/홍보성")
    private String reportReason;
    @Schema(description = "자세한 신고 사유", example = "부가설명")
    private String details;
}
