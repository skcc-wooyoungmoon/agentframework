package com.skax.aiplatform.entity.deploy;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * GPO_MIG_ASST_MAP_MAS 복합키: (SEQ_NO, MIG_UUID, ASST_G)
 * @IdClass용 - 단순 POJO, 어노테이션 불필요
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GpoMigAsstMapMasId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long seqNo;
    private String migUuid;
    private String asstG;
}

