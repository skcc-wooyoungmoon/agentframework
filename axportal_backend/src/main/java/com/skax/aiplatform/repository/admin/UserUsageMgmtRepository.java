package com.skax.aiplatform.repository.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skax.aiplatform.entity.UserUsageMgmt;

public interface UserUsageMgmtRepository extends JpaRepository<UserUsageMgmt, Long> {

       /**
        * 특정 사용자의 특정 액션에 대한 최근 로그 조회
        * 
        * @param userName       사용자명
        * @param action         액션
        * @param createdAtAfter 생성일시 이후
        * @return 최근 로그
        */
       Optional<UserUsageMgmt> findTopByUserNameAndActionAndCreatedAtAfterOrderByCreatedAtDesc(
                     String userName, String action, LocalDateTime createdAtAfter);

       /**
        * 특정 사용자의 특정 API 엔드포인트에 대한 가장 가까운 로그 조회 (ID 기반)
        * 
        * @param userName     사용자명
        * @param apiEndpoint  API 엔드포인트
        * @param currentLogId 현재 로그 ID
        * @return 가장 가까운 로그
        */
       Optional<UserUsageMgmt> findTopByUserNameAndApiEndpointAndIdLessThanOrderByIdDesc(
                     String userName, String apiEndpoint, Long currentLogId);

       /**
        * 사용자 사용량 관리 전체 조회 (최신순 정렬)
        * 
        * @param pageable 페이징 정보
        * @return 사용자 사용량 관리 목록 (최신순)
        */
       @Query("SELECT u FROM UserUsageMgmt u WHERE (u.targetAsset IS NULL OR u.targetAsset <> 'Y') ORDER BY u.createdAt DESC")
       Page<UserUsageMgmt> findAllOrderByCreatedAtDesc(Pageable pageable);

       /**
        * ID로 사용자 사용량 관리 상세 조회 (한글명 포함)
        * 
        * @param id 사용자 사용량 관리 ID
        * @return 사용자 사용량 관리 (한글명 포함)
        */
       @Query(value = "SELECT u.SEQ_NO, COALESCE(gum.JKW_NM, u.UUID) as UUID, " +
                     "COALESCE(gpm.GPO_PRJ_NM, u.GPO_PRJ_NM) as GPO_PRJ_NM, u.GPO_PRJ_NM as GPO_PRJ_ID, u.GPO_ROLE_NM, "
                     +
                     "u.MENU_LINK_PATH, u.ACTN_NM, u.HMK_NM, u.RESRC_TYPE, u.API_URL, u.API_RST_CD, " +
                     "u.CLIENT_IP_NO, u.DTL_CTNT, u.RQST_CTNT, u.FIST_RQST_DTL_CTNT, u.SECD_RQST_DTL_CTNT, u.THI_RQST_DTL_CTNT, u.FRH_RQST_DTL_CTNT, "
                     +
                     "u.RESP_CTNT, u.FIST_RESP_DTL_CTNT, u.SECD_RESP_DTL_CTNT, u.THI_RESP_DTL_CTNT, u.FRH_RESP_DTL_CTNT, u.FST_CREATED_AT, u.CREATED_BY "
                     +
                     "FROM GPO_LOG_MAS u " +
                     "LEFT JOIN GPO_USERS_MAS gum ON u.UUID = gum.MEMBER_ID " +
                     "LEFT JOIN GPO_PROJECTS_MAS gpm ON u.GPO_PRJ_NM = gpm.UUID " +
                     "WHERE u.SEQ_NO = :id " +
                     "AND (u.HMK_NM IS NULL OR u.HMK_NM <> 'Y')", nativeQuery = true)
       Optional<UserUsageMgmt> findByIdWithUserName(@Param("id") Long id);

