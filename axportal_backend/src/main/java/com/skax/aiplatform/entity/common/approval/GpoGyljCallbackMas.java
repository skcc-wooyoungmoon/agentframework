package com.skax.aiplatform.entity.common.approval;

import com.skax.aiplatform.entity.common.BaseEntity2;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gpo_gylj_callback_mas")
public class GpoGyljCallbackMas extends BaseEntity2 {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_hist_no_gylj_callback")
    @SequenceGenerator(
            name = "s_hist_no_gylj_callback",
            sequenceName = "S_HIST_NO_GYLJ_CALLBACK",
            allocationSize = 1
    )
    @Column(name = "hist_no", nullable = false)
    private Long id;

    @Size(max = 100)
    @Column(name = "gylj_resp_id", length = 100)
    private String gyljRespId;

    @Size(max = 2048)
    @Column(name = "api_spcl_v", length = 2048)
    private String apiSpclV;

    @Size(max = 50)
    @Column(name = "gyljja_member_id", length = 50)
    private String gyljjaMemberId;

    @Size(max = 45)
    @Column(name = "api_rst_cd", length = 45)
    private String apiRstCd;

    @Size(max = 1024)
    @Column(name = "ero_ctnt", length = 1024)
    private String eroCtnt;

}