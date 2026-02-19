package com.skax.aiplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "gpo_notice_files_mas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeFile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_attch_file_seq")
    @SequenceGenerator(
            name = "s_attch_file_seq",
            sequenceName = "S_ATTCH_FILE_SEQ",
            allocationSize = 1
    )
    @Column(name = "attch_file_seq")
    private Long fileId;
    
    @Column(name = "noti_seq", nullable = false)
    private Long noticeId;
    
    @Column(name = "gpo_attch_file_nm", nullable = false, length = 300)
    private String originalFilename;
    
    @Column(name = "attch_file_id", nullable = false, length = 100)
    private String storedFilename;
    
    @Column(name = "attch_file_size_v", length = 100)
    private String fileSize;
    
    @Column(name = "attch_file_u_inf", length = 100)
    private String contentType;
    
    @Column(name = "attch_file_path", length = 1000)
    private String filePath;
    
    @Column(name = "lst_updated_at")
    private LocalDateTime uploadDate;
    
    @Column(name = "usyn")
    private Integer useYn; // 상태 (0: N, 1: Y)
    
    @Column(name = "updated_by")
    private LocalDateTime updatedBy;
    
    @Column(name = "fst_created_at")
    private LocalDateTime fstCreatedAt;
    
    @Column(name = "created_by")
    private LocalDateTime createdBy;
    
    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDateTime.now();
        fstCreatedAt = LocalDateTime.now();
        if (useYn == null) {
            useYn = 1; // 기본값: 1 (Y)
        }
    }
}