       /**
        * 검색 조건에 따른 사용자 사용량 관리 조회
        * 
        * @param dateType    날짜 타입
        * @param projectName 프로젝트명
        * @param result      결과
        * @param searchType  검색 타입
        * @param searchValue 검색 값
        * @param fromDate    시작 날짜
        * @param toDate      종료 날짜
        * @param pageable    페이징 정보
        * @return 사용자 사용량 관리 목록
        */
       @Query(value = "SELECT u.SEQ_NO, COALESCE(gum.JKW_NM, u.UUID) as UUID, gum.JKW_NM, " +
                     "COALESCE(gpm.GPO_PRJ_NM, u.GPO_PRJ_NM) as GPO_PRJ_NM, u.GPO_PRJ_NM as GPO_PRJ_ID, u.GPO_ROLE_NM, "
                     +
                     "u.MENU_LINK_PATH, u.ACTN_NM, u.HMK_NM, u.RESRC_TYPE, u.API_URL, u.API_RST_CD, " +
                     "u.CLIENT_IP_NO, u.DTL_CTNT, u.RQST_CTNT, u.FIST_RQST_DTL_CTNT, u.SECD_RQST_DTL_CTNT, u.THI_RQST_DTL_CTNT, u.FRH_RQST_DTL_CTNT, "
                     +
                     "u.RESP_CTNT, u.FIST_RESP_DTL_CTNT, u.SECD_RESP_DTL_CTNT, u.THI_RESP_DTL_CTNT, u.FRH_RESP_DTL_CTNT, u.FST_CREATED_AT, u.CREATED_BY "
                     +
                     "FROM GPO_LOG_MAS u " +
                     "LEFT JOIN GPO_USERS_MAS gum ON u.UUID = gum.MEMBER_ID " +
                     "LEFT JOIN GPO_PROJECTS_MAS gpm ON u.GPO_PRJ_NM = gpm.UUID " +
                     "WHERE " +
                     "(u.HMK_NM IS NULL OR u.HMK_NM <> 'Y') AND " +
                     "(:dateType IS NULL OR " +
                     "  (:dateType = 'created' AND u.FST_CREATED_AT BETWEEN :fromDate AND :toDate)) AND " +
                     "(:projectName IS NULL OR u.GPO_PRJ_NM LIKE '%' || :projectName || '%' OR COALESCE(gpm.GPO_PRJ_NM, '') LIKE '%' || :projectName || '%') AND "
                     +
                     "(:result IS NULL OR " +
                     "  (:result = 'success' AND u.API_RST_CD IN ('200', '201', '204')) OR " +
                     "  (:result = 'fail' AND u.API_RST_CD NOT IN ('200', '201', '204'))) AND " +
                     "(:searchValue IS NULL OR " +
                     "  (:searchType IS NULL AND (" +
                     "    LOWER(u.UUID) LIKE '%' || LOWER(:searchValue) || '%' OR " +
                     "    LOWER(COALESCE(gum.JKW_NM, '')) LIKE '%' || LOWER(:searchValue) || '%' OR " +
                     "    LOWER(u.GPO_ROLE_NM) LIKE '%' || LOWER(:searchValue) || '%' OR " +
                     "    LOWER(u.API_URL) LIKE '%' || LOWER(:searchValue) || '%')) OR " +
                     "  (:searchType = '전체' AND (" +
                     "    LOWER(u.UUID) LIKE '%' || LOWER(:searchValue) || '%' OR " +
                     "    LOWER(COALESCE(gum.JKW_NM, '')) LIKE '%' || LOWER(:searchValue) || '%' OR " +
                     "    LOWER(u.GPO_ROLE_NM) LIKE '%' || LOWER(:searchValue) || '%' OR " +
                     "    LOWER(u.API_URL) LIKE '%' || LOWER(:searchValue) || '%')) OR " +
                     "  (:searchType = 'userName' AND (LOWER(u.UUID) LIKE '%' || LOWER(:searchValue) || '%' OR LOWER(COALESCE(gum.JKW_NM, '')) LIKE '%' || LOWER(:searchValue) || '%')) OR "
                     +
                     "  (:searchType = 'roleName' AND LOWER(u.GPO_ROLE_NM) LIKE '%' || LOWER(:searchValue) || '%') OR "
                     +
                     "  (:searchType = 'apiUrl' AND LOWER(u.API_URL) LIKE '%' || LOWER(:searchValue) || '%') OR " +
                     "  (:searchType = 'apiEndpoint' AND LOWER(u.API_URL) LIKE (LOWER(:searchValue) || '%'))) " +
                     "ORDER BY u.SEQ_NO DESC", countQuery = "SELECT COUNT(*) FROM GPO_LOG_MAS u " +
                                   "LEFT JOIN GPO_USERS_MAS gum ON u.UUID = gum.MEMBER_ID " +
                                   "LEFT JOIN GPO_PROJECTS_MAS gpm ON u.GPO_PRJ_NM = gpm.UUID " +
                                   "WHERE " +
                                   "(u.HMK_NM IS NULL OR u.HMK_NM <> 'Y') AND " +
                                   "(:dateType IS NULL OR " +
                                   "  (:dateType = 'created' AND u.FST_CREATED_AT BETWEEN :fromDate AND :toDate)) AND "
                                   +
                                   "(:projectName IS NULL OR u.GPO_PRJ_NM LIKE '%' || :projectName || '%' OR COALESCE(gpm.GPO_PRJ_NM, '') LIKE '%' || :projectName || '%') AND "
                                   +
                                   "(:result IS NULL OR " +
                                   "  (:result = 'success' AND u.API_RST_CD IN ('200', '201', '204')) OR " +
                                   "  (:result = 'fail' AND u.API_RST_CD NOT IN ('200', '201', '204'))) AND " +
                                   "(:searchValue IS NULL OR " +
                                   "  (:searchType IS NULL AND (" +
                                   "    LOWER(u.UUID) LIKE '%' || LOWER(:searchValue) || '%' OR " +
                                   "    LOWER(COALESCE(gum.JKW_NM, '')) LIKE '%' || LOWER(:searchValue) || '%' OR " +
                                   "    LOWER(u.GPO_ROLE_NM) LIKE '%' || LOWER(:searchValue) || '%' OR " +
                                   "    LOWER(u.API_URL) LIKE '%' || LOWER(:searchValue) || '%')) OR " +
                                   "  (:searchType = '전체' AND (" +
                                   "    LOWER(u.UUID) LIKE '%' || LOWER(:searchValue) || '%' OR " +
                                   "    LOWER(COALESCE(gum.JKW_NM, '')) LIKE '%' || LOWER(:searchValue) || '%' OR " +
                                   "    LOWER(u.GPO_ROLE_NM) LIKE '%' || LOWER(:searchValue) || '%' OR " +
                                   "    LOWER(u.API_URL) LIKE '%' || LOWER(:searchValue) || '%')) OR " +
                                   "  (:searchType = 'userName' AND (LOWER(u.UUID) LIKE '%' || LOWER(:searchValue) || '%' OR LOWER(COALESCE(gum.JKW_NM, '')) LIKE '%' || LOWER(:searchValue) || '%')) OR "
                                   +
                                   "  (:searchType = 'roleName' AND LOWER(u.GPO_ROLE_NM) LIKE '%' || LOWER(:searchValue) || '%') OR "
                                   +
                                   "  (:searchType = 'apiUrl' AND LOWER(u.API_URL) LIKE '%' || LOWER(:searchValue) || '%') OR "
                                   +
                                   "  (:searchType = 'apiEndpoint' AND LOWER(u.API_URL) LIKE (LOWER(:searchValue) || '%')))", nativeQuery = true)
       Page<UserUsageMgmt> findBySearchConditions(
                     @Param("dateType") String dateType,
                     @Param("projectName") String projectName,
                     @Param("result") String result,
                     @Param("searchType") String searchType,
                     @Param("searchValue") String searchValue,
                     @Param("fromDate") LocalDateTime fromDate,
                     @Param("toDate") LocalDateTime toDate,
                     Pageable pageable);

