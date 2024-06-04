package com.spring.familymoments.domain.awsS3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.spring.familymoments.config.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.spring.familymoments.config.BaseResponseStatus.DELETE_FAIL_S3;
import static com.spring.familymoments.config.BaseResponseStatus.POST_FAIL_S3;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AwsS3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Autowired
    private final AmazonS3 amazonS3;

    public String uploadImage(MultipartFile image) throws BaseException {
        try {
            String fileName = createFileName(image.getOriginalFilename());
//            String fileName = UUID.randomUUID().toString();
            String fileUrl = amazonS3.getUrl(bucket, fileName).toString();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(image.getSize());
            objectMetadata.setContentType(image.getContentType());

            try (InputStream inputStream = image.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
            }

            return fileUrl;
        }
        catch (Exception e){
            throw new BaseException(POST_FAIL_S3);
        }
    }

    public List<String> uploadImages(List<MultipartFile> images) {
        List<String> fileNameList = new ArrayList<>();
        List<String> fileUrlList = new ArrayList<>();

        images.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            String fileUrl = amazonS3.getUrl(bucket, fileName).toString();

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch(IOException e) {
                log.error(e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
            }

            fileNameList.add(fileName);
            fileUrlList.add(fileUrl);
        });

        return fileUrlList;
    }

    public void deleteImage(String fileName) throws BaseException {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));

        }
        catch (Exception e){
            throw new BaseException(DELETE_FAIL_S3);
        }
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }
}
