package com.skax.aiplatform.dto.admin.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.skax.aiplatform.entity.NoticeFile;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeFileRes {
    
    private Long fileId;
    private String originalFilename;
    private String storedFilename;
    private String fileSize;
    private String contentType;
    private LocalDateTime uploadDate;
    private String useYn;
    
    // ==================== 값 변환 메서드 ====================
    
    /**
     * Y/N 문자열을 숫자로 변환 (Y -> 1, N -> 0)
     */
    public static Integer convertStringToNumber(String useYn) {
        if (useYn == null) {
            return null;
        }
        return "Y".equalsIgnoreCase(useYn) ? 1 : 0;
    }
    
    /**
     * 숫자를 Y/N 문자열로 변환 (1 -> Y, 0 -> N)
     */
    public static String convertNumberToString(Integer useYn) {
        if (useYn == null) {
            return null;
        }
        return useYn == 1 ? "Y" : "N";
    }
    
    /**
     * NoticeFile 엔티티에서 NoticeFileRes로 변환
     */
    public static NoticeFileRes fromEntity(NoticeFile entity) {
        if (entity == null) {
            return null;
        }
        
        return NoticeFileRes.builder()
                .fileId(entity.getFileId())
                .originalFilename(entity.getOriginalFilename())
                .storedFilename(entity.getStoredFilename())
                .fileSize(entity.getFileSize())
                .contentType(entity.getContentType())
                .uploadDate(entity.getUploadDate())
                .useYn(convertNumberToString(entity.getUseYn()))
                .build();
    }
}