       /**
        * 사용자 사용량 관리 전체 조회 (최신순 정렬) - 내보내기용
        * 
        * @return 전체 사용자 사용량 관리 목록 (최신순)
        */
       @Query(value = "SELECT u.SEQ_NO, COALESCE(gum.JKW_NM, u.UUID) as UUID, gum.JKW_NM, " +
                     "COALESCE(gpm.GPO_PRJ_NM, u.GPO_PRJ_NM) as GPO_PRJ_NM, u.GPO_PRJ_NM as GPO_PRJ_ID, u.GPO_ROLE_NM, "
                     +
                     "u.MENU_LINK_PATH, u.ACTN_NM, u.HMK_NM, u.RESRC_TYPE, u.API_URL, u.API_RST_CD, " +
                     "u.CLIENT_IP_NO, u.DTL_CTNT, u.RQST_CTNT, u.FIST_RQST_DTL_CTNT, u.SECD_RQST_DTL_CTNT, u.THI_RQST_DTL_CTNT, u.FRH_RQST_DTL_CTNT, "
                     +
                     "u.RESP_CTNT, u.FIST_RESP_DTL_CTNT, u.SECD_RESP_DTL_CTNT, u.THI_RESP_DTL_CTNT, u.FRH_RESP_DTL_CTNT, u.FST_CREATED_AT, u.CREATED_BY "
                     +
                     "FROM GPO_LOG_MAS u " +
                     "LEFT JOIN GPO_USERS_MAS gum ON u.UUID = gum.MEMBER_ID " +
                     "LEFT JOIN GPO_PROJECTS_MAS gpm ON u.GPO_PRJ_NM = gpm.UUID " +
                     "ORDER BY u.SEQ_NO DESC", nativeQuery = true)
       List<UserUsageMgmt> findAllByOrderByCreatedAtDesc();

       /**
        * 선택된 ID 목록으로 사용자 사용량 관리 조회 (최신순 정렬)
        * 
        * @param ids 조회할 로그 ID 목록
        * @return 선택된 사용자 사용량 관리 목록 (최신순)
        */
       @Query(value = "SELECT u.SEQ_NO, COALESCE(gum.JKW_NM, u.UUID) as UUID, gum.JKW_NM, " +
                     "COALESCE(gpm.GPO_PRJ_NM, u.GPO_PRJ_NM) as GPO_PRJ_NM, u.GPO_PRJ_NM as GPO_PRJ_ID, u.GPO_ROLE_NM, "
                     +
                     "u.MENU_LINK_PATH, u.ACTN_NM, u.HMK_NM, u.RESRC_TYPE, u.API_URL, u.API_RST_CD, " +
                     "u.CLIENT_IP_NO, u.DTL_CTNT, u.RQST_CTNT, u.FIST_RQST_DTL_CTNT, u.SECD_RQST_DTL_CTNT, u.THI_RQST_DTL_CTNT, u.FRH_RQST_DTL_CTNT, "
                     +
                     "u.RESP_CTNT, u.FIST_RESP_DTL_CTNT, u.SECD_RESP_DTL_CTNT, u.THI_RESP_DTL_CTNT, u.FRH_RESP_DTL_CTNT, u.FST_CREATED_AT, u.CREATED_BY "
                     +
                     "FROM GPO_LOG_MAS u " +
                     "LEFT JOIN GPO_USERS_MAS gum ON u.UUID = gum.MEMBER_ID " +
                     "LEFT JOIN GPO_PROJECTS_MAS gpm ON u.GPO_PRJ_NM = gpm.UUID " +
                     "WHERE u.SEQ_NO IN (:ids) " +
                     "AND (u.HMK_NM IS NULL OR u.HMK_NM <> 'Y') " +
                     "ORDER BY u.SEQ_NO DESC", nativeQuery = true)
       List<UserUsageMgmt> findByIdInOrderByCreatedAtDesc(@Param("ids") List<Long> ids);

