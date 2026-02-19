package com.skax.aiplatform.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gpo_ide_status_mas_old")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserIdeStatus {

    @Id
    @Column(name = "uuid", length = 100, nullable = false)
    private String uuid; // IDE ID

    @Column(name = "member_id", length = 50)
    private String memberId; // 사용자 ID

    @Column(name = "jkw_nm", length = 100)
    private String jkwNm; // 사용자 이름

    @Column(name = "img_g", length = 10)
    private String imgG; // IDE 구분 (jupyter, vscode)

    @Column(name = "status_nm", length = 100)
    private String statusNm; // 상태

    @Column(name = "prj_seq", length = 5)  // UUID는 36자이므로 50으로 충분
    private long prjSeq; // 프로젝트 ID

    @Column(name = "cpu_use_haldng_v", precision = 10, scale = 0)
    private BigDecimal cpuUseHaldngV; // CPU 할당량

    @Column(name = "mem_use_haldng_v", precision = 10, scale = 0)
    private BigDecimal memUseHaldngV; // 메모리 할당량

    @Column(name = "tag_ctnt", length = 100)
    private String tagCtnt; // 이미지 태그

    @Column(name = "exp_at")
    private LocalDateTime expAt; // 만료일시

    @Column(name = "fst_created_at")
    private LocalDateTime fstCreatedAt;

    @Column(name = "lst_updated_at")
    private LocalDateTime lstUpdatedAt;

    @Column(name = "svr_url_nm", length = 500)  // URL은 길 수 있으므로 500으로 설정
    private String svrUrlNm; // 서버 URL

    @Column(name = "pgm_version_no", length = 10)
    private String pgmVersionNo; // Python 버전

}
