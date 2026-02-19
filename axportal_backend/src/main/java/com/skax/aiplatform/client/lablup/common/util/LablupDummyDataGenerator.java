package com.skax.aiplatform.client.lablup.common.util;

import com.skax.aiplatform.client.lablup.api.dto.response.ArtifactListResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Lablup API 더미 데이터 생성기
 * 
 * <p>Lablup API 응답에 대한 더미 데이터를 생성하는 유틸리티 클래스입니다.
 * 개발 및 테스트 목적으로 사용됩니다.</p>
 * 
 * @author 김예리
 * @since 2025-01-27
 * @version 1.0
 */
@Slf4j
public class LablupDummyDataGenerator {
    
    /**
     * 아티팩트 목록 더미 데이터 생성
     * 
     * @return 아티팩트 목록 응답 더미 데이터
     */
    public static ArtifactListResponse generateArtifactListResponse() {
        log.debug("🔍 [LABLUP DUMMY] 아티팩트 목록 더미 데이터 생성 시작");
        
        ArtifactListResponse response = ArtifactListResponse.builder()
            .artifacts(createArtifacts())
            .build();
        
        log.debug("✅ [LABLUP DUMMY] 아티팩트 목록 더미 데이터 생성 완료 - 아티팩트 수: {}", 
                  response.getArtifacts().size());
        
        return response;
    }
    
    /**
     * 아티팩트 목록 생성
     * 
     * @return 아티팩트 목록
     */
    private static List<ArtifactListResponse.Artifact> createArtifacts() {
        return Arrays.asList(
            createHuggingFaceModel(),
            createReservoirPackage(),
            createCustomImage(),
            createLargeModel(),
            createSmallUtility(),
            createTransformerModel(), // 리비전 3개
            createPyTorchModel()     // 리비전 3개
        );
    }
    
    /**
     * 허깅페이스 모델 아티팩트 생성
     */
    private static ArtifactListResponse.Artifact createHuggingFaceModel() {
        return ArtifactListResponse.Artifact.builder()
            .id(UUID.randomUUID().toString())
            .name("bert-base-uncased")
            .type("MODEL")
            .description("None") // 허깅페이스의 경우 None
            .registryId(UUID.randomUUID().toString())
            .sourceRegistryId(UUID.randomUUID().toString())
            .registryType("huggingface")
            .sourceRegistryType("huggingface")
            .scannedAt(LocalDateTime.now().minusHours(2))
            .updatedAt(LocalDateTime.now().minusMinutes(30))
            .readonly(true)
            .revisions(createHuggingFaceRevisions())
            .build();
    }
    
    /**
     * 레저버 패키지 아티팩트 생성
     */
    private static ArtifactListResponse.Artifact createReservoirPackage() {
        return ArtifactListResponse.Artifact.builder()
            .id(UUID.randomUUID().toString())
            .name("tensorflow-2.15.0")
            .type("PACKAGE")
            .description("TensorFlow 2.15.0 machine learning framework package")
            .registryId(UUID.randomUUID().toString())
            .sourceRegistryId(UUID.randomUUID().toString())
            .registryType("reservoir")
            .sourceRegistryType("reservoir")
            .scannedAt(LocalDateTime.now().minusDays(1))
            .updatedAt(LocalDateTime.now().minusHours(3))
            .readonly(false)
            .revisions(createReservoirRevisions())
            .build();
    }
    
    /**
     * 커스텀 이미지 아티팩트 생성
     */
    private static ArtifactListResponse.Artifact createCustomImage() {
        return ArtifactListResponse.Artifact.builder()
            .id(UUID.randomUUID().toString())
            .name("custom-pytorch-image")
            .type("IMAGE")
            .description("Custom PyTorch image with CUDA support")
            .registryId(UUID.randomUUID().toString())
            .sourceRegistryId(UUID.randomUUID().toString())
            .registryType("reservoir")
            .sourceRegistryType("docker")
            .scannedAt(LocalDateTime.now().minusDays(3))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .readonly(false)
            .revisions(createCustomImageRevisions())
            .build();
    }
    
