package com.spring.familymoments.domain.awsS3;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.config.NoAuthCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/s3")
@Tag(name = "S3", description = "S3 API Document")
public class AwsS3Controller {

    @Autowired
    private final AwsS3Service awsS3Service;

    public AwsS3Controller(AwsS3Service awsS3Service) {
        this.awsS3Service = awsS3Service;
    }

    @ResponseBody
    @PostMapping("/image")
    @Operation(summary = "이미지 등록", description = "이미지 한 개를 S3에 등록하고 URL을 반환합니다.")
    @NoAuthCheck
    public BaseResponse<String> uploadImage(@RequestParam(name = "image") MultipartFile image) throws BaseException {
        String fileUrl = awsS3Service.uploadImage(image);
        return new BaseResponse<>(fileUrl);
    }

    @ResponseBody
    @PostMapping("/images")
    @Operation(summary = "다중 이미지 등록", description = "이미지 여러 개를 S3에 등록하고 URL을 반환합니다.")
    @NoAuthCheck
    public BaseResponse<List<String>> uploadImages(@RequestPart(required = false) List<MultipartFile> images) throws BaseException {
        List<String> fileUrls = awsS3Service.uploadImages(images);
        return new BaseResponse<>(fileUrls);
    }

    @ResponseBody
    @DeleteMapping("")
    @Operation(summary = "이미지 삭제", description = "이미지를 S3에서 삭제합니다.")
    @NoAuthCheck
    public BaseResponse<String> deleteImage(@RequestParam(name = "url", required = true) String fileName) {
        try {
            awsS3Service.deleteImage(fileName);
            return new BaseResponse<>("성공했습니다.");

        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

}
