package com.skax.aiplatform.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gpo_ide_image_mas_old")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdeImageCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // todo: sequence로 변경 필요
    @Column(name = "seq_no", nullable = false)
    private Long seqNo;

    @Column(name = "img_g", length = 20)
    private String imgG; // IDE 구분 (jupyter, vscode)

    @Column(name = "pgm_version_no", length = 10)
    private String pgmVersionNo; // Python 버전 (3.12, 3.13)

    @Column(name = "tag_ctnt", length = 100)
    private String tagCtnt; // 이미지 태그

    @Column(name = "usyn", precision = 1, scale = 0)
    private BigDecimal usyn; // 사용여부 (1: 사용, 0: 미사용)

    @Column(name = "fst_created_at")
    private LocalDateTime fstCreatedAt;

    @Column(name = "lst_updated_at")
    private LocalDateTime lstUpdatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

}
