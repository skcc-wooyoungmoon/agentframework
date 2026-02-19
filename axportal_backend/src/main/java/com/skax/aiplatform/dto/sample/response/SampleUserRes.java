package com.skax.aiplatform.dto.sample.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 샘플 사용자 응답 DTO
 * 
 * <p>샘플 사용자 정보를 클라이언트에 반환할 때 사용되는 응답 데이터입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-03
 * @version 2.0.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "샘플 사용자 응답")
public class SampleUserRes {
    
    @Schema(description = "사용자 ID", example = "1")
    private Long id;
    
    @Schema(description = "사용자명", example = "john_doe")
    private String username;
    
    @Schema(description = "이메일 주소", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "전체 이름", example = "홍길동")
    private String fullName;
    
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phoneNumber;
    
    @Schema(description = "활성화 여부", example = "true")
    private Boolean isActive;
    
    @Schema(description = "부서", example = "개발팀")
    private String department;
    
    @Schema(description = "직급", example = "시니어 개발자")
    private String position;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "생성일시", example = "2025-08-03 10:30:00")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "수정일시", example = "2025-08-03 15:45:00")
    private LocalDateTime updatedAt;
    
    @Schema(description = "생성자", example = "admin")
    private String createdBy;
    
    @Schema(description = "수정자", example = "admin")
    private String updatedBy;
}
