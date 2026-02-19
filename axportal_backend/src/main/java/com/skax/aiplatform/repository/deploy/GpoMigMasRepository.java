package com.skax.aiplatform.repository.deploy;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.skax.aiplatform.entity.deploy.GpoMigMas;
import com.skax.aiplatform.entity.deploy.GpoMigMasId;

@Repository
public interface GpoMigMasRepository extends JpaRepository<GpoMigMas, GpoMigMasId> {

        /**
         * UUID로 엔티티 조회
         * 복합키로 인해 여러 레코드가 있을 수 있으므로 첫 번째 레코드만 반환
         */
        @Query(value = """
                        SELECT * FROM GPO_MIG_MAS 
                        WHERE UUID = :uuid 
                        ORDER BY FST_CREATED_AT DESC
                        LIMIT 1
                        """, nativeQuery = true)
        Optional<GpoMigMas> findByUuid(@Param("uuid") String uuid);

        /**
         * UUID로 엔티티 목록 조회 (복합키로 인해 여러 레코드가 있을 수 있음)
         */
        List<GpoMigMas> findAllByUuid(String uuid);

        /**
         * 어시스트명으로 조회
         * 
         * @param asstNm 어시스트명
         * @return 어시스트 목록
         */
        List<GpoMigMas> findByAsstNm(String asstNm);

        /**
         * 삭제되지 않은 UUID로 조회
         * 
         * <p>
         * 사용 예시:
         * </p>
         * 
         * <pre>{@code
         * Optional<GpoMigMas> entity = repository.findByUuidAndDelYn("uuid-value", 0);
         * }</pre>
         * 
         * @param uuid  UUID
         * @param delYn 삭제 여부 (0: 정상, 1: 삭제)
         * @return 삭제되지 않은 엔티티
         */
        Optional<GpoMigMas> findByUuidAndDelYn(String uuid, Integer delYn);

        
        /**
         * descmt 조회
         * 
         * @param uuid UUID
         * @return 프로그램 설명 내용이 일치하는 엔티티
         */
        Optional<GpoMigMas> findByPgmDescCtnt(String pgmDescCtnt);

        /**
         * 운영 이행 관리 조회 (페이지네이션 지원) - Native Query
         * 
         * <p>
         * 조건:
         * <ul>
         * <li>fst_created_at >= startDate (startDate가 있는 경우)</li>
         * <li>fst_created_at <= endDate (endDate가 있는 경우)</li>
         * <li>asst_nm LIKE %asstNm% (asstNm이 있는 경우)</li>
         * <li>asst_g = asstG (asstG가 있는 경우)</li>
         * </ul>
         * </p>
         * 
         * @param startDate 조회 시작일시 (null 가능, String으로 전달)
         * @param endDate   조회 종료일시 (null 가능, String으로 전달)
         * @param asstNm    어시스트명 검색어 (null 또는 빈 문자열 가능)
         * @param asstG     어시스트 그룹 필터 (null 또는 빈 문자열 가능)
         * @param pageable  페이지네이션 정보
         * @return 페이지네이션된 결과
         */
        @Query(value = """
                        SELECT * FROM gpo_mig_mas m
                        WHERE 1=1
                          AND (:startDate IS NULL OR :startDate = '' OR m.fst_created_at >= TO_TIMESTAMP(:startDate, 'YYYY-MM-DD HH24:MI:SS'))
                          AND (:endDate IS NULL OR :endDate = '' OR m.fst_created_at <= TO_TIMESTAMP(:endDate, 'YYYY-MM-DD HH24:MI:SS'))
                          AND (:asstNm IS NULL OR :asstNm = '' OR m.asst_nm LIKE '%' || :asstNm || '%')
                          AND (:asstG IS NULL OR :asstG = '' OR m.asst_g = :asstG)
                          AND (:prjSeq IS NULL OR m.prj_seq = :prjSeq)
                          AND del_yn = 0
                        ORDER BY m.fst_created_at DESC
                        """, countQuery = """
                        SELECT COUNT(*) FROM gpo_mig_mas m
                        WHERE 1=1
                          AND (:startDate IS NULL OR :startDate = '' OR m.fst_created_at >= TO_TIMESTAMP(:startDate, 'YYYY-MM-DD HH24:MI:SS'))
                          AND (:endDate IS NULL OR :endDate = '' OR m.fst_created_at <= TO_TIMESTAMP(:endDate, 'YYYY-MM-DD HH24:MI:SS'))
                          AND (:asstNm IS NULL OR :asstNm = '' OR m.asst_nm LIKE '%' || :asstNm || '%')
                          AND (:asstG IS NULL OR :asstG = '' OR m.asst_g = :asstG)
                          AND (:prjSeq IS NULL OR m.prj_seq = :prjSeq)
                          AND del_yn = 0
                        """, nativeQuery = true)
        Page<GpoMigMas> findMigMasWithFilters(
                        @Param("startDate") String startDate,
                        @Param("endDate") String endDate,
                        @Param("asstNm") String asstNm,
                        @Param("asstG") String asstG,
                        @Param("prjSeq") Integer prjSeq,
                        Pageable pageable);

