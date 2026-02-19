package com.skax.aiplatform.dto.auth.response;

import com.skax.aiplatform.common.constant.CommCode;
import com.skax.aiplatform.entity.alarm.GpoAlarmsMas;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmListResponse {
    /**
     * 알람 목록
     */
    private List<AlarmDto> alarms;

    /**
     * 알람 총 개수
     */
    private int count;

    /**
     * 알람 목록으로부터 응답 객체 생성
     *
     * @param alarms 알람 목록
     * @return 알람 목록 응답 DTO
     */
    public static AlarmListResponse from(List<GpoAlarmsMas> alarms) {
        List<AlarmDto> alarmDtos = alarms != null
                ? alarms.stream()
                .map(AlarmDto::from)
                .collect(Collectors.toList())
                : null;

        return AlarmListResponse.builder()
                .alarms(alarmDtos)
                .count(alarms != null ? alarms.size() : 0)
                .build();
    }

    /**
     * 알람 데이터 DTO
     * createdAt을 포맷팅된 날짜/시간 형식으로 제공
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlarmDto {
        private String alarmId;
        private String memberId;
        private String ttlNm;
        private String alarmCtnt;
        private String apiRstMsg;

        @Enumerated(EnumType.STRING)
        private CommCode.AlarmStatus statusNm;

        private String readYn;
        private String readAt; // 포맷팅된 생성 시간
        private String createdAt;

        /**
         * Alarms 엔티티로부터 DTO 생성
         */
        public static AlarmDto from(GpoAlarmsMas alarm) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

            return AlarmDto.builder()
                    .alarmId(alarm.getId().getAlarmId())
                    .memberId(alarm.getId().getMemberId())
                    .ttlNm(alarm.getAlarmTtl())
                    .alarmCtnt(alarm.getDtlCtnt())
                    .apiRstMsg(alarm.getApiRstMsg())
                    .statusNm(alarm.getStatusNm())
                    .readYn(String.valueOf(alarm.getReadYn()))
                    .readAt(alarm.getReadAt() != null ? alarm.getReadAt().format(formatter) : null)
                    .createdAt(alarm.getFstCreatedAt() != null ? alarm.getFstCreatedAt().format(formatter) : null)
                    .build();
        }
    }
}
