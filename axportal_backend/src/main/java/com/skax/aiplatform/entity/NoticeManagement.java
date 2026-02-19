package com.skax.aiplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 공지사항 엔티티
 * 
 * <p>
 * 공지사항을 저장하는 엔티티입니다.
 * BaseEntity를 상속받아 공통 필드(생성일시, 수정일시 등)를 포함합니다.
 * </p>
 * 
 */

@Entity
@Table(name = "gpo_notice_mas")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NoticeManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_noti_seq")
    @SequenceGenerator(
            name = "s_noti_seq",
            sequenceName = "S_NOTI_SEQ",
            allocationSize = 1
    )
    @Column(name = "noti_seq", nullable = false)
    private Long notiId; // 공지사항 번호 (PK)

    @Column(name = "noti_ttl", length = 500)
    private String title; // 제목

    @Column(name = "blltb_noti_ctnt", columnDefinition = "TEXT")
    private String msg; // 내용

    @Column(name = "fist_noti_dtl_ctnt", length = 1000)
    private String firstDetail; // 1차 상세 내용

    @Column(name = "secd_noti_dtl_ctnt", length = 1000)
    private String secondDetail; // 2차 상세 내용

    @Column(name = "thi_noti_dtl_ctnt", length = 1000)
    private String thirdDetail; // 3차 상세 내용

    @Column(name = "frh_noti_dtl_ctnt", length = 1000)
    private String fourthDetail; // 4차 상세 내용

    @Column(name = "noti_u_ctnt", length = 100)
    private String type; // 공지사항 유형 (예: 일반, 긴급)

    @Column(name = "usyn")
    private Integer useYn; // 상태 (0: N, 1: Y)

    @Column(name = "EXP_STT_AT")
    private LocalDateTime expFrom; // 공지 시작일

    @Column(name = "EXP_END_AT")
    private LocalDateTime expTo; // 공지 종료일

    // 공통 필드들을 직접 정의
    @Column(name = "fst_created_at", updatable = false)
    private LocalDateTime createAt;

    @Column(name = "lst_updated_at")
    private LocalDateTime updateAt;

    @Column(name = "created_by", length = 50, updatable = false)
    private String createBy;

    @Column(name = "updated_by", length = 50)
    private String updateBy;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }

    // ==================== 값 변환 메서드 ====================
    
    /**
     * Y/N 문자열을 숫자로 변환 (Y -> 1, N -> 0)
     */
    public static Integer convertStringToNumber(String useYn) {
        if (useYn == null) {
            return null;
        }
        return "Y".equalsIgnoreCase(useYn) ? 1 : 0;
    }
    
    /**
     * 숫자를 Y/N 문자열로 변환 (1 -> Y, 0 -> N)
     */
    public static String convertNumberToString(Integer useYn) {
        if (useYn == null) {
            return null;
        }
        return useYn == 1 ? "Y" : "N";
    }
    
    /**
     * 현재 useYn 값을 문자열로 반환
     */
    public String getUseYnAsString() {
        return convertNumberToString(this.useYn);
    }
    
    /**
     * useYn을 문자열로 설정
     */
    public void setUseYnAsString(String useYn) {
        this.useYn = convertStringToNumber(useYn);
    }

}
