package com.skax.aiplatform.repository.home;

import com.skax.aiplatform.common.constant.CommCode;
import com.skax.aiplatform.entity.alarm.GpoAlarmsMas;
import com.skax.aiplatform.entity.alarm.GpoAlarmsMasId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GpoAlarmsRepository extends JpaRepository<GpoAlarmsMas, GpoAlarmsMasId> {
    @Query("""
            SELECT a
            FROM GpoAlarmsMas a
            WHERE a.id.memberId = :username AND a.fstCreatedAt >= :startDate
            ORDER BY a.fstCreatedAt desc
            """)
    List<GpoAlarmsMas> findAlarmsAfterDate(String username, LocalDateTime startDate);

    @Query("""
            SELECT a
            FROM GpoAlarmsMas a
            WHERE a.id.memberId = :username AND a.fstCreatedAt >= :startDate
            AND a.readYn = 0
            ORDER BY a.fstCreatedAt desc
            """)
    List<GpoAlarmsMas> findNewAlarmsAfterDate(String username, LocalDateTime startDate);

    @Query("""
            SELECT a
            FROM GpoAlarmsMas a
            WHERE a.id.memberId = :memberId AND a.id.alarmId = :alarmId
            """)
    GpoAlarmsMas findByAlarmIdAndMemberId(String alarmId, String memberId);

    @Query("""
            SELECT a
            FROM GpoAlarmsMas a
            WHERE a.id.alarmId = :alarmId
            """)
    GpoAlarmsMas findByAlarmId(String alarmId);

    @Query("""
            SELECT a
            FROM GpoAlarmsMas a
            WHERE a.id.memberId = :memberId
            """)
    List<GpoAlarmsMas> findByAlarmMemberId(String memberId);

    @Query("""
            SELECT a
            FROM GpoAlarmsMas a
            WHERE a.apiRstMsg = :documentId
            """)
    List<GpoAlarmsMas> findByDocumentId(String documentId);

    @Query("""
            SELECT a
            FROM GpoAlarmsMas a
            WHERE a.apiRstMsg = :documentId
            AND a.statusNm = :statusNm
            """)
    List<GpoAlarmsMas> findByDocumentIdAndStatusNm(String documentId, CommCode.AlarmStatus statusNm);

    @Modifying
    @Query("""
            DELETE FROM GpoAlarmsMas a
            WHERE a.apiRstMsg = :apiRstMsg AND a.id.memberId <> :memberId
            """)
    void deleteByApiRstMsgAndNotMatchMemberId(String apiRstMsg, String memberId);

    boolean existsByApiRstMsgAndStatusNm(String apiRstMsg, CommCode.AlarmStatus statusNm);
}
