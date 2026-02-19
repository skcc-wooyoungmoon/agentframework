package com.skax.aiplatform.common.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.skax.aiplatform.common.config.S3Config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketLocationConstraint;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * S3 관련 공통 유틸리티 클래스
 * 
 * <p>
 * S3 업로드, 다운로드, 삭제 등의 기능을 제공합니다.
 * </p>
 * 
 * @author 장지원
 * @since 2025-10-27
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class S3Util {

    private final S3Config s3Config;

    /**
     * S3Client 인스턴스를 생성합니다.
     * 
     * @return S3Client 인스턴스
     */
    private S3Client createS3Client() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                s3Config.getAccessKey(),
                s3Config.getSecretKey());

        S3Client s3Client = S3Client.builder()
                .region(Region.of(s3Config.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(s3Config.isPathStyleAccess())
                        .build())
                .build();

        // 엔드포인트가 설정된 경우 재생성
        if (s3Config.getEndpoint() != null && !s3Config.getEndpoint().isEmpty()) {
            s3Client.close();
            s3Client = S3Client.builder()
                    .region(Region.of(s3Config.getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                    .serviceConfiguration(S3Configuration.builder()
                            .pathStyleAccessEnabled(s3Config.isPathStyleAccess())
                            .build())
                    .endpointOverride(java.net.URI.create(s3Config.getEndpoint()))
                    .build();
        }

        return s3Client;
    }

    /**
     * 파일을 S3에 업로드합니다.
     * 
     * @param file        업로드할 파일
     * @param s3Key       S3 키 (파일 경로)
     * @param contentType 콘텐츠 타입
     * @return 업로드 결과 정보
     */
    public Map<String, Object> uploadFile(File file, String s3Key, String contentType) {
        return uploadFile(file, s3Key, contentType, null);
    }

    /**
     * 파일을 S3에 업로드합니다 (버킷 이름 지정 가능).
     *
     * @param file        업로드할 파일
     * @param s3Key       S3 키 (파일 경로)
     * @param contentType 콘텐츠 타입
     * @param bucketName  버킷 이름 (null이면 기본 버킷 사용)
     * @return 업로드 결과 정보
     */
    public Map<String, Object> uploadFile(File file, String s3Key, String contentType, String bucketName) {
        log.info(">>> S3 파일 업로드 시작 - file: {}, s3Key: {}, contentType: {}, bucketName: {}",
                file.getAbsolutePath(), s3Key, contentType, bucketName);

        try (S3Client s3Client = createS3Client()) {
            // 파일 크기 확인
            long fileSize = file.length();
            if (fileSize < 0) {
                throw new IllegalArgumentException("파일 크기를 확인할 수 없습니다: " + file.getAbsolutePath());
            }

            // 버킷 이름 결정
            String targetBucketName = bucketName != null && !bucketName.trim().isEmpty()
                    ? bucketName
                    : s3Config.getBucketName();

            // S3 키를 그대로 사용
            String finalS3Key = s3Key;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(targetBucketName)
                    .key(finalS3Key)
                    .contentType(contentType)
                    .contentLength(fileSize)
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest,
                    software.amazon.awssdk.core.sync.RequestBody.fromFile(file));

            Map<String, Object> result = new HashMap<>();
            result.put("s3Key", finalS3Key);
            result.put("bucketName", targetBucketName);
            result.put("etag", response.eTag());
            result.put("versionId", response.versionId());
            result.put("filePath", file.getAbsolutePath());
            result.put("fileSize", fileSize);

            log.info(">>> S3 파일 업로드 완료 - s3Key: {}, bucketName: {}, etag: {}, fileSize: {} bytes",
                    finalS3Key, targetBucketName, response.eTag(), fileSize);
            return result;

        } catch (IllegalArgumentException e) {
            log.error(">>> S3 파일 업로드 실패 - 잘못된 파라미터: file={}, s3Key={}, error={}",
                    file.getAbsolutePath(), s3Key, e.getMessage(), e);
            throw new RuntimeException("S3 파일 업로드 실패: 잘못된 파라미터입니다.", e);
        } catch (SecurityException e) {
            log.error(">>> S3 파일 업로드 실패 - 권한 오류: file={}, s3Key={}, error={}",
                    file.getAbsolutePath(), s3Key, e.getMessage(), e);
            throw new RuntimeException("S3 파일 업로드 실패: 파일 접근 권한이 없습니다.", e);
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            log.error(">>> S3 파일 업로드 실패 - S3 서비스 오류: file={}, s3Key={}, statusCode={}, error={}",
                    file.getAbsolutePath(), s3Key, e.statusCode(), e.getMessage(), e);
            throw new RuntimeException("S3 파일 업로드 실패: S3 서비스 오류 (" + e.statusCode() + ")", e);
        } catch (software.amazon.awssdk.core.exception.SdkClientException e) {
            log.error(">>> S3 파일 업로드 실패 - AWS SDK 클라이언트 오류: file={}, s3Key={}, error={}",
                    file.getAbsolutePath(), s3Key, e.getMessage(), e);
            throw new RuntimeException("S3 파일 업로드 실패: AWS SDK 클라이언트 오류입니다.", e);
        } catch (Exception e) {
            log.error(">>> S3 파일 업로드 실패 - file: {}, s3Key: {}, bucketName: {}, error: {}",
                    file.getAbsolutePath(), s3Key, bucketName, e.getMessage(), e);
            throw new RuntimeException("S3 파일 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 새로운 S3 버킷을 생성합니다.
     * 
     * @param bucketName 생성할 버킷 이름
     * @param region     버킷을 생성할 리전 (선택사항, null이면 기본 리전 사용)
     * @return 버킷 생성 결과
     */
    public Map<String, Object> createBucket(String bucketName, String region) {
        log.info(">>> S3 버킷 생성 시작 - bucketName: {}, region: {}", bucketName, region);

        try (S3Client s3Client = createS3Client()) {
            // 먼저 버킷 존재 여부 확인 (동일한 S3Client 사용)
            log.info(">>> 버킷 존재 여부 확인 시작 - bucketName: {}", bucketName);
            boolean exists = bucketExists(bucketName, s3Client);
            log.info(">>> 버킷 존재 여부 확인 결과 - bucketName: {}, exists: {}", bucketName, exists);

            if (exists) {
                log.warn(">>> S3 버킷이 이미 존재함 - bucketName: {}", bucketName);
                Map<String, Object> result = new HashMap<>();
                result.put("bucketName", bucketName);
                result.put("region", region != null ? region : s3Config.getRegion());
                result.put("success", false);
                result.put("message", "버킷이 이미 존재합니다.");
                return result;
            }

            log.info(">>> 버킷이 존재하지 않음, 생성 진행 - bucketName: {}", bucketName);

            // 버킷 생성 요청
            CreateBucketRequest.Builder createBucketRequestBuilder = CreateBucketRequest.builder()
                    .bucket(bucketName);

            // 리전이 지정된 경우 버킷 설정 추가
            if (region != null && !region.isEmpty()) {
                CreateBucketConfiguration bucketConfiguration = CreateBucketConfiguration.builder()
                        .locationConstraint(BucketLocationConstraint.fromValue(region))
                        .build();
                createBucketRequestBuilder.createBucketConfiguration(bucketConfiguration);
            }

            log.info(">>> CreateBucketRequest 실행 중 - bucketName: {}", bucketName);
            CreateBucketResponse response = s3Client.createBucket(createBucketRequestBuilder.build());

            Map<String, Object> result = new HashMap<>();
            result.put("bucketName", bucketName);
            result.put("region", region != null ? region : s3Config.getRegion());
            result.put("location", response.location());
            result.put("success", true);
            result.put("message", "버킷이 성공적으로 생성되었습니다.");

            log.info(">>> S3 버킷 생성 완료 - bucketName: {}, location: {}", bucketName, response.location());
            return result;

        } catch (BucketAlreadyExistsException e) {
            log.warn(">>> S3 버킷이 이미 존재함 (예외 발생) - bucketName: {}, error: {}", bucketName, e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("bucketName", bucketName);
            result.put("region", region != null ? region : s3Config.getRegion());
            result.put("success", false);
            result.put("message", "버킷이 이미 존재합니다.");
            return result;
        } catch (Exception e) {
            log.error(">>> S3 버킷 생성 실패 - bucketName: {}, error: {}", bucketName, e.getMessage(), e);
            throw new RuntimeException("S3 버킷 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * S3 버킷을 삭제합니다.
     * 
     * @param bucketName 삭제할 버킷 이름
     * @return 버킷 삭제 결과
     */
    public Map<String, Object> deleteBucket(String bucketName) {
        log.info(">>> S3 버킷 삭제 시작 - bucketName: {}", bucketName);

        try (S3Client s3Client = createS3Client()) {
            // 버킷 삭제 요청
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.deleteBucket(deleteBucketRequest);

            Map<String, Object> result = new HashMap<>();
            result.put("bucketName", bucketName);
            result.put("success", true);
            result.put("message", "버킷이 성공적으로 삭제되었습니다.");

            log.info(">>> S3 버킷 삭제 완료 - bucketName: {}", bucketName);
            return result;

        } catch (NoSuchBucketException e) {
            log.warn(">>> S3 버킷이 존재하지 않음 - bucketName: {}", bucketName);
            Map<String, Object> result = new HashMap<>();
            result.put("bucketName", bucketName);
            result.put("success", false);
            result.put("message", "버킷이 존재하지 않습니다.");
            return result;
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("not empty")) {
                log.warn(">>> S3 버킷이 비어있지 않음 - bucketName: {}", bucketName);
                Map<String, Object> result = new HashMap<>();
                result.put("bucketName", bucketName);
                result.put("success", false);
                result.put("message", "버킷이 비어있지 않아 삭제할 수 없습니다.");
                return result;
            }
            log.error(">>> S3 버킷 삭제 실패 - bucketName: {}, error: {}", bucketName, e.getMessage(), e);
            throw new RuntimeException("S3 버킷 삭제 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * S3 버킷의 존재 여부를 확인합니다 (S3Client 제공).
     * 
     * @param bucketName 확인할 버킷 이름
     * @param s3Client   S3Client 인스턴스
     * @return 버킷 존재 여부 (boolean)
     */
    public boolean bucketExists(String bucketName, S3Client s3Client) {
        log.info(">>> S3 버킷 존재 확인 (S3Client 제공) - bucketName: {}", bucketName);

        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            log.info(">>> HeadBucketRequest 실행 중 - bucketName: {}", bucketName);
            HeadBucketResponse response = s3Client.headBucket(headBucketRequest);
            log.info(">>> S3 버킷 존재 확인 완료 - bucketName: {}, region: {}", bucketName, response.bucketRegion());
            return true;

        } catch (NoSuchBucketException e) {
            log.info(">>> S3 버킷이 존재하지 않음 - bucketName: {}", bucketName);
            return false;
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            log.error(">>> S3 버킷 존재 확인 실패 - S3 서비스 오류: bucketName={}, statusCode={}, error={}",
                    bucketName, e.statusCode(), e.getMessage(), e);
            return false;
        } catch (software.amazon.awssdk.core.exception.SdkClientException e) {
            log.error(">>> S3 버킷 존재 확인 실패 - AWS SDK 클라이언트 오류: bucketName={}, error={}",
                    bucketName, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error(">>> S3 버킷 존재 확인 실패 - bucketName: {}, error: {}", bucketName, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 임시 버킷을 생성합니다.
     * 
     * @param prefix 버킷 이름 접두사
     * @return 생성된 임시 버킷 정보
     */
    public Map<String, Object> createTempBucket(String prefix) {
        // 더 고유한 이름 생성 (타임스탬프 + 랜덤 숫자)
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuidSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String tempBucketName = (prefix != null ? prefix + "-" : "temp-") + timestamp + "-" + uuidSuffix;

        log.info(">>> 임시 버킷 생성 시작 - tempBucketName: {}", tempBucketName);

        try {
            Map<String, Object> result = createBucket(tempBucketName, null);
            result.put("tempBucketName", tempBucketName);
            result.put("createdAt", java.time.LocalDateTime.now().toString());

            log.info(">>> 임시 버킷 생성 완료 - tempBucketName: {}", tempBucketName);
            return result;

        } catch (IllegalArgumentException e) {
            log.error(">>> 임시 버킷 생성 실패 - 잘못된 버킷명: tempBucketName={}, error={}", tempBucketName, e.getMessage(), e);
            throw new RuntimeException("임시 버킷 생성 실패: 잘못된 버킷명입니다.", e);
        } catch (BucketAlreadyExistsException e) {
            log.error(">>> 임시 버킷 생성 실패 - 버킷이 이미 존재함: tempBucketName={}, error={}", tempBucketName, e.getMessage(), e);
            throw new RuntimeException("임시 버킷 생성 실패: 버킷이 이미 존재합니다.", e);
        } catch (Exception e) {
            log.error(">>> 임시 버킷 생성 실패 - tempBucketName: {}, error: {}", tempBucketName, e.getMessage(), e);
            throw new RuntimeException("임시 버킷 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 버킷 내 모든 객체를 삭제합니다.
     *
     * @param bucketName 삭제할 객체가 있는 버킷 이름
     * @return 삭제 결과
     */
    public Map<String, Object> deleteObject(String bucketName) {
        log.info(">>> delete_object 실행 - bucketName: {}", bucketName);

        long deletedCount = 0L;
        try (S3Client s3Client = createS3Client()) {
            String continuationToken = null;
            boolean isTruncated;

            do {
                ListObjectsV2Request.Builder listRequestBuilder = ListObjectsV2Request.builder()
                        .bucket(bucketName);

                if (continuationToken != null) {
                    listRequestBuilder.continuationToken(continuationToken);
                }

                ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequestBuilder.build());

                if (listResponse.contents() != null && !listResponse.contents().isEmpty()) {
                    List<ObjectIdentifier> objectsToDelete = listResponse.contents().stream()
                            .map(s3Object -> ObjectIdentifier.builder()
                                    .key(s3Object.key())
                                    .build())
                            .collect(java.util.stream.Collectors.toList());

                    DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                            .bucket(bucketName)
                            .delete(Delete.builder()
                                    .objects(objectsToDelete)
                                    .build())
                            .build();

                    s3Client.deleteObjects(deleteRequest);
                    deletedCount += objectsToDelete.size();
                    log.info(">>> delete_object - bucketName: {}, 누적 삭제 객체 수: {}", bucketName, deletedCount);
                }

                continuationToken = listResponse.nextContinuationToken();
                isTruncated = listResponse.isTruncated();
            } while (isTruncated);

            Map<String, Object> result = new HashMap<>();
            result.put("bucketName", bucketName);
            result.put("deletedObjectCount", deletedCount);
            result.put("success", true);
            result.put("message", "총 " + deletedCount + "개의 객체를 삭제했습니다.");

            log.info(">>> delete_object 완료 - bucketName: {}, deletedObjectCount: {}", bucketName, deletedCount);
            return result;

        } catch (NoSuchBucketException e) {
            log.warn(">>> delete_object 실패 - 버킷이 존재하지 않음: bucketName={}", bucketName);
            Map<String, Object> result = new HashMap<>();
            result.put("bucketName", bucketName);
            result.put("success", false);
            result.put("message", "버킷이 존재하지 않습니다.");
            return result;
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            log.error(">>> delete_object 실패 - S3 서비스 오류: bucketName={}, statusCode={}, error={}",
                    bucketName, e.statusCode(), e.getMessage(), e);
            throw new RuntimeException("버킷 내 모든 객체 삭제 실패: S3 서비스 오류 (" + e.statusCode() + ")", e);
        } catch (software.amazon.awssdk.core.exception.SdkClientException e) {
            log.error(">>> delete_object 실패 - AWS SDK 클라이언트 오류: bucketName={}, error={}",
                    bucketName, e.getMessage(), e);
            throw new RuntimeException("버킷 내 모든 객체 삭제 실패: AWS SDK 클라이언트 오류입니다.", e);
        } catch (Exception e) {
            log.error(">>> delete_object 실패 - bucketName: {}, error: {}", bucketName, e.getMessage(), e);
            throw new RuntimeException("버킷 내 모든 객체 삭제 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 특정 버킷의 모든 객체 목록을 조회합니다.
     * 
     * @param bucketName 조회할 버킷 이름
     * @param prefix     객체 키 접두사 (선택사항)
     * @param maxKeys    최대 조회 개수 (선택사항, 기본값: 1000)
     * @return 버킷 내 객체 목록
     */
    public Map<String, Object> listBucketObjects(String bucketName, String prefix, Integer maxKeys) {
        log.info(">>> 버킷 객체 목록 조회 시작 - bucketName: {}, prefix: {}, maxKeys: {}",
                bucketName, prefix, maxKeys);

        try {
            // 먼저 기본 S3Client로 시도 (yaml 설정의 리전 사용)
            S3Client s3Client = createS3Client();
            try {
                ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                        .bucket(bucketName);

                if (prefix != null && !prefix.isEmpty()) {
                    requestBuilder.prefix(prefix);
                }

                if (maxKeys != null && maxKeys > 0) {
                    requestBuilder.maxKeys(maxKeys);
                } else {
                    requestBuilder.maxKeys(1000); // 기본값
                }

                ListObjectsV2Request listRequest = requestBuilder.build();
                ListObjectsV2Response response = s3Client.listObjectsV2(listRequest);

                Map<String, Object> result = new HashMap<>();
                List<Map<String, Object>> objects = new java.util.ArrayList<>();

                if (response.contents() != null) {
                    for (S3Object s3Object : response.contents()) {
                        Map<String, Object> objectInfo = new HashMap<>();
                        objectInfo.put("key", s3Object.key());
                        objectInfo.put("size", s3Object.size());
                        objectInfo.put("lastModified", s3Object.lastModified());
                        objectInfo.put("etag", s3Object.eTag());
                        objectInfo.put("storageClass", s3Object.storageClass());

                        // Owner 정보를 수동으로 구성
                        if (s3Object.owner() != null) {
                            Map<String, Object> ownerInfo = new HashMap<>();
                            ownerInfo.put("id", s3Object.owner().id());
                            ownerInfo.put("displayName", s3Object.owner().displayName());
                            objectInfo.put("owner", ownerInfo);
                        }

                        // S3 키에서 원본 파일명 추출 (구분자 | 사용)
                        String key = s3Object.key();
                        if (key != null && key.contains("__gaf__")) {
                            // 구분자 | 이후가 원본 파일명
                            String originalFileName = key.substring(key.lastIndexOf("__gaf__") + 7);
                            objectInfo.put("originalFileName", originalFileName);
                        }

                        objects.add(objectInfo);
                    }
                }

                result.put("bucketName", bucketName);
                result.put("prefix", prefix);
                result.put("objects", objects);
                result.put("totalCount", objects.size());
                result.put("isTruncated", response.isTruncated());
                result.put("nextContinuationToken", response.nextContinuationToken());

                log.info(">>> 버킷 객체 목록 조회 완료 - bucketName: {}, 총 {}개 객체", bucketName, objects.size());
                return result;

            } finally {
                s3Client.close();
            }

        } catch (NoSuchBucketException e) {
            log.error(">>> 버킷 객체 목록 조회 실패 - 버킷이 존재하지 않음: bucketName={}", bucketName);
            throw new RuntimeException("버킷이 존재하지 않습니다: " + bucketName, e);
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            log.warn(">>> 기본 S3Client로 실패, 버킷 리전 확인 후 재시도 - bucketName={}, statusCode={}, error={}",
                    bucketName, e.statusCode(), e.getMessage());

            // 기본 S3Client로 실패한 경우, 버킷의 리전을 확인하고 해당 리전의 S3Client 사용
            try {
                String bucketRegion = getBucketRegion(bucketName);
                log.info(">>> 버킷 리전 확인 완료 - bucketName: {}, region: {}", bucketName, bucketRegion);

                S3Client regionS3Client = createS3ClientForRegion(bucketRegion);
                try {
                    ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                            .bucket(bucketName);

                    if (prefix != null && !prefix.isEmpty()) {
                        requestBuilder.prefix(prefix);
                    }

                    if (maxKeys != null && maxKeys > 0) {
                        requestBuilder.maxKeys(maxKeys);
                    } else {
                        requestBuilder.maxKeys(1000); // 기본값
                    }

                    ListObjectsV2Request listRequest = requestBuilder.build();
                    ListObjectsV2Response response = regionS3Client.listObjectsV2(listRequest);

                    Map<String, Object> result = new HashMap<>();
                    List<Map<String, Object>> objects = new java.util.ArrayList<>();

                    if (response.contents() != null) {
                        for (S3Object s3Object : response.contents()) {
                            Map<String, Object> objectInfo = new HashMap<>();
                            objectInfo.put("key", s3Object.key());
                            objectInfo.put("size", s3Object.size());
                            objectInfo.put("lastModified", s3Object.lastModified());
                            objectInfo.put("etag", s3Object.eTag());
                            objectInfo.put("storageClass", s3Object.storageClass());

                            // Owner 정보를 수동으로 구성
                            if (s3Object.owner() != null) {
                                Map<String, Object> ownerInfo = new HashMap<>();
                                ownerInfo.put("id", s3Object.owner().id());
                                ownerInfo.put("displayName", s3Object.owner().displayName());
                                objectInfo.put("owner", ownerInfo);
                            }
                            objects.add(objectInfo);
                        }
                    }

                    result.put("bucketName", bucketName);
                    result.put("prefix", prefix);
                    result.put("objects", objects);
                    result.put("totalCount", objects.size());
                    result.put("isTruncated", response.isTruncated());
                    result.put("nextContinuationToken", response.nextContinuationToken());

                    log.info(">>> 버킷 객체 목록 조회 완료 (리전 재시도) - bucketName: {}, 총 {}개 객체", bucketName, objects.size());
                    return result;

                } finally {
                    regionS3Client.close();
                }

            } catch (NoSuchBucketException retryException) {
                log.error(">>> 버킷 객체 목록 조회 최종 실패 - 버킷이 존재하지 않음: bucketName={}", bucketName);
                throw new RuntimeException("버킷이 존재하지 않습니다: " + bucketName, retryException);
            } catch (software.amazon.awssdk.services.s3.model.S3Exception retryException) {
                log.error(">>> 버킷 객체 목록 조회 최종 실패 - S3 서비스 오류: bucketName={}, statusCode={}, error={}",
                        bucketName, retryException.statusCode(), retryException.getMessage(), retryException);
                throw new RuntimeException("버킷 객체 목록 조회 실패: S3 서비스 오류 (" + retryException.statusCode() + ")",
                        retryException);
            } catch (software.amazon.awssdk.core.exception.SdkClientException retryException) {
                log.error(">>> 버킷 객체 목록 조회 최종 실패 - AWS SDK 클라이언트 오류: bucketName={}, error={}",
                        bucketName, retryException.getMessage(), retryException);
                throw new RuntimeException("버킷 객체 목록 조회 실패: AWS SDK 클라이언트 오류입니다.", retryException);
            } catch (Exception retryException) {
                log.error(">>> 버킷 객체 목록 조회 최종 실패 - bucketName: {}, error: {}", bucketName, retryException.getMessage(), retryException);
                throw new RuntimeException("버킷 객체 목록 조회 중 오류가 발생했습니다: " + retryException.getMessage(), retryException);
            }
        }
    }

    /**
     * 버킷의 리전을 조회합니다.
     * 
     * @param bucketName 조회할 버킷 이름
     * @return 버킷의 리전
     */
    private String getBucketRegion(String bucketName) {
        log.info(">>> 버킷 리전 조회 시작 - bucketName: {}", bucketName);

        try (S3Client s3Client = createS3Client()) {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            HeadBucketResponse response = s3Client.headBucket(headBucketRequest);
            String region = response.bucketRegion();

            log.info(">>> 버킷 리전 조회 완료 - bucketName: {}, region: {}", bucketName, region);
            return region;

        } catch (NoSuchBucketException e) {
            log.warn(">>> 버킷 리전 조회 실패 - 버킷이 존재하지 않음: bucketName={}, 기본 리전 반환", bucketName);
            return "us-east-1";
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            log.error(">>> 버킷 리전 조회 실패 - S3 서비스 오류: bucketName={}, statusCode={}, 기본 리전 반환",
                    bucketName, e.statusCode());
            return "us-east-1";
        } catch (software.amazon.awssdk.core.exception.SdkClientException e) {
            log.error(">>> 버킷 리전 조회 실패 - AWS SDK 클라이언트 오류: bucketName={}, 기본 리전 반환", bucketName);
            return "us-east-1";
        } catch (Exception e) {
            log.error(">>> 버킷 리전 조회 실패 - bucketName: {}, error: {}", bucketName, e.getMessage(), e);
            // 기본 리전으로 fallback
            return "us-east-1";
        }
    }

    /**
     * 특정 리전의 S3Client를 생성합니다.
     * 
     * @param region 리전
     * @return S3Client
     */
    private S3Client createS3ClientForRegion(String region) {
        log.info(">>> 특정 리전 S3Client 생성 - region: {}", region);

        try {
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                    s3Config.getAccessKey(),
                    s3Config.getSecretKey());

            S3Configuration s3Configuration = S3Configuration.builder()
                    .pathStyleAccessEnabled(s3Config.isPathStyleAccess())
                    .build();

            return S3Client.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                    .region(Region.of(region))
                    .serviceConfiguration(s3Configuration)
                    .build();

        } catch (IllegalArgumentException e) {
            log.error(">>> 특정 리전 S3Client 생성 실패 - 잘못된 리전: region={}, error={}", region, e.getMessage(), e);
            throw new RuntimeException("특정 리전 S3Client 생성 실패: 잘못된 리전입니다.", e);
        } catch (NullPointerException e) {
            log.error(">>> 특정 리전 S3Client 생성 실패 - 필수 설정값 누락: region={}, error={}", region, e.getMessage(), e);
            throw new RuntimeException("특정 리전 S3Client 생성 실패: 필수 설정값이 누락되었습니다.", e);
        } catch (SecurityException e) {
            log.error(">>> 특정 리전 S3Client 생성 실패 - 보안 설정 오류: region={}, error={}", region, e.getMessage(), e);
            throw new RuntimeException("특정 리전 S3Client 생성 실패: 보안 설정 오류입니다.", e);
        } catch (Exception e) {
            log.error(">>> 특정 리전 S3Client 생성 실패 - region: {}, error: {}", region, e.getMessage(), e);
            throw new RuntimeException("특정 리전 S3Client 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 파일명으로 S3에서 파일을 찾아 다운로드합니다.
     * 
     * <p>
     * 파일명은 __gaf__ 구분자 뒤의 원본 파일명으로 검색합니다.
     * 예: test.zip 파일을 찾기 위해 S3 키에서 __gaf__test.zip 패턴을 검색합니다.
     * </p>
     * 
     * @param fileName   검색할 파일명 (예: test.zip) 또는 S3 키 (s3Key가 null이 아닌 경우)
     * @param bucketName 버킷 이름 (null이면 기본 버킷 사용)
     * @param targetFile 다운로드할 대상 파일
     * @param s3Key      S3 키 (직접 지정 시, null이면 파일명으로 검색)
     * @return 다운로드 결과 정보
     */
    public Map<String, Object> downloadFileByFileName(String fileName, String bucketName, File targetFile,
            String s3Key) {
        log.info(">>> S3 파일 다운로드 시작 - fileName: {}, bucketName: {}, s3Key: {}, targetFile: {}",
                fileName, bucketName, s3Key, targetFile.getAbsolutePath());

        try (S3Client s3Client = createS3Client()) {
            String searchBucket = bucketName != null ? bucketName : s3Config.getBucketName();

            String foundS3Key;
            // S3 키가 직접 제공된 경우 검색 생략
            if (s3Key != null && !s3Key.trim().isEmpty()) {
                foundS3Key = s3Key;
                log.info(">>> S3 키 직접 사용 - s3Key: {}", foundS3Key);
            } else {
                // S3에서 파일명으로 검색 (__gaf__ 패턴 사용)
                foundS3Key = findS3KeyByFileName(fileName, searchBucket, s3Client);

                if (foundS3Key == null) {
                    throw new RuntimeException("파일을 찾을 수 없습니다: " + fileName);
                }

                log.info(">>> 파일 발견 - fileName: {}, s3Key: {}", fileName, foundS3Key);
            }

            // 파일 다운로드 (스트리밍 방식)
            software.amazon.awssdk.services.s3.model.GetObjectRequest getObjectRequest = software.amazon.awssdk.services.s3.model.GetObjectRequest
                    .builder()
                    .bucket(searchBucket)
                    .key(foundS3Key)
                    .build();

            // 대상 파일의 디렉토리가 없으면 생성
            File parentDir = targetFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // 스트리밍 방식으로 다운로드
            try (java.io.FileOutputStream outputStream = new java.io.FileOutputStream(targetFile)) {
                s3Client.getObject(getObjectRequest,
                        software.amazon.awssdk.core.sync.ResponseTransformer.toOutputStream(outputStream));
            }

            long fileSize = targetFile.length();

            // 파일명 추출 (__gaf__ 기준)
            String actualFileName = fileName;
            if (foundS3Key != null && foundS3Key.contains("__gaf__")) {
                actualFileName = foundS3Key.substring(foundS3Key.lastIndexOf("__gaf__") + 7);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("fileName", actualFileName);
            result.put("s3Key", foundS3Key);
            result.put("bucketName", searchBucket);
            result.put("targetFilePath", targetFile.getAbsolutePath());
            result.put("fileSize", fileSize);
            result.put("success", true);

            log.info(">>> S3 파일 다운로드 완료 - fileName: {}, s3Key: {}, fileSize: {} bytes",
                    actualFileName, foundS3Key, fileSize);
            return result;

        } catch (NoSuchBucketException e) {
            log.error(">>> S3 파일 다운로드 실패 - 버킷 없음: bucketName: {}, error: {}", bucketName, e.getMessage(), e);
            throw new RuntimeException("S3 파일 다운로드 실패: 버킷이 존재하지 않습니다.", e);
        } catch (software.amazon.awssdk.services.s3.model.NoSuchKeyException e) {
            log.error(">>> S3 파일 다운로드 실패 - 객체 없음: fileName: {}, s3Key: {}, error: {}", fileName, s3Key, e.getMessage(),
                    e);
            throw new RuntimeException("S3 파일 다운로드 실패: 지정한 객체를 찾을 수 없습니다.", e);
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            log.error(">>> S3 파일 다운로드 실패 - S3 서비스 오류: statusCode={}, error={}", e.statusCode(), e.getMessage(), e);
            throw new RuntimeException("S3 파일 다운로드 실패: S3 서비스 오류 (" + e.statusCode() + ")", e);
        } catch (software.amazon.awssdk.core.exception.SdkClientException e) {
            log.error(">>> S3 파일 다운로드 실패 - AWS SDK 클라이언트 오류: error={}", e.getMessage(), e);
            throw new RuntimeException("S3 파일 다운로드 실패: AWS SDK 클라이언트 오류입니다.", e);
        } catch (java.io.IOException e) {
            log.error(">>> S3 파일 다운로드 실패 - 로컬 파일 처리 오류: targetFile={}, error={}", targetFile.getAbsolutePath(),
                    e.getMessage(), e);
            throw new RuntimeException("S3 파일 다운로드 실패: 로컬 파일 처리 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            log.error(">>> S3 파일 다운로드 실패 - fileName: {}, s3Key: {}, error: {}", fileName, s3Key, e.getMessage(), e);
            throw new RuntimeException("S3 파일 다운로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 파일명으로 S3에서 파일을 찾아 다운로드합니다.
     *
     * <p>
     * 파일명은 __gaf__ 구분자 뒤의 원본 파일명으로 검색합니다.
     * 예: test.zip 파일을 찾기 위해 S3 키에서 __gaf__test.zip 패턴을 검색합니다.
     * </p>
     *
     * @param fileName   검색할 파일명 (예: test.zip)
     * @param bucketName 버킷 이름 (null이면 기본 버킷 사용)
     * @param targetFile 다운로드할 대상 파일
     * @return 다운로드 결과 정보
     */
    public Map<String, Object> downloadFileByFileName(String fileName, String bucketName, File targetFile) {
        return downloadFileByFileName(fileName, bucketName, targetFile, null);
    }

    /**
     * 파일명으로 S3 키를 찾습니다.
     * 
     * @param fileName   검색할 파일명
     * @param bucketName 버킷 이름
     * @param s3Client   S3Client 인스턴스
     * @return 찾은 S3 키 (없으면 null)
     */
    private String findS3KeyByFileName(String fileName, String bucketName, S3Client s3Client) {
        log.info(">>> S3 키 검색 시작 - fileName: {}, bucketName: {}", fileName, bucketName);

        try {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(listRequest);

            if (response.contents() != null) {
                for (S3Object s3Object : response.contents()) {
                    String key = s3Object.key();

                    // __gaf__ 구분자 뒤의 파일명 추출
                    if (key != null && key.contains("__gaf__")) {
                        String originalFileName = key.substring(key.lastIndexOf("__gaf__") + 7);

                        // 파일명 비교 (정규화)
                        if (originalFileName.equals(fileName)) {
                            log.info(">>> 파일명으로 S3 키 발견 - fileName: {}, s3Key: {}", fileName, key);
                            return key;
                        }
                    }
                }
            }

            log.warn(">>> 파일명으로 S3 키를 찾을 수 없음 - fileName: {}", fileName);
            return null;

        } catch (NoSuchBucketException e) {
            log.error(">>> S3 키 검색 실패 - 버킷이 존재하지 않음: fileName={}, bucketName={}", fileName, bucketName);
            throw new RuntimeException("버킷이 존재하지 않습니다: " + bucketName, e);
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            log.error(">>> S3 키 검색 실패 - S3 서비스 오류: fileName={}, statusCode={}, error={}",
                    fileName, e.statusCode(), e.getMessage(), e);
            throw new RuntimeException("S3 키 검색 실패: S3 서비스 오류 (" + e.statusCode() + ")", e);
        } catch (software.amazon.awssdk.core.exception.SdkClientException e) {
            log.error(">>> S3 키 검색 실패 - AWS SDK 클라이언트 오류: fileName={}, error={}", fileName, e.getMessage(), e);
            throw new RuntimeException("S3 키 검색 실패: AWS SDK 클라이언트 오류입니다.", e);
        } catch (Exception e) {
            log.error(">>> S3 키 검색 실패 - fileName: {}, error: {}", fileName, e.getMessage(), e);
            throw new RuntimeException("S3 키 검색 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * S3 객체를 다른 버킷으로 복사합니다.
     * 
     * @param sourceBucketName 소스 버킷 이름
     * @param sourceKey        소스 객체 키
     * @param targetBucketName 대상 버킷 이름
     * @param targetKey        대상 객체 키
     * @return 복사 결과 정보
     */
    public Map<String, Object> copyObject(String sourceBucketName, String sourceKey,
            String targetBucketName, String targetKey) {
        log.info(">>> S3 객체 복사 시작 - sourceBucket: {}, sourceKey: {}, targetBucket: {}, targetKey: {}",
                sourceBucketName, sourceKey, targetBucketName, targetKey);

        try (S3Client s3Client = createS3Client()) {
            // CopyObjectRequest 생성 (sourceBucket과 sourceKey를 별도로 지정)
            CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                    .sourceBucket(sourceBucketName)
                    .sourceKey(sourceKey)
                    .destinationBucket(targetBucketName)
                    .destinationKey(targetKey)
                    .build();

            // 객체 복사 실행
            CopyObjectResponse response = s3Client.copyObject(copyObjectRequest);

            Map<String, Object> result = new HashMap<>();
            result.put("sourceBucket", sourceBucketName);
            result.put("sourceKey", sourceKey);
            result.put("targetBucket", targetBucketName);
            result.put("targetKey", targetKey);
            result.put("etag", response.copyObjectResult() != null ? response.copyObjectResult().eTag() : null);
            result.put("versionId", response.versionId());
            result.put("success", true);

            log.info(">>> S3 객체 복사 완료 - sourceBucket: {}, sourceKey: {}, targetBucket: {}, targetKey: {}, etag: {}",
                    sourceBucketName, sourceKey, targetBucketName, targetKey,
                    response.copyObjectResult() != null ? response.copyObjectResult().eTag() : null);
            return result;

        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            log.error(
                    ">>> S3 객체 복사 실패 - S3 서비스 오류: sourceBucket={}, sourceKey={}, targetBucket={}, targetKey={}, statusCode={}, error={}",
                    sourceBucketName, sourceKey, targetBucketName, targetKey, e.statusCode(), e.getMessage(), e);
            throw new RuntimeException("S3 객체 복사 실패: S3 서비스 오류 (" + e.statusCode() + ")", e);
        } catch (software.amazon.awssdk.core.exception.SdkClientException e) {
            log.error(
                    ">>> S3 객체 복사 실패 - AWS SDK 클라이언트 오류: sourceBucket={}, sourceKey={}, targetBucket={}, targetKey={}, error={}",
                    sourceBucketName, sourceKey, targetBucketName, targetKey, e.getMessage(), e);
            throw new RuntimeException("S3 객체 복사 실패: AWS SDK 클라이언트 오류입니다.", e);
        } catch (Exception e) {
            log.error(">>> S3 객체 복사 실패 - sourceBucket: {}, sourceKey: {}, targetBucket: {}, targetKey: {}, error: {}",
                    sourceBucketName, sourceKey, targetBucketName, targetKey, e.getMessage(), e);
            throw new RuntimeException("S3 객체 복사 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
