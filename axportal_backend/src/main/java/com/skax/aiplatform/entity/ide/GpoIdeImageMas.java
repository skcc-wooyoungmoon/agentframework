package com.skax.aiplatform.entity.ide;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.skax.aiplatform.entity.common.BaseEntity2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * IDE 이미지 원장 테이블
 */
@Entity
@Table(name = "GPO_IDE_IMAGE_MAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpoIdeImageMas extends BaseEntity2 {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "UUID")
    private String uuid;

    /**
     * 이미지명
     */
    @Column(name = "IMG_NM")
    private String imgNm;

    /**
     * 설명
     */
    @Column(name = "DTL_CTNT")
    private String dtlCtnt;

    /**
     * 이미지 구분 (= 도구명)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "IMG_G")
    private ImageType imgG;

    /**
     * 이미지 경로
     */
    @Column(name = "IMG_URL")
    private String imgUrl;

    public static GpoIdeImageMas create(String imgNm, String dtlCtnt, String imgUrl, ImageType imgG) {
        return GpoIdeImageMas.builder()
                .imgNm(imgNm)
                .dtlCtnt(dtlCtnt)
                .imgUrl(imgUrl)
                .imgG(imgG)
                .build();
    }

    public void update(String imgNm, String dtlCtnt, String imgUrl, ImageType imgG) {
        this.imgNm = imgNm;
        this.dtlCtnt = dtlCtnt;
        this.imgUrl = imgUrl;
        this.imgG = imgG;
    }

}
