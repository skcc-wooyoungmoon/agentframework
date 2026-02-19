package com.skax.aiplatform.entity.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * GPO 도커 이미지 URL 원장 테이블
 */
@Entity
@Table(name = "GPO_DOCKER_IMG_URL_MAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpoDockerImgUrlMas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ_NO")
    private Long seqNo;

    /**
     * SYSTEM 유형값
     */
    @Column(name = "SYS_U_V", length = 100, nullable = false)
    private String sysUV;

    /**
     * 이미지 URL
     */
    @Column(name = "IMG_URL", length = 300, nullable = false)
    private String imgUrl;

    /**
     * 삭제 여부 (0: 정상, 1: 삭제)
     */
    @Column(name = "DEL_YN")
    private Integer delYn;

    /**
     * 최초 생성일시
     */
    @Column(name = "FST_CREATED_AT")
    private LocalDateTime fstCreatedAt;

    /**
     * 생성자
     */
    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    /**
     * 최종 수정일시
     */
    @Column(name = "LST_UPDATED_AT")
    private LocalDateTime lstUpdatedAt;

    /**
     * 수정자
     */
    @Column(name = "UPDATED_BY", length = 50)
    private String updatedBy;
}
