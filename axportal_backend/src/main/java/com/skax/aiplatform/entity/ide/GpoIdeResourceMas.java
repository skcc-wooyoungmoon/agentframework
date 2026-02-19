package com.skax.aiplatform.entity.ide;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.skax.aiplatform.entity.common.BaseEntity2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * IDE 자원 테이블
 */
@Entity
@Table(name = "GPO_IDE_RESRC_MAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpoIdeResourceMas extends BaseEntity2 {

    /**
     * 이미지 구분
     */
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "IMG_G")
    private ImageType imgG;

    /**
     * 이미지 생성 가능 개수
     */
    @Column(name = "LIMIT_CNT")
    private int limitCnt;

    public void updateLimitCnt(int limitCnt) {
        this.limitCnt = limitCnt;
    }

}
