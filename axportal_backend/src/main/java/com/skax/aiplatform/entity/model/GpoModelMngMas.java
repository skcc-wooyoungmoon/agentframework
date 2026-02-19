package com.skax.aiplatform.entity.model;

import com.skax.aiplatform.entity.common.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * GPO 모델 매핑 원장 엔티티
 * 
 * <p>모델 관리 정보를 저장하는 엔티티입니다.</p>
 * 
 * @author system
 * @since 2025-01-XX
 * @version 1.0.0
 */
@Entity
@Table(name = "GPO_MODEL_MNG_MAS")
@Getter
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GpoModelMngMas extends AuditableEntity {

    /**
     * 시퀀스 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_seq_no_model_mng")
    @SequenceGenerator(
            name = "s_seq_no_model_mng",
            sequenceName = "S_SEQ_NO_MODEL_MNG",
            allocationSize = 1
    )
    @Column(name = "SEQ_NO", insertable = false, updatable = false)
    private Long seqNo;

    
    /**
     * 사용가능여부모델SEQNO
     * 모델 가든 id
     */
    @Column(name = "USE_GNYN_MODEL_SEQ_NO", precision = 19, nullable = false)
    private Long useGnynModelSeqNo;


    /**
     * 모델 관리 ID
     * 모델 카탈로그 id
     */
    @Column(name = "MODEL_MNG_ID", length = 500, nullable = false)
    private String modelMngId;

}