       /**
        * 월별 로그인 성공 건수 조회 (그룹바이)
        * 
        * <p>
        * Tibero/Oracle 호환을 위해 TRUNC 함수를 사용합니다.
        * 동일 사용자(uuid)와 동일 프로젝트(GPO_PRJ_NM) 조합을 하루에 1번만 카운트하고, 월별로 합산합니다.
        * </p>
        * 
        * @param startDate   시작일시
        * @param endDate     종료일시
        * @param projectType 프로젝트 타입 (선택사항)
        * @return 월별 로그인 성공 건수 목록
        */
       @Query(value = "SELECT " +
                     "    TO_CHAR(TRUNC(daily.day_date, 'MM'), 'YYYY-MM') AS month, " +
                     "    NVL(SUM(daily_count), 0) AS count " +
                     "FROM ( " +
                     "    SELECT " +
                     "        TRUNC(u.FST_CREATED_AT) AS day_date, " +
                     "        COUNT(DISTINCT u.UUID || '|' || NVL(u.GPO_PRJ_NM, '')) AS daily_count " +
                     "    FROM GPO_LOG_MAS u " +
                     "    LEFT JOIN GPO_PROJECTS_MAS p ON u.GPO_PRJ_NM = p.UUID " +
                     "    WHERE u.FST_CREATED_AT >= :startDate " +
                     "      AND u.FST_CREATED_AT < :endDate " +
                     "      AND (u.HMK_NM IS NULL OR u.HMK_NM <> 'Y') " +
                     "      AND u.API_RST_CD IN ('200', '201', '204') " +
                     "      AND u.API_URL LIKE '%auth/users/exchange%' " +
                     "      AND u.UUID IS NOT NULL " +
                     "      AND u.GPO_PRJ_NM IS NOT NULL " +
                     "      AND (:projectType IS NULL OR :projectType = 'ALL' OR u.GPO_PRJ_NM = :projectType OR NVL(p.GPO_PRJ_NM, '') = :projectType) "
                     +
                     "    GROUP BY TRUNC(u.FST_CREATED_AT) " +
                     ") daily " +
                     "GROUP BY TRUNC(daily.day_date, 'MM') " +
                     "ORDER BY TRUNC(daily.day_date, 'MM')", nativeQuery = true)
       List<Object[]> countLoginSuccessByMonthGroupBy(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate,
                     @Param("projectType") String projectType);

       /**
        * 월별 로그인 성공 건수 조회 (그룹바이) - PostgreSQL용
        * 
        * <p>
        * PostgreSQL 호환을 위해 DATE_TRUNC 함수를 사용합니다.
        * 동일 사용자(uuid)와 동일 프로젝트(GPO_PRJ_NM) 조합을 하루에 1번만 카운트하고, 월별로 합산합니다.
        * </p>
        * 
        * @param startDate   시작일시
        * @param endDate     종료일시
        * @param projectType 프로젝트 타입 (선택사항)
        * @return 월별 로그인 성공 건수 목록
        */
       @Query(value = "SELECT " +
                     "    TO_CHAR(DATE_TRUNC('month', daily.day_date), 'YYYY-MM') AS month, " +
                     "    COALESCE(SUM(daily_count), 0) AS count " +
                     "FROM ( " +
                     "    SELECT " +
                     "        DATE_TRUNC('day', u.FST_CREATED_AT) AS day_date, " +
                     "        COUNT(DISTINCT (u.UUID, u.GPO_PRJ_NM)) AS daily_count " +
                     "    FROM GPO_LOG_MAS u " +
                     "    LEFT JOIN GPO_PROJECTS_MAS p ON u.GPO_PRJ_NM = p.UUID " +
                     "    WHERE u.FST_CREATED_AT >= :startDate " +
                     "      AND u.FST_CREATED_AT < :endDate " +
                     "      AND (u.HMK_NM IS NULL OR u.HMK_NM <> 'Y') " +
                     "      AND u.API_RST_CD IN ('200', '201', '204') " +
                     "      AND u.API_URL LIKE '%auth/users/exchange%' " +
                     "      AND u.UUID IS NOT NULL " +
                     "      AND u.GPO_PRJ_NM IS NOT NULL " +
                     "      AND (:projectType IS NULL OR :projectType = 'ALL' OR u.GPO_PRJ_NM = :projectType OR COALESCE(p.GPO_PRJ_NM, '') = :projectType) "
                     +
                     "    GROUP BY DATE_TRUNC('day', u.FST_CREATED_AT) " +
                     ") daily " +
                     "GROUP BY DATE_TRUNC('month', daily.day_date) " +
                     "ORDER BY DATE_TRUNC('month', daily.day_date)", nativeQuery = true)
       List<Object[]> countLoginSuccessByMonthGroupByPostgresql(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate,
                     @Param("projectType") String projectType);