    /**
     * 대형 모델 아티팩트 생성
     */
    private static ArtifactListResponse.Artifact createLargeModel() {
        return ArtifactListResponse.Artifact.builder()
            .id(UUID.randomUUID().toString())
            .name("gpt-3.5-turbo")
            .type("MODEL")
            .description("GPT-3.5 Turbo language model")
            .registryId(UUID.randomUUID().toString())
            .sourceRegistryId(UUID.randomUUID().toString())
            .registryType("huggingface")
            .sourceRegistryType("huggingface")
            .scannedAt(LocalDateTime.now().minusDays(7))
            .updatedAt(LocalDateTime.now().minusDays(2))
            .readonly(true)
            .revisions(createLargeModelRevisions())
            .build();
    }
    
    /**
     * 소형 유틸리티 아티팩트 생성
     */
    private static ArtifactListResponse.Artifact createSmallUtility() {
        return ArtifactListResponse.Artifact.builder()
            .id(UUID.randomUUID().toString())
            .name("numpy-1.24.0")
            .type("PACKAGE")
            .description("NumPy 1.24.0 numerical computing library")
            .registryId(UUID.randomUUID().toString())
            .sourceRegistryId(UUID.randomUUID().toString())
            .registryType("reservoir")
            .sourceRegistryType("pypi")
            .scannedAt(LocalDateTime.now().minusHours(12))
            .updatedAt(LocalDateTime.now().minusHours(1))
            .readonly(false)
            .revisions(createSmallUtilityRevisions())
            .build();
    }
    
    /**
     * 허깅페이스 모델 리비전 생성
     */
    private static List<ArtifactListResponse.Revision> createHuggingFaceRevisions() {
        return Arrays.asList(
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("1.0.0")
                .size(440_000_000L) // 440MB
                .status("SCANNED")
                .createdAt(LocalDateTime.now().minusHours(2))
                .updatedAt(LocalDateTime.now().minusMinutes(30))
                .build(),
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("1.1.0")
                .size(450_000_000L) // 450MB
                .status("PULLING")
                .createdAt(LocalDateTime.now().minusHours(1))
                .updatedAt(LocalDateTime.now().minusMinutes(15))
                .build()
        );
    }
    
