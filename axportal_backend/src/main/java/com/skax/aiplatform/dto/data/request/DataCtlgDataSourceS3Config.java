package com.skax.aiplatform.dto.data.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "S3 설정")
public class DataCtlgDataSourceS3Config {

    @Schema(description = "S3 버킷 이름", example = "my-bucket")
    private String bucketName;

    @Schema(description = "S3 액세스 키", example = "AKIAIOSFODNN7EXAMPLE")
    private String accessKey;

    @Schema(description = "S3 시크릿 키", example = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY")
    private String secretKey;

    @Schema(description = "S3 리전", example = "us-east-1")
    private String region;

    @Schema(description = "S3 프리픽스", example = "data/")
    private String prefix;

    @Schema(description = "S3 엔드포인트", example = "https://s3.ap-northeast-2.amazonaws.com")
    private String endpoint;
}