       /**
        * 특정 월의 API 호출 성공 건수 조회
        * 
        * @param startDate   시작일시 (해당월 1일)
        * @param endDate     종료일시 (해당월 말일)
        * @param projectType 프로젝트 타입 (선택사항)
        * @return API 호출 성공 건수
        */
       @Query("SELECT COUNT(u) FROM UserUsageMgmt u LEFT JOIN Project p ON p.uuid = u.projectName WHERE " +
                     "u.createdAt >= :startDate AND u.createdAt < :endDate AND " +
                     "(u.targetAsset IS NULL OR u.targetAsset <> 'Y') AND " +
                     "u.errCode IN ('200', '201', '204') " +
                     "AND (:projectType IS NULL OR :projectType = 'ALL' OR u.projectName = :projectType OR (p.prjNm IS NOT NULL AND p.prjNm = :projectType))")
       Long countApiSuccessByMonth(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate,
                     @Param("projectType") String projectType);

       /**
        * 특정 월의 API 호출 실패 건수 조회
        * 
        * @param startDate   시작일시 (해당월 1일)
        * @param endDate     종료일시 (해당월 말일)
        * @param projectType 프로젝트 타입 (선택사항)
        * @return API 호출 실패 건수
        */
       @Query("SELECT COUNT(u) FROM UserUsageMgmt u LEFT JOIN Project p ON p.uuid = u.projectName WHERE " +
                     "u.createdAt >= :startDate AND u.createdAt < :endDate AND " +
                     "(u.targetAsset IS NULL OR u.targetAsset <> 'Y') AND " +
                     "u.errCode NOT IN ('200', '201', '204') " +
                     "AND (:projectType IS NULL OR :projectType = 'ALL' OR u.projectName = :projectType OR (p.prjNm IS NOT NULL AND p.prjNm = :projectType))")
       Long countApiFailureByMonth(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate,
                     @Param("projectType") String projectType);

       /**
        * API 호출 실패 요약 데이터 조회 (최신순 6개)
        * 
        * @param startDate   시작일
        * @param endDate     종료일
        * @param projectType 프로젝트 타입 (선택사항)
        * @return API 호출 실패 요약 데이터 목록
        */
       @Query("SELECT u.createdAt, u.menuPath, u.errCode " +
                     "FROM UserUsageMgmt u LEFT JOIN Project p ON p.uuid = u.projectName WHERE " +
                     "u.createdAt >= :startDate AND u.createdAt < :endDate " +
                     "AND (u.targetAsset IS NULL OR u.targetAsset <> 'Y') " +
                     "AND u.errCode NOT IN ('200', '201', '204') " +
                     "AND (:projectType IS NULL OR :projectType = 'ALL' OR u.projectName = :projectType OR (p.prjNm IS NOT NULL AND p.prjNm = :projectType)) "
                     +
                     "ORDER BY u.createdAt DESC " +
                     "FETCH FIRST 6 ROWS ONLY")
       List<Object[]> findRecentApiFailures(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate,
                     @Param("projectType") String projectType);

