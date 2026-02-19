package com.skax.aiplatform.entity.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * GPO 모델 임베딩 원장 엔티티
 * 
 * <p>
 * 모델 임베딩 정보를 관리하는 마스터 테이블입니다.
 * </p>
 * 
 * @author system
 * @since 2025-01-XX
 * @version 1.0
 */
@Entity
@Table(name = "gpo_model_embedding_mas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpoModelEmbeddingMas {

    /**
     * 모델명 (Primary Key)
     */
    @Id
    @Column(name = "model_nm", nullable = false)
    private String modelNm;

    /**
     * 파라미터 내용
     * 모델 파라미터 내용을 JSON 형식으로 저장
     * ex) {"dimension": 2048, ...}
     */
    @Column(name = "prmt_ctnt")
    private String prmtCtnt;

    /**
     * 삭제 여부
     */
    @Column(name = "del_yn")
    private Integer delYn;

    /**
     * 최초 생성일시
     */
    @Column(name = "fst_created_at")
    private LocalDate fstCreatedAt;

    /**
     * 생성자
     */
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    /**
     * 최종 수정일시
     */
    @Column(name = "lst_updated_at")
    private LocalDate lstUpdatedAt;

    /**
     * 수정자
     */
    @Column(name = "updated_by")
    private String updatedBy;
}

