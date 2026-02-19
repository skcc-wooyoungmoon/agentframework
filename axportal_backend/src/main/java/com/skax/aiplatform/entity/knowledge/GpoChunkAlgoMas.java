package com.skax.aiplatform.entity.knowledge;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * GPO 청킹 알고리즘 마스터 엔티티
 * 
 * <p>
 * 청킹 알고리즘 정보를 관리하는 엔티티입니다.
 * 기존 gpo_chunk_mas 테이블을 대체합니다.
 * </p>
 * 
 * @author system
 * @since 2025-10-17
 * @version 3.0.0
 */
@Entity
@Table(name = "gpo_chunk_algo_mas")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GpoChunkAlgoMas {

    /**
     * 알고리즘ID (PK)
     */
    @Id
    @Column(name = "algo_id", length = 100, nullable = false)
    private String algoId;

    /**
     * 알고리즘명
     */
    @Column(name = "algo_nm", length = 100, nullable = false)
    private String algoNm;

    /**
     * 파라미터 내용
     */
    @Column(name = "prmt_ctnt", length = 4000, nullable = false)
    private String prmtCtnt;

    /**
     * 상세 내용
     */
    @Column(name = "dtl_ctnt", length = 4000, nullable = false)
    private String dtlCtnt;

    /**
     * 삭제 여부 (0: 정상, 1: 삭제)
     */
    @Column(name = "del_yn", nullable = false)
    private Integer delYn;

    /**
     * 최초 생성일시
     */
    @Column(name = "fst_created_at", nullable = false)
    private LocalDateTime fstCreatedAt;

    /**
     * 생성자
     */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    /**
     * 최종 수정일시
     */
    @Column(name = "lst_updated_at", nullable = false)
    private LocalDateTime lstUpdatedAt;

    /**
     * 수정자
     */
    @Column(name = "updated_by", length = 50, nullable = false)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (fstCreatedAt == null) {
            fstCreatedAt = now;
        }
        lstUpdatedAt = now;
        if (createdBy == null) {
            createdBy = "system";
        }
        if (delYn == null) {
            delYn = 0; // 0: 정상
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lstUpdatedAt = LocalDateTime.now();
    }
}

