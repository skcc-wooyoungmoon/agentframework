package com.skax.aiplatform.entity.ide;

import com.skax.aiplatform.entity.common.BaseEntity2;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * IDE 상태 테이블
 */
@Entity
@Table(name = "GPO_IDE_STATUS_MAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpoIdeStatusMas extends BaseEntity2 {

    @Id
    @Column(name = "UUID")
    private String uuid;

    // == 참조 컬럼 == //

    /**
     * 사용자 ID
     */
    @Column(name = "MEMBER_ID")
    private String memberId;

    /**
     * IDE 이미지 UUID
     */
    @Column(name = "IMG_UUID")
    private String imgUuid;

    /**
     * DW 계정 ID
     */
    @Column(name = "DW_ACT_ID")
    private String dwAccountId;

    // == 핵심 컬럼 == //

    /**
     * 서버 URL
     */
    @Column(name = "SVR_URL_NM")
    private String svrUrlNm;

    /**
     * CPU 할당량
     */
    @Column(name = "CPU_USE_HALDNG_V")
    private BigDecimal cpuUseHaldngV;

    /**
     * 메모리 할당량
     */
    @Column(name = "MEM_USE_HALDNG_V")
    private BigDecimal memUseHaldngV;

    /**
     * 만료일시
     */
    @Column(name = "EXP_AT")
    private LocalDateTime expAt;

    /**
     * 만료일시 연장
     * @param days 연장할 일수
     */
    public void extendExpiration(int days) {
        this.expAt = this.expAt.plusDays(days);
    }

}
