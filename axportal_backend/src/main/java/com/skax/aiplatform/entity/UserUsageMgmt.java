package com.skax.aiplatform.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;


 
    
    /**
     * 사용자 사용량 관리 엔티티
     * 
     * <p>
     * 사용자 사용량 관리를 저장하는 엔티티입니다.
     * </p>
     * 
     */


@Entity
@Table(name = "GPO_LOG_MAS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserUsageMgmt {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_seq_no_log")
    @SequenceGenerator(
            name = "s_seq_no_log",
            sequenceName = "S_SEQ_NO_LOG",
            allocationSize = 1
    )
    @Column(name = "SEQ_NO", nullable = false)
    private Long id;

    @Column(name = "UUID")
    private String userName;

    @Column(name = "GPO_PRJ_NM")
    private String projectName;

    @Transient
    private String projectId;

    @Column(name = "GPO_ROLE_NM", length = 150)
    private String roleName;

    @Column(name = "MENU_LINK_PATH", length = 200)
    private String menuPath;

    @Column(name = "ACTN_NM")
    private String action;

    @Column(name = "HMK_NM", length = 200)
    private String targetAsset;

    @Column(name = "RESRC_TYPE")
    private String resourceType;

    @Column(name = "API_URL", length = 500)
    private String apiEndpoint;

    @Column(name = "API_RST_CD")
    private String errCode;

    @Column(name = "CLIENT_IP_NO")
    private String clientIp;

    @Column(name = "DTL_CTNT", length = 500)
    private String userAgent;

    @Column(name = "RQST_CTNT", length = 1000)
    private String requestContent;

    @Column(name = "FIST_RQST_DTL_CTNT", length = 1000)
    private String firstRequestDetail;

    @Column(name = "SECD_RQST_DTL_CTNT", length = 1000)
    private String secondRequestDetail;

    @Column(name = "THI_RQST_DTL_CTNT", length = 1000)
    private String thirdRequestDetail;

    @Column(name = "FRH_RQST_DTL_CTNT", length = 1000)
    private String fourthRequestDetail;

    @Column(name = "RESP_CTNT", length = 500)
    private String responseContent;

    @Column(name = "FIST_RESP_DTL_CTNT", length = 1000)
    private String firstResponseDetail;

    @Column(name = "SECD_RESP_DTL_CTNT", length = 1000)
    private String secondResponseDetail;

    @Column(name = "THI_RESP_DTL_CTNT", length = 1000)
    private String thirdResponseDetail;

    @Column(name = "FRH_RESP_DTL_CTNT", length = 1000)
    private String fourthResponseDetail;

    @Column(name = "FST_CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.createdBy == null) {
            this.createdBy = "system";
        }
    }

}