       /**
        * 가장 많이 사용한 메뉴 조회 (상위 5개) - Tibero용
        * menuPath를 상위 2단계까지만 가공하여 그룹화 (/1deps/2deps/3deps -> /1deps/2deps)
        * 
        * @param startDate   시작일
        * @param endDate     종료일
        * @param projectType 프로젝트 타입 (선택사항)
        * @return 메뉴별 사용량 통계
        */
       @Query(value = "SELECT " +
                     "    CASE " +
                     "        WHEN INSTR(u.MENU_LINK_PATH, '/', 1, 3) > 0 " +
                     "        THEN SUBSTR(u.MENU_LINK_PATH, 1, INSTR(u.MENU_LINK_PATH, '/', 1, 3) - 1) " +
                     "        ELSE u.MENU_LINK_PATH " +
                     "    END AS menuPath, " +
                     "    COUNT(*) as count " +
                     "FROM GPO_LOG_MAS u " +
                     "LEFT JOIN GPO_PROJECTS_MAS p ON u.GPO_PRJ_NM = p.UUID " +
                     "WHERE u.FST_CREATED_AT >= :startDate AND u.FST_CREATED_AT < :endDate " +
                     "AND (:projectType IS NULL OR :projectType = 'ALL' OR u.GPO_PRJ_NM = :projectType OR (p.GPO_PRJ_NM IS NOT NULL AND p.GPO_PRJ_NM = :projectType)) "
                     +
                     "AND u.MENU_LINK_PATH IS NOT NULL " +
                     "AND u.HMK_NM = 'Y' " +
                     "GROUP BY " +
                     "    CASE " +
                     "        WHEN INSTR(u.MENU_LINK_PATH, '/', 1, 3) > 0 " +
                     "        THEN SUBSTR(u.MENU_LINK_PATH, 1, INSTR(u.MENU_LINK_PATH, '/', 1, 3) - 1) " +
                     "        ELSE u.MENU_LINK_PATH " +
                     "    END " +
                     "ORDER BY count DESC " +
                     "FETCH FIRST 5 ROWS ONLY", nativeQuery = true)
       List<Object[]> findTopUsedMenus(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate,
                     @Param("projectType") String projectType);

       /**
        * 가장 많이 사용한 메뉴 조회 (상위 5개) - PostgreSQL용
        * menuPath를 상위 2단계까지만 가공하여 그룹화 (/1deps/2deps/3deps -> /1deps/2deps)
        * 
        * @param startDate   시작일
        * @param endDate     종료일
        * @param projectType 프로젝트 타입 (선택사항)
        * @return 메뉴별 사용량 통계
        */
       @Query(value = "SELECT " +
                     "    CASE " +
                     "        WHEN SPLIT_PART(u.MENU_LINK_PATH, '/', 4) != '' THEN " +
                     "            '/' || SPLIT_PART(u.MENU_LINK_PATH, '/', 2) || '/' || SPLIT_PART(u.MENU_LINK_PATH, '/', 3) "
                     +
                     "        WHEN SPLIT_PART(u.MENU_LINK_PATH, '/', 3) != '' THEN " +
                     "            '/' || SPLIT_PART(u.MENU_LINK_PATH, '/', 2) || '/' || SPLIT_PART(u.MENU_LINK_PATH, '/', 3) "
                     +
                     "        WHEN SPLIT_PART(u.MENU_LINK_PATH, '/', 2) != '' THEN " +
                     "            '/' || SPLIT_PART(u.MENU_LINK_PATH, '/', 2) " +
                     "        ELSE u.MENU_LINK_PATH " +
                     "    END AS menuPath, " +
                     "    COUNT(*) as count " +
                     "FROM GPO_LOG_MAS u " +
                     "LEFT JOIN GPO_PROJECTS_MAS p ON u.GPO_PRJ_NM = p.UUID " +
                     "WHERE u.FST_CREATED_AT >= :startDate AND u.FST_CREATED_AT < :endDate " +
                     "AND (:projectType IS NULL OR :projectType = 'ALL' OR u.GPO_PRJ_NM = :projectType OR (p.GPO_PRJ_NM IS NOT NULL AND p.GPO_PRJ_NM = :projectType)) "
                     +
                     "AND u.MENU_LINK_PATH IS NOT NULL " +
                     "AND u.HMK_NM = 'Y' " +
                     "GROUP BY " +
                     "    CASE " +
                     "        WHEN SPLIT_PART(u.MENU_LINK_PATH, '/', 4) != '' THEN " +
                     "            '/' || SPLIT_PART(u.MENU_LINK_PATH, '/', 2) || '/' || SPLIT_PART(u.MENU_LINK_PATH, '/', 3) "
                     +
                     "        WHEN SPLIT_PART(u.MENU_LINK_PATH, '/', 3) != '' THEN " +
                     "            '/' || SPLIT_PART(u.MENU_LINK_PATH, '/', 2) || '/' || SPLIT_PART(u.MENU_LINK_PATH, '/', 3) "
                     +
                     "        WHEN SPLIT_PART(u.MENU_LINK_PATH, '/', 2) != '' THEN " +
                     "            '/' || SPLIT_PART(u.MENU_LINK_PATH, '/', 2) " +
                     "        ELSE u.MENU_LINK_PATH " +
                     "    END " +
                     "ORDER BY count DESC " +
                     "LIMIT 5", nativeQuery = true)
       List<Object[]> findTopUsedMenusPostgresql(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate,
                     @Param("projectType") String projectType);

