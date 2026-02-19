package com.skax.aiplatform.entity.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.skax.aiplatform.enums.ModelDoipCtntType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * GPO 모델 도입 내용 원장
 */
@Entity
@Table(name = "GPO_MODEL_DOIP_CTNT_MAS")
@Getter
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
public class GpoModelDoipCtntMas {

    /**
     * 시퀀스 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_seq_no_model_doip_ctnt")
    @SequenceGenerator(
            name = "s_seq_no_model_doip_ctnt",
            sequenceName = "S_SEQ_NO_MODEL_DOIP_CTNT",
            allocationSize = 1
    )
    @Column(name = "SEQ_NO", nullable = false, insertable = false, updatable = false)
    private Long seqNo;

    /**
     * 아티팩트 ID
     */
    @Column(name = "ARTIFACT_ID", length = 50, nullable = false)
    private String artifactId;

    /**
     * 리비전 ID
     */
    @Column(name = "REVISION_ID", length = 50, nullable = false)
    private String revisionId;

    /**
     * 타입
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_NM", length = 100)
    private ModelDoipCtntType type;

    /**
     * 상세 내용
     */
    @Column(name = "DTL_CTNT", length = 4000)
    private String dtlCtnt;

    /**
     * 최초 생성일시
     */
    @CreatedDate
    @Column(name = "FST_CREATED_AT", updatable = false, nullable = false)
    private LocalDateTime fstCreatedAt;

    /**
     * 생성자
     */
    @CreatedBy
    @Column(name = "CREATED_BY", length = 50, updatable = false, nullable = false)
    private String createdBy;

}

