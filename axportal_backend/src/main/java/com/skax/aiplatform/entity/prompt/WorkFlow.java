package com.skax.aiplatform.entity.prompt;

import com.skax.aiplatform.entity.common.BaseEntity2;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "gpo_workflow_mas")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WorkFlow extends BaseEntity2 {

    @Id
    @Column(name = "wf_id", length = 8, nullable = false)
    private String workflowId;

    @Column(name = "wf_nm", length = 150)
    private String workflowName;

    @Column(name = "ver_mng_sno", precision = 10, scale = 0)
    private Integer versionNo;

    @Column(name = "dtl_ctnt", length = 4000)
    private String part1;

    @Column(name = "fist_dtl_ctnt", length = 4000)
    private String part2;

    @Column(name = "secd_dtl_ctnt", length = 4000)
    private String part3;

    @Column(name = "thi_dtl_ctnt", length = 4000)
    private String part4;

    @Column(name = "frh_dtl_ctnt", length = 4000)
    private String part5;

    public String getXmlText() {
        return (part1 == null ? "" : part1) +
               (part2 == null ? "" : part2) +
               (part3 == null ? "" : part3) +
               (part4 == null ? "" : part4) +
               (part5 == null ? "" : part5);
    }

    public void setXmlText(String text) {
        if (text == null) {
            this.part1 = null;
            this.part2 = null;
            this.part3 = null;
            this.part4 = null;
            this.part5 = null;
            return;
        }

        int chunkSize = 1333;
        int length = text.length();

        this.part1 = text.substring(0, Math.min(length, chunkSize));
        this.part2 = length > chunkSize ? text.substring(chunkSize, Math.min(length, chunkSize * 2)) : null;
        this.part3 = length > chunkSize * 2 ? text.substring(chunkSize * 2, Math.min(length, chunkSize * 3)) : null;
        this.part4 = length > chunkSize * 3 ? text.substring(chunkSize * 3, Math.min(length, chunkSize * 4)) : null;
        this.part5 = length > chunkSize * 4 ? text.substring(chunkSize * 4, Math.min(length, chunkSize * 5)) : null;
    }

    @Column(name = "desc_ctnt", length = 50)
    private String description;

    @Column(name = "usyn", precision = 1, scale = 0)
    private Integer isActive;

    @Column(name = "prj_seq", length = 5)
    private long projectSeq;

    @Column(name = "scop_v_desc_ctnt", length = 300)
    private String projectScope;

    @Column(name = "tag_dtl_ctnt", length = 4000)
    private String tag;
}