    /**
     * 레저버 패키지 리비전 생성
     */
    private static List<ArtifactListResponse.Revision> createReservoirRevisions() {
        return Arrays.asList(
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("2.15.0")
                .size(1_200_000_000L) // 1.2GB
                .status("PULLED")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(3))
                .build(),
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("2.14.0")
                .size(1_150_000_000L) // 1.15GB
                .status("SCANNED")
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build()
        );
    }
    
    /**
     * 커스텀 이미지 리비전 생성
     */
    private static List<ArtifactListResponse.Revision> createCustomImageRevisions() {
        return Arrays.asList(
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("latest")
                .size(2_500_000_000L) // 2.5GB
                .status("PULLED")
                .createdAt(LocalDateTime.now().minusDays(3))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build(),
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("v1.0.0")
                .size(2_300_000_000L) // 2.3GB
                .status("SCANNED")
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now().minusDays(3))
                .build()
        );
    }
    
    /**
     * 대형 모델 리비전 생성
     */
    private static List<ArtifactListResponse.Revision> createLargeModelRevisions() {
        return Arrays.asList(
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("3.5-turbo")
                .size(15_000_000_000L) // 15GB
                .status("PULLED")
                .createdAt(LocalDateTime.now().minusDays(7))
                .updatedAt(LocalDateTime.now().minusDays(2))
                .build(),
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("3.5-turbo-16k")
                .size(18_000_000_000L) // 18GB
                .status("PULLING")
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build()
        );
    }
    
    /**
     * 소형 유틸리티 리비전 생성
     */
    private static List<ArtifactListResponse.Revision> createSmallUtilityRevisions() {
        return Arrays.asList(
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("1.24.0")
                .size(25_000_000L) // 25MB
                .status("SCANNED")
                .createdAt(LocalDateTime.now().minusHours(12))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build(),
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("1.23.0")
                .size(24_000_000L) // 24MB
                .status("PULLED")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(12))
                .build()
        );
    }
    
    /**
     * 트랜스포머 모델 아티팩트 생성 (리비전 3개)
     */
    private static ArtifactListResponse.Artifact createTransformerModel() {
        return ArtifactListResponse.Artifact.builder()
            .id(UUID.randomUUID().toString())
            .name("transformer-4.0")
            .type("MODEL")
            .description("Advanced transformer model for natural language processing")
            .registryId(UUID.randomUUID().toString())
            .sourceRegistryId(UUID.randomUUID().toString())
            .registryType("huggingface")
            .sourceRegistryType("huggingface")
            .scannedAt(LocalDateTime.now().minusDays(5))
            .updatedAt(LocalDateTime.now().minusDays(1))
            .readonly(true)
            .revisions(createTransformerRevisions())
            .build();
    }
    
    /**
     * PyTorch 모델 아티팩트 생성 (리비전 3개)
     */
    private static ArtifactListResponse.Artifact createPyTorchModel() {
        return ArtifactListResponse.Artifact.builder()
            .id(UUID.randomUUID().toString())
            .name("pytorch-vision-model")
            .type("MODEL")
            .description("PyTorch computer vision model with pre-trained weights")
            .registryId(UUID.randomUUID().toString())
            .sourceRegistryId(UUID.randomUUID().toString())
            .registryType("reservoir")
            .sourceRegistryType("pytorch")
            .scannedAt(LocalDateTime.now().minusDays(10))
            .updatedAt(LocalDateTime.now().minusDays(3))
            .readonly(false)
            .revisions(createPyTorchRevisions())
            .build();
    }
    
    /**
     * 트랜스포머 모델 리비전 생성 (3개)
     */
    private static List<ArtifactListResponse.Revision> createTransformerRevisions() {
        return Arrays.asList(
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("4.0.0")
                .size(2_800_000_000L) // 2.8GB
                .status("PULLED")
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build(),
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("3.9.0")
                .size(2_600_000_000L) // 2.6GB
                .status("SCANNED")
                .createdAt(LocalDateTime.now().minusDays(8))
                .updatedAt(LocalDateTime.now().minusDays(3))
                .build(),
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("3.8.0")
                .size(2_400_000_000L) // 2.4GB
                .status("PULLING")
                .createdAt(LocalDateTime.now().minusDays(12))
                .updatedAt(LocalDateTime.now().minusDays(5))
                .build()
        );
    }
    
    /**
     * PyTorch 모델 리비전 생성 (3개)
     */
    private static List<ArtifactListResponse.Revision> createPyTorchRevisions() {
        return Arrays.asList(
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("1.12.0")
                .size(1_500_000_000L) // 1.5GB
                .status("PULLED")
                .createdAt(LocalDateTime.now().minusDays(10))
                .updatedAt(LocalDateTime.now().minusDays(3))
                .build(),
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("1.11.0")
                .size(1_400_000_000L) // 1.4GB
                .status("SCANNED")
                .createdAt(LocalDateTime.now().minusDays(15))
                .updatedAt(LocalDateTime.now().minusDays(8))
                .build(),
            ArtifactListResponse.Revision.builder()
                .id(UUID.randomUUID().toString())
                .artifactId(UUID.randomUUID().toString())
                .version("1.10.0")
                .size(1_300_000_000L) // 1.3GB
                .status("PULLED")
                .createdAt(LocalDateTime.now().minusDays(20))
                .updatedAt(LocalDateTime.now().minusDays(12))
                .build()
        );
    }
    
    /**
     * JSON 형태의 더미 데이터 생성
     * 
     * @return JSON 형태의 더미 데이터 문자열
     */
    public static String generateArtifactListJson() {
        log.debug("🔍 [LABLUP DUMMY] JSON 형태 아티팩트 목록 더미 데이터 생성 시작");
        
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"artifacts\": [\n");
        
        // 허깅페이스 모델
        json.append("    {\n");
        json.append("      \"id\": \"").append(UUID.randomUUID().toString()).append("\",\n");
        json.append("      \"name\": \"bert-base-uncased\",\n");
        json.append("      \"type\": \"MODEL\",\n");
        json.append("      \"description\": \"None\",\n");
        json.append("      \"registry_id\": \"").append(UUID.randomUUID().toString()).append("\",\n");
        json.append("      \"source_registry_id\": \"").append(UUID.randomUUID().toString()).append("\",\n");
        json.append("      \"registry_type\": \"huggingface\",\n");
        json.append("      \"source_registry_type\": \"huggingface\",\n");
        json.append("      \"scanned_at\": \"").append(LocalDateTime.now().minusHours(2)).append("\",\n");
        json.append("      \"updated_at\": \"").append(LocalDateTime.now().minusMinutes(30)).append("\",\n");
        json.append("      \"readonly\": true,\n");
        json.append("      \"revisions\": [\n");
        json.append("        {\n");
        json.append("          \"id\": \"").append(UUID.randomUUID().toString()).append("\",\n");
        json.append("          \"artifact_id\": \"").append(UUID.randomUUID().toString()).append("\",\n");
        json.append("          \"version\": \"1.0.0\",\n");
        json.append("          \"size\": 440000000,\n");
        json.append("          \"status\": \"SCANNED\",\n");
        json.append("          \"created_at\": \"").append(LocalDateTime.now().minusHours(2)).append("\",\n");
        json.append("          \"updated_at\": \"").append(LocalDateTime.now().minusMinutes(30)).append("\"\n");
        json.append("        }\n");
        json.append("      ]\n");
        json.append("    },\n");
        
        // 레저버 패키지
        json.append("    {\n");
        json.append("      \"id\": \"").append(UUID.randomUUID().toString()).append("\",\n");
        json.append("      \"name\": \"tensorflow-2.15.0\",\n");
        json.append("      \"type\": \"PACKAGE\",\n");
        json.append("      \"description\": \"TensorFlow 2.15.0 machine learning framework package\",\n");
        json.append("      \"registry_id\": \"").append(UUID.randomUUID().toString()).append("\",\n");
        json.append("      \"source_registry_id\": \"").append(UUID.randomUUID().toString()).append("\",\n");
        json.append("      \"registry_type\": \"reservoir\",\n");
        json.append("      \"source_registry_type\": \"reservoir\",\n");
        json.append("      \"scanned_at\": \"").append(LocalDateTime.now().minusDays(1)).append("\",\n");
        json.append("      \"updated_at\": \"").append(LocalDateTime.now().minusHours(3)).append("\",\n");
        json.append("      \"readonly\": false,\n");
        json.append("      \"revisions\": [\n");
        json.append("        {\n");
        json.append("          \"id\": \"").append(UUID.randomUUID().toString()).append("\",\n");
        json.append("          \"artifact_id\": \"").append(UUID.randomUUID().toString()).append("\",\n");
        json.append("          \"version\": \"2.15.0\",\n");
        json.append("          \"size\": 1200000000,\n");
        json.append("          \"status\": \"PULLED\",\n");
        json.append("          \"created_at\": \"").append(LocalDateTime.now().minusDays(1)).append("\",\n");
        json.append("          \"updated_at\": \"").append(LocalDateTime.now().minusHours(3)).append("\"\n");
        json.append("        }\n");
        json.append("      ]\n");
        json.append("    }\n");
        
        json.append("  ]\n");
        json.append("}\n");
        
        log.debug("✅ [LABLUP DUMMY] JSON 형태 아티팩트 목록 더미 데이터 생성 완료");
        
        return json.toString();
    }
}
