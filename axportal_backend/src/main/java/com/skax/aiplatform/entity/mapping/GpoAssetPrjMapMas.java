package com.skax.aiplatform.entity.mapping;

import com.skax.aiplatform.entity.common.BaseEntity2;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

/**
 * 자산-프로젝트 매핑 원장 테이블 엔티티 (gpo_asstprj_map_mas)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "gpo_asstprj_map_mas")
public class GpoAssetPrjMapMas extends BaseEntity2 {

    /**
     * 어셋 URL (PK)
     */
    @Id
    @Column(name = "asst_url", length = 300, nullable = false)
    private String asstUrl;

    /**
     * 최초 프로젝트 SEQ
     */
    @Column(name = "fst_prj_seq")
    private Integer fstPrjSeq;

    /**
     * 최종 프로젝트 SEQ
     */
    @Column(name = "lst_prj_seq")
    private Integer lstPrjSeq;
}
