package com.skax.aiplatform.entity.alarm;

import com.skax.aiplatform.common.constant.CommCode;
import com.skax.aiplatform.entity.common.BaseEntity2;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gpo_alarms_mas")
public class GpoAlarmsMas extends BaseEntity2 {

    @EmbeddedId
    private GpoAlarmsMasId id;

    @Size(max = 600)
    @Column(name = "alarm_ttl_nm", length = 600)
    private String alarmTtl;

    @Size(max = 100)
    @Column(name = "dtl_ctnt", length = 100)
    private String dtlCtnt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @NotNull
    @Column(name = "read_yn", nullable = false, precision = 1)
    private BigDecimal readYn;

    @Size(max = 45)
    @Column(name = "api_rst_msg", length = 45)
    private String apiRstMsg;

    @Column(name = "status_nm", nullable = true)
    @Convert(converter = AlarmStatusConverter.class)
    private CommCode.AlarmStatus statusNm;
}