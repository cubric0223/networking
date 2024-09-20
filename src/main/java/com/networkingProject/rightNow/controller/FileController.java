package com.networkingProject.rightNow.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.networkingProject.rightNow.service.ExpenditureService;
import com.networkingProject.rightNow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final AmazonS3 amazonS3Client;
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private final ExpenditureService expenditureService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @PostMapping("/expenditure/upload/{expenditureId}")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long expenditureId) {
        // 가져온 세션에서 userId 없으면 에러
        try {
            String fileName = file.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // 파일 업로드
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

            // 파일 URL
            String fileUrl = amazonS3Client.getUrl(bucket, fileName).toString();

            expenditureService.updateImageUrl(expenditureId, fileUrl);
            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            logger.error("Error uploading file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