       /**
        * 주별 로그인 성공 건수 조회 (12주간, -7일 단위)
        * 
        * <p>
        * Tibero/Oracle 호환을 위해 CONNECT BY를 사용합니다.
        * 동일 사용자(uuid)와 동일 프로젝트(GPO_PRJ_NM) 조합을 하루에 1번만 카운트하고, 주간으로 합산합니다.
        * </p>
        * 
        * @param selectedDate 선택된 날짜
        * @param projectType  프로젝트 타입 (선택사항)
        * @return 주별 로그인 성공 건수
        */
       @Query(value = "SELECT " +
                     "    b.start_date AS group_start, " +
                     "    NVL(SUM(daily_count), 0) AS login_success_count " +
                     "FROM (" +
                     "    SELECT TRUNC(TO_DATE(:selectedDate, 'YYYY-MM-DD')) - (LEVEL - 1) * 7 AS start_date " +
                     "    FROM DUAL " +
                     "    CONNECT BY LEVEL <= 12" +
                     ") b " +
                     "LEFT JOIN ( " +
                     "    SELECT " +
                     "        TRUNC(u.FST_CREATED_AT) AS day_date, " +
                     "        COUNT(DISTINCT u.uuid || '|' || NVL(u.GPO_PRJ_NM, '')) AS daily_count " +
                     "    FROM GPO_LOG_MAS u " +
                     "    LEFT JOIN GPO_PROJECTS_MAS gpm ON u.GPO_PRJ_NM = gpm.UUID " +
                     "    WHERE u.API_RST_CD IN ('200', '201', '204') " +
                     "      AND u.API_URL LIKE '%auth/users/exchange%' " +
                     "      AND (u.HMK_NM IS NULL OR u.HMK_NM <> 'Y') " +
                     "      AND u.uuid IS NOT NULL " +
                     "      AND u.GPO_PRJ_NM IS NOT NULL " +
                     "      AND (:projectType IS NULL OR :projectType = 'ALL' OR u.GPO_PRJ_NM = :projectType OR COALESCE(gpm.GPO_PRJ_NM, '') = :projectType) "
                     +
                     "    GROUP BY TRUNC(u.FST_CREATED_AT) " +
                     ") daily " +
                     "    ON daily.day_date BETWEEN b.start_date AND (b.start_date + 6) " +
                     "GROUP BY b.start_date " +
                     "ORDER BY b.start_date DESC", nativeQuery = true)
       List<Object[]> countLoginSuccessByWeekGroupBy(@Param("selectedDate") String selectedDate,
                     @Param("projectType") String projectType);

       /**
        * 일별 로그인 성공 건수 조회 (12일간)
        * 
        * <p>
        * Tibero/Oracle 호환을 위해 CONNECT BY를 사용하며, 모든 날짜를 포함합니다.
        * 동일 사용자(uuid)와 동일 프로젝트(GPO_PRJ_NM) 조합을 하루에 1번만 카운트합니다.
        * </p>
        * 
        * @param selectedDate 선택된 날짜
        * @param projectType  프로젝트 타입 (선택사항)
        * @return 일별 로그인 성공 건수
        */
       @Query(value = "SELECT " +
                     "    TO_CHAR(b.day_date, 'YYYY-MM-DD') AS day, " +
                     "    NVL(COUNT(DISTINCT CASE WHEN u.uuid IS NOT NULL AND u.GPO_PRJ_NM IS NOT NULL THEN u.uuid || '|' || NVL(u.GPO_PRJ_NM, '') END), 0) AS login_success_count " +
                     "FROM (" +
                     "    SELECT TRUNC(TO_DATE(:selectedDate, 'YYYY-MM-DD')) - (LEVEL - 1) AS day_date " +
                     "    FROM DUAL " +
                     "    CONNECT BY LEVEL <= 12" +
                     ") b " +
                     "LEFT JOIN ( " +
                     "    SELECT u_inner.*, gpm.GPO_PRJ_NM AS GPO_PRJ_KO_NM " +
                     "    FROM GPO_LOG_MAS u_inner " +
                     "    LEFT JOIN GPO_PROJECTS_MAS gpm ON u_inner.GPO_PRJ_NM = gpm.UUID " +
                     ") u " +
                     "    ON u.API_RST_CD IN ('200', '201', '204') " +
                     "   AND u.API_URL LIKE '%auth/users/exchange%' " +
                     "   AND TRUNC(u.FST_CREATED_AT) = b.day_date " +
                     "   AND (:projectType IS NULL OR :projectType = 'ALL' OR u.GPO_PRJ_NM = :projectType OR COALESCE(u.GPO_PRJ_KO_NM, '') = :projectType) "
                     +
                     "   AND u.uuid IS NOT NULL " +
                     "   AND u.GPO_PRJ_NM IS NOT NULL " +
                     "GROUP BY b.day_date " +
                     "ORDER BY b.day_date DESC", nativeQuery = true)
       List<Object[]> countLoginSuccessByDayGroupBy(@Param("selectedDate") String selectedDate,
                     @Param("projectType") String projectType);

