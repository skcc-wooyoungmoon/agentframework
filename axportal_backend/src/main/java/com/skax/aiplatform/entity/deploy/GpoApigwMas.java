package com.skax.aiplatform.entity.deploy;

import com.skax.aiplatform.entity.common.AuditableEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "GPO_APIGW_MAS")
@Getter
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GpoApigwMas extends AuditableEntity {
    
    /**
     * 시퀀스 번호 (PK)
     */
    // @Id
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_seq_no_gpo_apigw_mas")
    // @SequenceGenerator(
    //         name = "s_seq_no_gpo_apigw_mas",
    //         sequenceName = "S_SEQ_NO_GPO_APIGW_MAS",
    //         allocationSize = 1
    // )
    // @Column(name = "SEQ_NO", insertable = false, updatable = false)
    // private Long seqNo;
    
    /**
     * GPO API ID
     */
    @Id
    @NotNull
    @Size(max = 100)
    @Column(name = "GPO_API_ID", length = 100, nullable = false)
    private String gpoApiId;

    /**
     * GPO API Name
     */
    @NotNull
    @Size(max = 100)
    @Column(name = "GPO_TSK_ID", length = 100, nullable = false)
    private String gpoTskId;
    
}

