package com.skax.aiplatform.entity.common.approval;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.skax.aiplatform.entity.common.BaseEntity2;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "gpo_gylj_mas")
@EntityListeners(AuditingEntityListener.class)
public class GpoGyljMas extends BaseEntity2 {
    @Id
    @Size(max = 100)
    @Column(name = "gylj_id", nullable = false, length = 100)
    private String gyljId;

    @Size(max = 100)
    @Column(name = "gylj_ttl", length = 100)
    private String gyljTtl;

    @Size(max = 4000)
    @Column(name = "gylj_dtl_ctnt", length = 4000)
    private String gyljRsn;

    @Size(max = 4000)
    @Column(name = "dtl_ctnt", length = 4000)
    private String dtlCtnt;

    @Size(max = 500)
    @Column(name = "clbk_url", length = 500)
    private String clbkUrl;

    @Size(max = 50)
    @Column(name = "member_id", length = 50)
    private String memberId;

    @Size(max = 50)
    @Column(name = "gyljja_member_id", length = 50)
    private String gyljjaMemberId;

    @Column(name = "gylj_line_sno", precision = 3)
    private BigDecimal gyljLineSno;

    @Column(name = "gylj_line_tot_sno", precision = 3)
    private BigDecimal gyljLineTotSno;

    @Size(max = 100)
    @Column(name = "gylj_resp_id", length = 100)
    private String gyljRespId;

    @Size(max = 45)
    @Column(name = "api_rst_cd", length = 45)
    private String apiRstCd;

    @Size(max = 1024)
    @Column(name = "ero_ctnt", length = 1024)
    private String eroCtnt;

    @Size(max = 50)
    @Column(name = "trx_id", length = 50)
    private String trxId;

    @Size(max = 100)
    @Column(name = "gylj_line_nm", length = 100)
    private String gyljLineNm;


}