       /**
        * 주별 로그인 성공 건수 조회 (12주간, -7일 단위) - PostgreSQL용
        * 
        * <p>
        * PostgreSQL 호환을 위해 generate_series를 사용합니다.
        * 동일 사용자(uuid)와 동일 프로젝트(GPO_PRJ_NM) 조합을 하루에 1번만 카운트하고, 주간으로 합산합니다.
        * </p>
        * 
        * @param selectedDate 선택된 날짜
        * @param projectType  프로젝트 타입 (선택사항)
        * @return 주별 로그인 성공 건수
        */
       @Query(value = "SELECT " +
                     "    b.start_date AS group_start, " +
                     "    COALESCE(SUM(daily_count), 0) AS login_success_count " +
                     "FROM (" +
                     "    SELECT CAST(:selectedDate AS DATE) - (generate_series - 1) * INTERVAL '7 days' AS start_date "
                     +
                     "    FROM generate_series(1, 12)" +
                     ") b " +
                     "LEFT JOIN ( " +
                     "    SELECT " +
                     "        DATE(u.FST_CREATED_AT) AS day_date, " +
                     "        COUNT(DISTINCT (u.uuid, u.GPO_PRJ_NM)) AS daily_count " +
                     "    FROM GPO_LOG_MAS u " +
                     "    LEFT JOIN GPO_PROJECTS_MAS gpm ON u.GPO_PRJ_NM = gpm.UUID " +
                     "    WHERE u.API_RST_CD IN ('200', '201', '204') " +
                     "      AND u.API_URL LIKE '%auth/users/exchange%' " +
                     "      AND u.uuid IS NOT NULL " +
                     "      AND u.GPO_PRJ_NM IS NOT NULL " +
                     "      AND (:projectType IS NULL OR :projectType = 'ALL' OR u.GPO_PRJ_NM = :projectType OR COALESCE(gpm.GPO_PRJ_NM, '') = :projectType) "
                     +
                     "    GROUP BY DATE(u.FST_CREATED_AT) " +
                     ") daily " +
                     "    ON daily.day_date BETWEEN (b.start_date - INTERVAL '6 days') AND b.start_date " +
                     "GROUP BY b.start_date " +
                     "ORDER BY b.start_date DESC", nativeQuery = true)
       List<Object[]> countLoginSuccessByWeekGroupByPostgresql(@Param("selectedDate") String selectedDate,
                     @Param("projectType") String projectType);

       /**
        * 일별 로그인 성공 건수 조회 (12일간) - PostgreSQL용
        * 
        * <p>
        * PostgreSQL 호환을 위해 generate_series를 사용하며, 모든 날짜를 포함합니다.
        * 동일 사용자(uuid)와 동일 프로젝트(GPO_PRJ_NM) 조합을 하루에 1번만 카운트합니다.
        * </p>
        * 
        * @param selectedDate 선택된 날짜
        * @param projectType  프로젝트 타입 (선택사항)
        * @return 일별 로그인 성공 건수
        */
       @Query(value = "SELECT " +
                     "    TO_CHAR(b.day_date, 'YYYY-MM-DD') AS day, " +
                     "    COALESCE(COUNT(DISTINCT CASE WHEN u.uuid IS NOT NULL AND u.GPO_PRJ_NM IS NOT NULL THEN (u.uuid, u.GPO_PRJ_NM) END), 0) AS login_success_count " +
                     "FROM (" +
                     "    SELECT CAST(:selectedDate AS DATE) - (generate_series - 1) AS day_date " +
                     "    FROM generate_series(1, 12)" +
                     ") b " +
                     "LEFT JOIN ( " +
                     "    SELECT u_inner.*, gpm.GPO_PRJ_NM AS GPO_PRJ_KO_NM " +
                     "    FROM GPO_LOG_MAS u_inner " +
                     "    LEFT JOIN GPO_PROJECTS_MAS gpm ON u_inner.GPO_PRJ_NM = gpm.UUID " +
                     ") u " +
                     "    ON u.API_RST_CD IN ('200', '201', '204') " +
                     "   AND u.API_URL LIKE '%auth/users/exchange%' " +
                     "   AND DATE(u.FST_CREATED_AT) = b.day_date " +
                     "   AND (:projectType IS NULL OR :projectType = 'ALL' OR u.GPO_PRJ_NM = :projectType OR COALESCE(u.GPO_PRJ_KO_NM, '') = :projectType) "
                     +
                     "   AND u.uuid IS NOT NULL " +
                     "   AND u.GPO_PRJ_NM IS NOT NULL " +
                     "GROUP BY b.day_date " +
                     "ORDER BY b.day_date DESC", nativeQuery = true)
       List<Object[]> countLoginSuccessByDayGroupByPostgresql(@Param("selectedDate") String selectedDate,
                     @Param("projectType") String projectType);

       /**
        * 생성일시가 지정된 날짜 이전인 사용자 사용량 관리 데이터 삭제
        * 
        * @param beforeDate 삭제할 기준 날짜 (이 날짜 이전 데이터 삭제)
        */
       @Modifying
       @Query("DELETE FROM UserUsageMgmt u WHERE u.createdAt < :beforeDate")
       void deleteByCreatedAtBefore(@Param("beforeDate") LocalDateTime beforeDate);

}
