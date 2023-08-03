package com.spring.familymoments.domain.awsS3;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.config.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/s3")
public class AwsS3Controller {

    @Autowired
    private final AwsS3Service awsS3Service;

    public AwsS3Controller(AwsS3Service awsS3Service) {
        this.awsS3Service = awsS3Service;
    }

    @ResponseBody
    @PostMapping("/image")
    public BaseResponse<String> uploadImage(@RequestParam(name = "image") MultipartFile image) throws BaseException {
        String fileUrl = awsS3Service.uploadImage(image);
        return new BaseResponse<>(fileUrl);
    }

    @ResponseBody
    @PostMapping("/images")
    public BaseResponse<List<String>> uploadImages(@RequestPart(required = false) List<MultipartFile> images) throws BaseException {
        List<String> fileUrls = awsS3Service.uploadImages(images);
        return new BaseResponse<>(fileUrls);
    }

    @ResponseBody
    @DeleteMapping("")
    public BaseResponse<String> deleteImage(String fileName) {
        try {
            awsS3Service.deleteImage(fileName);
            return new BaseResponse<>("성공했습니다.");

        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

}