        /**
         * GPO_MIG_MAS와 GPO_MIG_ASST_MAP_MAS 조인 조회 (모든 정보)
         * 
         * <p>
         * 두 테이블을 LEFT JOIN하여 gpo_mig_mas의 모든 레코드를 조회하고,
         * 매칭되는 gpo_migasst_map_mas 레코드가 있으면 함께 조회합니다.
         * </p>
         * <p>
         * 조인 조건: m.UUID = map.MIG_UUID AND m.seq_no = map.mig_seq_no
         *            AND m.asst_g = map.asst_g AND m.asst_nm = map.asst_nm
         * </p>
         * 
         * @return 조인된 결과 (Object[] 배열)
         *         [0] = GPO_MIG_MAS의 모든 컬럼
         *         [1] = GPO_MIG_ASST_MAP_MAS의 모든 컬럼
         */
        @Query(value = """
                        SELECT
                            m.SEQ_NO AS mas_seq_no,
                            m.UUID AS mas_uuid,
                            m.ASST_G AS mas_asst_g,
                            m.ASST_NM AS mas_asst_nm,
                            m.PRJ_SEQ AS mas_prj_seq,
                            m.GPO_PRJ_NM AS mas_gpo_prj_nm,
                            m.MIG_FILE_PATH AS mas_mig_file_path,
                            m.MIG_FILE_NM AS mas_mig_file_nm,
                            m.PGM_DESC_CTNT AS mas_pgm_desc_ctnt,
                            m.DEL_YN AS mas_del_yn,
                            m.FST_CREATED_AT AS mas_fst_created_at,
                            m.CREATED_BY AS mas_created_by,
                            map.SEQ_NO AS map_seq_no,
                            map.MIG_SEQ_NO AS map_mig_seq_no,
                            map.MIG_UUID AS map_mig_uuid,
                            map.ASST_UUID AS map_asst_uuid,
                            map.ASST_G AS map_asst_g,
                            map.ASST_NM AS map_asst_nm,
                            map.MIG_MAP_NM AS map_mig_map_nm,
                            map.DVLP_DTL_CTNT AS map_dvlp_dtl_ctnt,
                            map.UNYUNG_DTL_CTNT AS map_unyung_dtl_ctnt
                        FROM gpo_mig_mas m
                        LEFT JOIN gpo_migasst_map_mas map ON m.UUID = map.MIG_UUID
                            AND m.seq_no = map.mig_seq_no
                        WHERE (:sequence IS NULL OR m.SEQ_NO = :sequence)
                            AND (:uuid IS NULL OR m.UUID = :uuid)
                            AND (:asstG IS NULL OR m.ASST_G = :asstG)
                        ORDER BY m.FST_CREATED_AT DESC, COALESCE(map.SEQ_NO, 0) ASC
                        """, nativeQuery = true)
        List<Object[]> findAllMigMasWithMap(
                        @Param("sequence") Integer sequence,
                        @Param("uuid") String uuid,
                        @Param("asstG") String asstG);
}