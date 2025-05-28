package com.zikk.backend.global;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(InputStream inputStream, String fileName, String dir) {
        try {
            // 전체 바이트로 읽고 content-length 확보
            byte[] bytes = inputStream.readAllBytes();

            String key = dir + "/" + fileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .acl("public-read")
                    .contentType("image/jpeg")
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            return s3Client.utilities().getUrl(b -> b.bucket(bucket).key(key)).toExternalForm();

        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 중 IOException 발생", e);
        }
    }
}
