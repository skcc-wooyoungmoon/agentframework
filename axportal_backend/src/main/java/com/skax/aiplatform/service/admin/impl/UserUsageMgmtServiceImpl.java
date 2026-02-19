package com.skax.aiplatform.service.admin.impl;

import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.util.ExcelUtils;
import com.skax.aiplatform.dto.admin.request.UserUsageMgmtReq;
import com.skax.aiplatform.dto.admin.response.ProjectRes;
import com.skax.aiplatform.dto.admin.response.UserUsageMgmtRes;
import com.skax.aiplatform.dto.admin.response.UserUsageMgmtStatsRes;
import com.skax.aiplatform.entity.UserUsageMgmt;
import com.skax.aiplatform.entity.project.Project;
import com.skax.aiplatform.entity.project.ProjectStatus;
import com.skax.aiplatform.mapper.admin.UserUsageMgmtMapper;
import com.skax.aiplatform.repository.admin.UserUsageMgmtRepository;
import com.skax.aiplatform.repository.home.GpoProjectsRepository;
import com.skax.aiplatform.service.admin.UserUsageMgmtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUsageMgmtServiceImpl implements UserUsageMgmtService {

    private static final int TARGET_ASSET_MAX_LENGTH = 66;

    private final UserUsageMgmtRepository userUsageMgmtRepository;
    private final UserUsageMgmtMapper userUsageMgmtMapper;
    private final GpoProjectsRepository gpoProjectsRepository;

    @Value("${spring.datasource.driver-class-name:}")
    private String driverClassName;

    /**
     * 외부망 환경인지 확인합니다.
     *
     * @return true이면 외부망(PostgreSQL), false이면 내부망(Tibero)
     */
    private boolean isPostgre() {
        // driverClassName이 Tibero 드라이버이면 내부망(Tibero)
        if ("com.tmax.tibero.jdbc.TbDriver".equals(driverClassName)) {
            log.debug("Tibero 환경 감지 (Driver: {})", driverClassName);
            return false;
        }

        // 그 외(비어있거나 PostgreSQL 등)는 외부망(PostgreSQL)
        log.debug("PostgreSQL 환경 감지 (Driver: {})", driverClassName);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserUsageMgmtRes createUserUsageMgmt(UserUsageMgmtReq userUsageMgmt) {
        try {
            UserUsageMgmt entity = userUsageMgmtMapper.toEntity(userUsageMgmt);
            // projectName(UUID)가 비어있거나 null이면 null로 설정
            if (entity.getProjectName() != null && entity.getProjectName().trim().isEmpty()) {
                entity.setProjectName(null);
            }
            applyRequestDetails(entity, userUsageMgmt.getRequestContent());
            applyResponseDetails(entity, userUsageMgmt.getResponseContent());
            enforceFieldConstraints(entity);
            UserUsageMgmt savedEntity = userUsageMgmtRepository.save(entity);

            log.debug("사용자 사용량 관리 저장 성공: user={}, action={}, errCode={}",
                    userUsageMgmt.getUserName(), userUsageMgmt.getAction(), userUsageMgmt.getErrCode());

            return mapToResponseWithDetails(savedEntity);
        } catch (org.springframework.dao.DataAccessException e) {
            // 데이터베이스 스키마 변경으로 인한 insert 오류는 로그만 출력하고 예외를 발생시키지 않음
            log.warn("사용자 사용량 관리 저장 실패 (데이터베이스 오류): {}", e.getMessage());
            // 원본 API 호출에 영향을 주지 않도록 null 반환
            return null;
        } catch (RuntimeException e) {
            // 기타 런타임 예외도 로그만 출력하고 예외를 발생시키지 않음
            log.warn("사용자 사용량 관리 저장 실패 (런타임 오류): {}", e.getMessage());
            // 원본 API 호출에 영향을 주지 않도록 null 반환
            return null;
        }
    }

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createUserUsageMgmtAsync(UserUsageMgmtReq userUsageMgmt) {
        try {
            UserUsageMgmt entity = userUsageMgmtMapper.toEntity(userUsageMgmt);
            // projectName(UUID)가 비어있거나 null이면 null로 설정
            if (entity.getProjectName() != null && entity.getProjectName().trim().isEmpty()) {
                entity.setProjectName(null);
            }
            applyRequestDetails(entity, userUsageMgmt.getRequestContent());
            applyResponseDetails(entity, userUsageMgmt.getResponseContent());
            enforceFieldConstraints(entity);
            userUsageMgmtRepository.save(entity);

            log.debug("비동기 사용자 사용량 관리 저장 성공: user={}, action={}, errCode={}",
                    userUsageMgmt.getUserName(), userUsageMgmt.getAction(), userUsageMgmt.getErrCode());
        } catch (org.springframework.dao.DataAccessException e) {
            log.error("비동기 사용자 사용량 관리 저장 실패 (데이터베이스 오류): {}", e.getMessage(), e);
            // 비동기 메서드에서는 예외를 던지지 않고 로깅만 함
        } catch (RuntimeException e) {
            log.error("비동기 사용자 사용량 관리 저장 실패 (런타임 오류): {}", e.getMessage(), e);
            // 비동기 메서드에서는 예외를 던지지 않고 로깅만 함
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateNearestLoginLogWithUserInfo(Long currentLogId, String userName, String userInfo) {
        try {
            // 현재 로그에서 사용자명과 API 엔드포인트 추출
            UserUsageMgmt currentLog = userUsageMgmtRepository.findById(currentLogId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "로그를 찾을 수 없습니다."));

            String apiEndpoint = currentLog.getApiEndpoint();

            // 가장 가까운 로그인 로그 찾기
            UserUsageMgmt nearestLoginLog = userUsageMgmtRepository
                    .findTopByUserNameAndApiEndpointAndIdLessThanOrderByIdDesc(userName, apiEndpoint, currentLogId)
                    .orElse(null);

            if (nearestLoginLog != null) {
                // 사용자 정보 업데이트
                nearestLoginLog.setUserName(userName);
                // userInfo는 별도 필드로 저장하거나 로그에만 기록
                userUsageMgmtRepository.save(nearestLoginLog);

                log.debug("가장 가까운 로그인 로그 업데이트 완료: logId={}, userName={}",
                        nearestLoginLog.getId(), userName);
            } else {
                log.debug("업데이트할 로그인 로그를 찾을 수 없음: userName={}, apiEndpoint={}",
                        userName, apiEndpoint);
            }
        } catch (org.springframework.dao.DataAccessException e) {
            log.error("가장 가까운 로그인 로그 업데이트 실패 (데이터베이스 오류): {}", e.getMessage(), e);
            // 이 메서드는 실패해도 원본 로직에 영향을 주지 않도록 예외를 던지지 않음
        } catch (RuntimeException e) {
            log.error("가장 가까운 로그인 로그 업데이트 실패 (런타임 오류): {}", e.getMessage(), e);
            // 이 메서드는 실패해도 원본 로직에 영향을 주지 않도록 예외를 던지지 않음
        }
    }

    @Override
    @Transactional
    public UserUsageMgmtRes updateUserUsageMgmt(UserUsageMgmtReq userUsageMgmt) {
        try {
            UserUsageMgmt existingEntity = userUsageMgmtRepository.findById(Long.valueOf(userUsageMgmt.getId()))
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "사용자 사용량 관리를 찾을 수 없습니다."));

            // 기존 엔티티 업데이트
            existingEntity.setUserName(userUsageMgmt.getUserName());
            // projectName(UUID)가 비어있거나 null이면 null로 설정
            String projectName = userUsageMgmt.getProjectName();
            existingEntity.setProjectName((projectName != null && !projectName.trim().isEmpty()) ? projectName : null);
            existingEntity.setRoleName(userUsageMgmt.getRoleName());
            existingEntity.setMenuPath(userUsageMgmt.getMenuPath());
            existingEntity.setAction(userUsageMgmt.getAction());
            existingEntity.setTargetAsset(truncateContent(userUsageMgmt.getTargetAsset(), TARGET_ASSET_MAX_LENGTH));
            existingEntity.setResourceType(userUsageMgmt.getResourceType());
            existingEntity.setApiEndpoint(userUsageMgmt.getApiEndpoint());
            existingEntity.setErrCode(userUsageMgmt.getErrCode());
            existingEntity.setClientIp(userUsageMgmt.getClientIp());
            existingEntity.setUserAgent(userUsageMgmt.getUserAgent());

            UserUsageMgmt savedEntity = userUsageMgmtRepository.save(existingEntity);

            log.debug("사용자 사용량 관리 수정 성공: id={}, user={}",
                    userUsageMgmt.getId(), userUsageMgmt.getUserName());

            return userUsageMgmtMapper.toResponse(savedEntity);
        } catch (org.springframework.dao.DataAccessException e) {
            log.error("사용자 사용량 관리 수정 실패 (데이터베이스 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 사용량 관리 수정에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("사용자 사용량 관리 수정 실패 (런타임 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 사용량 관리 수정에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserUsageMgmtRes> getUserUsageMgmts(String dateType, String projectName, String result,
                                                    String searchType, String searchValue, String fromDate,
                                                    String toDate, Pageable pageable) {
        try {
            log.info(
                    "사용자 사용량 관리 목록 조회 - dateType: {}, projectName: {}, result: {}, searchType: {}, searchValue: {}, fromDate: {}, toDate: {}",
                    dateType, projectName, result, searchType, searchValue, fromDate, toDate);

            // 공백 문자열을 null로 변환하여 전체 조회되도록 처리 (bytea 방지)
            String processedDateType = (dateType != null && !dateType.trim().isEmpty())
                    ? String.valueOf(dateType).trim()
                    : null;
            String processedProjectName = (projectName != null && !projectName.trim().isEmpty())
                    ? String.valueOf(projectName).trim()
                    : null;
            String processedResult = (result != null && !result.trim().isEmpty()) ? String.valueOf(result).trim()
                    : null;
            String processedSearchType = (searchType != null && !searchType.trim().isEmpty())
                    ? String.valueOf(searchType).trim()
                    : null;
            String processedSearchValue = (searchValue != null && !searchValue.trim().isEmpty())
                    ? String.valueOf(searchValue).trim()
                    : null;

            // 날짜 문자열을 LocalDateTime으로 변환 (2025-09-25 13:05:42.313 형태)
            LocalDateTime fromDateTime = null;
            LocalDateTime toDateTime = null;

            if (fromDate != null && !fromDate.trim().isEmpty()) {
                try {
                    // YYYY-MM-DD 형태의 날짜를 LocalDateTime으로 변환 (00:00:00.000)
                    fromDateTime = LocalDate.parse(fromDate).atStartOfDay();
                } catch (DateTimeParseException e) {
                    log.warn("fromDate 파싱 실패: {} - {}", fromDate, e.getMessage());
                    fromDateTime = null;
                }
            }

            if (toDate != null && !toDate.trim().isEmpty()) {
                try {
                    // YYYY-MM-DD 형태의 날짜를 LocalDateTime으로 변환 (23:59:59.999)
                    toDateTime = LocalDate.parse(toDate).atTime(23, 59, 59, 999_000_000);
                } catch (DateTimeParseException e) {
                    log.warn("toDate 파싱 실패: {} - {}", toDate, e.getMessage());
                    toDateTime = null;
                }
            }

            // Repository 호출 직전 모든 변수를 명시적으로 String으로 변환
            String finalDateType = processedDateType != null ? String.valueOf(processedDateType) : null;
            String finalProjectName = processedProjectName != null ? String.valueOf(processedProjectName) : null;
            String finalResult = processedResult != null ? String.valueOf(processedResult) : null;
            String finalSearchType = processedSearchType != null ? String.valueOf(processedSearchType) : null;
            String finalSearchValue = processedSearchValue != null ? String.valueOf(processedSearchValue) : null;

            Page<UserUsageMgmt> entities = userUsageMgmtRepository.findBySearchConditions(
                    finalDateType, finalProjectName, finalResult, finalSearchType,
                    finalSearchValue, fromDateTime, toDateTime, pageable);

            // 조회된 데이터 로깅
            log.info("조회된 사용자 사용량 관리 데이터 수: {}", entities.getTotalElements());
            if (!entities.getContent().isEmpty()) {
                UserUsageMgmt firstEntity = entities.getContent().get(0);
                log.info("=== 첫 번째 Entity 데이터 샘플 ===");
                log.info("ID: {}, UserName: {}, ErrCode: {}, CreatedAt: {}",
                        firstEntity.getId(), firstEntity.getUserName(), firstEntity.getErrCode(),
                        firstEntity.getCreatedAt());
                log.info("targetAsset (HMK_NM): {}", firstEntity.getTargetAsset());
                log.info("menuPath: {}", firstEntity.getMenuPath());
                log.info("action: {}", firstEntity.getAction());
            }

            Page<UserUsageMgmtRes> responsePage = entities.map(this::mapToResponseWithDetails);

            // totalElements가 119988개 이상일 경우 119988개로 제한
            final long MAX_TOTAL_ELEMENTS = 119988L;
            if (responsePage.getTotalElements() > MAX_TOTAL_ELEMENTS) {
                log.info("totalElements가 119988개를 초과하여 제한 적용 - 원본: {}, 제한: {}",
                        responsePage.getTotalElements(), MAX_TOTAL_ELEMENTS);

                // totalElements를 119988로 제한하고, totalPages도 재계산
                int pageSize = responsePage.getSize();
                int limitedTotalPages = (int) Math.ceil((double) MAX_TOTAL_ELEMENTS / pageSize);

                responsePage = new PageImpl<>(
                        responsePage.getContent(),
                        responsePage.getPageable(),
                        MAX_TOTAL_ELEMENTS);

                log.info("totalElements 제한 적용 완료 - 제한된 totalElements: {}, 제한된 totalPages: {}",
                        MAX_TOTAL_ELEMENTS, limitedTotalPages);
            }

            // Response DTO 확인
            if (!responsePage.getContent().isEmpty()) {
                UserUsageMgmtRes firstResponse = responsePage.getContent().get(0);
                log.info("=== 첫 번째 Response DTO 데이터 샘플 ===");
                log.info("ID: {}, UserName: {}", firstResponse.getId(), firstResponse.getUserName());
                log.info("targetAsset: {}", firstResponse.getTargetAsset());
                log.info("menuPath: {}", firstResponse.getMenuPath());
                log.info("action: {}", firstResponse.getAction());
            }

            return responsePage;
        } catch (org.springframework.dao.DataAccessException e) {
            log.error("사용자 사용량 관리 목록 조회 실패 (데이터베이스 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 사용량 관리 목록 조회에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("사용자 사용량 관리 목록 조회 실패 (런타임 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 사용량 관리 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserUsageMgmtRes getUserUsageMgmtById(Long id) {
        try {
            UserUsageMgmt entity = userUsageMgmtRepository.findByIdWithUserName(id)
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "사용자 사용량 관리를 찾을 수 없습니다."));

            // Entity 값 확인 로그
            log.info("=== 사용자 이용 현황 상세 조회 - Entity 값 확인 ===");
            log.info("ID: {}", entity.getId());
            log.info("userName: {}", entity.getUserName());
            log.info("targetAsset (HMK_NM): {}", entity.getTargetAsset());
            log.info("menuPath: {}", entity.getMenuPath());
            log.info("action: {}", entity.getAction());
            log.info("apiEndpoint: {}", entity.getApiEndpoint());

            UserUsageMgmtRes response = mapToResponseWithDetails(entity);

            // Response DTO 값 확인 로그
            log.info("=== 사용자 이용 현황 상세 조회 - Response DTO 값 확인 ===");
            log.info("ID: {}", response.getId());
            log.info("userName: {}", response.getUserName());
            log.info("targetAsset: {}", response.getTargetAsset());
            log.info("menuPath: {}", response.getMenuPath());
            log.info("action: {}", response.getAction());
            log.info("apiEndpoint: {}", response.getApiEndpoint());

            return response;
        } catch (org.springframework.dao.DataAccessException e) {
            log.error("사용자 사용량 관리 상세 조회 실패 (데이터베이스 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 사용량 관리 상세 조회에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("사용자 사용량 관리 상세 조회 실패 (런타임 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 사용량 관리 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportUserUsageMgmtsWithCustomData(List<Map<String, Object>> headers,
                                                     List<Map<String, Object>> data) {
        try {
            List<Map<String, Object>> safeHeaders = headers != null ? headers : Collections.emptyList();
            List<Map<String, Object>> safeData = data != null ? data : Collections.emptyList();

            if (safeHeaders.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "엑셀 헤더 정보가 비어있습니다.");
            }
            if (safeData.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "엑셀 데이터가 비어있습니다.");
            }

            log.info("커스텀 데이터 사용자 사용량 관리 Excel 내보내기 시작 - 헤더: {}개, 데이터: {}건",
                    safeHeaders.size(), safeData.size());

            // Map을 HeaderInfo로 변환
            List<ExcelUtils.HeaderInfo> headerInfos = safeHeaders.stream()
                    .map(header -> {
                        ExcelUtils.HeaderInfo headerInfo = new ExcelUtils.HeaderInfo();
                        headerInfo.setHeaderName((String) header.get("key"));
                        headerInfo.setField((String) header.get("value"));
                        return headerInfo;
                    })
                    .collect(java.util.stream.Collectors.toList());

            return ExcelUtils.createDynamicExcel(headerInfos, safeData);

        } catch (java.io.IOException e) {
            log.error("커스텀 데이터 사용자 사용량 관리 Excel 내보내기 실패 (IO 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "커스텀 데이터 Excel 내보내기 중 IO 오류가 발생했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("커스텀 데이터 사용자 사용량 관리 Excel 내보내기 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "커스텀 데이터 Excel 내보내기 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public UserUsageMgmtStatsRes getUserUsageMgmtStats(String searchType, String selectedDate, String projectType) {
        try {
            log.info("사용자 사용량 관리 통계 조회 시작 - searchType: {}, selectedDate: {}, projectType: {}",
                    searchType, selectedDate, projectType);

            LocalDate normalizedSelectedDate = normalizeSelectedDate(selectedDate);
            String repositorySelectedDate = normalizedSelectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            log.info("============실제로 QUery에 전달되는 Date 값입니다.==================");
            log.info(repositorySelectedDate);
            log.info("==================================================================");
            // searchType에 따른 날짜 범위 계산
            LocalDateTime startDate;
            LocalDateTime endDate;
            LocalDateTime currentDate;

            if ("week".equals(searchType)) {
                // week: YYYY-MM-DD 형식, 해당 날짜부터 1일 단위로 7일 전까지
                currentDate = normalizedSelectedDate.atStartOfDay();
                endDate = currentDate.plusDays(1).minusSeconds(1); // 해당 날짜 23:59:59
                startDate = currentDate.minusDays(7); // 7일 전
            } else if ("day".equals(searchType)) {
                // day: YYYY-MM-DD 형식, 해당 날짜부터 -1일 차이로 12일 전까지
                currentDate = normalizedSelectedDate.atStartOfDay();
                endDate = currentDate.plusDays(1).minusSeconds(1); // 해당 날짜 23:59:59
                startDate = currentDate.minusDays(12); // 12일 전
            } else {
                // month: YYYY-MM-DD 형식인 경우 YYYY-MM으로 변환
                YearMonth yearMonth = YearMonth.from(normalizedSelectedDate);
                currentDate = normalizedSelectedDate.atStartOfDay();
                endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
                startDate = yearMonth.minusYears(1).atDay(1).atStartOfDay();
            }

            log.info("통계 조회 기간: {} ~ {} (searchType: {})", startDate, endDate, searchType);

            // searchType에 따른 로그인 성공 건수 조회
            List<Object[]> loginData;
            if ("week".equals(searchType)) {
                if (isPostgre()) {
                    log.debug("외부망 환경: 일별 로그인 성공 건수 조회 (PostgreSQL WITH RECURSIVE 사용)");
                    loginData = userUsageMgmtRepository.countLoginSuccessByDayGroupByPostgresql(repositorySelectedDate,
                            projectType);
                } else {
                    log.debug("내부망 환경: 일별 로그인 성공 건수 조회 (Tibero CONNECT BY 사용)");
                    loginData = userUsageMgmtRepository.countLoginSuccessByDayGroupBy(repositorySelectedDate,
                            projectType);
                }
            } else if ("day".equals(searchType)) {
                if (isPostgre()) {
                    log.debug("외부망 환경: 일별 로그인 성공 건수 조회 (PostgreSQL WITH RECURSIVE 사용)");
                    loginData = userUsageMgmtRepository.countLoginSuccessByDayGroupByPostgresql(repositorySelectedDate,
                            projectType);
                } else {
                    log.debug("내부망 환경: 일별 로그인 성공 건수 조회 (Tibero CONNECT BY 사용)");
                    loginData = userUsageMgmtRepository.countLoginSuccessByDayGroupBy(repositorySelectedDate,
                            projectType);
                }
            } else {
                // 월별 로그인 성공 건수 조회
                if (isPostgre()) {
                    log.debug("외부망 환경: 월별 로그인 성공 건수 조회 (PostgreSQL DATE_TRUNC 사용)");
                    loginData = userUsageMgmtRepository.countLoginSuccessByMonthGroupByPostgresql(startDate, endDate,
                            projectType);
                } else {
                    log.debug("내부망 환경: 월별 로그인 성공 건수 조회 (Tibero TRUNC 사용)");
                    loginData = userUsageMgmtRepository.countLoginSuccessByMonthGroupBy(startDate, endDate,
                            projectType);
                }
            }

            // 로그인 성공 데이터를 Map 형태로 변환
            List<Map<String, Object>> loginSuccessCounts = new ArrayList<>();
            Long totalLoginSuccessCount = 0L;

            if ("week".equals(searchType)) {
                // 일별 데이터: SQL에서 이미 정렬된 데이터 처리 (7일 전부터 현재까지 필터링)
                LocalDate startDateOnly = startDate.toLocalDate();
                LocalDate endDateOnly = normalizedSelectedDate; // 선택한 날짜까지 포함

                for (Object[] row : loginData) {
                    // TO_CHAR로 변환된 값이 String이 아닐 수 있으므로 안전하게 변환
                    String day;
                    LocalDate rowDate;
                    if (row[0] instanceof String) {
                        day = (String) row[0];
                        rowDate = LocalDate.parse(day);
                    } else if (row[0] instanceof java.sql.Timestamp) {
                        // Timestamp인 경우 YYYY-MM-DD 형식으로 변환
                        java.sql.Timestamp timestamp = (java.sql.Timestamp) row[0];
                        rowDate = timestamp.toLocalDateTime().toLocalDate();
                        day = rowDate.toString();
                    } else if (row[0] instanceof java.sql.Date) {
                        // Date인 경우 YYYY-MM-DD 형식으로 변환
                        java.sql.Date date = (java.sql.Date) row[0];
                        rowDate = date.toLocalDate();
                        day = rowDate.toString();
                    } else {
                        // 기타 타입인 경우 문자열로 변환 후 YYYY-MM-DD 부분만 추출
                        String dateStr = row[0].toString();
                        day = dateStr.length() >= 10 ? dateStr.substring(0, 10) : dateStr;
                        rowDate = LocalDate.parse(day);
                    }

                    // 7일 전(startDate)부터 현재(selectedDate)까지의 데이터만 포함
                    if (rowDate.isBefore(startDateOnly) || rowDate.isAfter(endDateOnly)) {
                        continue; // 범위 밖이면 스킵
                    }

                    Long count = ((Number) row[1]).longValue(); // login_success_count

                    Map<String, Object> periodData = new HashMap<>();
                    periodData.put("day", day);
                    periodData.put("count", count);
                    loginSuccessCounts.add(periodData);

                    totalLoginSuccessCount += count;
                }
            } else if ("day".equals(searchType)) {
                // 일별 데이터: SQL에서 이미 정렬된 12개 데이터 처리
                for (Object[] row : loginData) {
                    // TO_CHAR로 변환된 값이 String이 아닐 수 있으므로 안전하게 변환
                    String day;
                    if (row[0] instanceof String) {
                        day = (String) row[0];
                    } else if (row[0] instanceof java.sql.Timestamp) {
                        // Timestamp인 경우 YYYY-MM-DD 형식으로 변환
                        java.sql.Timestamp timestamp = (java.sql.Timestamp) row[0];
                        day = timestamp.toLocalDateTime().toLocalDate().toString();
                    } else if (row[0] instanceof java.sql.Date) {
                        // Date인 경우 YYYY-MM-DD 형식으로 변환
                        java.sql.Date date = (java.sql.Date) row[0];
                        day = date.toLocalDate().toString();
                    } else {
                        // 기타 타입인 경우 문자열로 변환 후 YYYY-MM-DD 부분만 추출
                        String dateStr = row[0].toString();
                        day = dateStr.length() >= 10 ? dateStr.substring(0, 10) : dateStr;
                    }

                    Long count = ((Number) row[1]).longValue(); // login_success_count

                    Map<String, Object> periodData = new HashMap<>();
                    periodData.put("day", day);
                    periodData.put("count", count);
                    loginSuccessCounts.add(periodData);

                    totalLoginSuccessCount += count;
                }
            } else {
                // 월별 데이터 처리
                for (Object[] row : loginData) {
                    Map<String, Object> periodData = new HashMap<>();
                    // TO_CHAR로 변환된 값이 String이 아닐 수 있으므로 안전하게 변환
                    String period;
                    if (row[0] instanceof String) {
                        period = (String) row[0];
                    } else {
                        // 기타 타입인 경우 문자열로 변환
                        period = row[0].toString();
                    }

                    Long count = ((Number) row[1]).longValue();

                    periodData.put("month", period);
                    periodData.put("count", count);
                    loginSuccessCounts.add(periodData);

                    totalLoginSuccessCount += count;
                }
            }

            log.info("통계 조회 완료 - 월별 데이터: {}개월, 총 로그인 성공: {}건",
                    loginSuccessCounts.size(), totalLoginSuccessCount);

            // API 호출 성공/실패 건수 조회 (searchType에 따라 다른 기간)
            LocalDateTime apiStartDate, apiEndDate;
            if ("week".equals(searchType)) {
                // week: 현재 날짜부터 -7일까지
                apiStartDate = currentDate.minusDays(7);
                apiEndDate = currentDate.plusDays(1).minusSeconds(1);
            } else if ("day".equals(searchType)) {
                // day: 선택한 날만
                apiStartDate = currentDate;
                apiEndDate = currentDate.plusDays(1).minusSeconds(1);
            } else {
                // month: 해당월
                YearMonth yearMonth = YearMonth.from(normalizedSelectedDate);
                apiStartDate = yearMonth.atDay(1).atStartOfDay();
                apiEndDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            }

            // API 호출 성공 건수 조회
            Long apiSuccessCount = userUsageMgmtRepository.countApiSuccessByMonth(
                    apiStartDate, apiEndDate, projectType);

            // API 호출 실패 건수 조회
            Long apiFailureCount = userUsageMgmtRepository.countApiFailureByMonth(
                    apiStartDate, apiEndDate, projectType);

            // 총 API 호출 건수 계산
            Long totalApiCalls = (apiSuccessCount != null ? apiSuccessCount : 0L) +
                    (apiFailureCount != null ? apiFailureCount : 0L);

            // 성공률과 실패률 계산
            Double apiSuccessRate = 0.0;
            Double apiFailureRate = 0.0;

            if (totalApiCalls > 0) {
                apiSuccessRate = (apiSuccessCount != null ? apiSuccessCount.doubleValue() : 0.0)
                        / totalApiCalls.doubleValue() * 100.0;
                apiFailureRate = (apiFailureCount != null ? apiFailureCount.doubleValue() : 0.0)
                        / totalApiCalls.doubleValue() * 100.0;
            }

            log.info("API 통계 조회 완료 - 성공: {}건, 실패: {}건, 총 호출: {}건, 성공률: {:.2f}%, 실패률: {:.2f}%",
                    apiSuccessCount, apiFailureCount, totalApiCalls, apiSuccessRate, apiFailureRate);

            // API 호출 실패 요약 데이터 조회 (최신순 6개) - searchType에 따라 다른 기간
            LocalDateTime failureStartDate, failureEndDate;
            if ("week".equals(searchType)) {
                // week: 현재 날짜부터 -7일까지
                failureStartDate = currentDate.minusDays(7);
                failureEndDate = currentDate.plusDays(1).minusSeconds(1);
            } else if ("day".equals(searchType)) {
                // day: 선택한 날만
                failureStartDate = currentDate;
                failureEndDate = currentDate.plusDays(1).minusSeconds(1);
            } else {
                // month: 해당월
                YearMonth yearMonth = YearMonth.from(normalizedSelectedDate);
                failureStartDate = yearMonth.atDay(1).atStartOfDay();
                failureEndDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            }
            List<Object[]> failureData = userUsageMgmtRepository.findRecentApiFailures(failureStartDate, failureEndDate,
                    projectType);
            List<Map<String, Object>> apiFailureSummary = new ArrayList<>();

            for (Object[] row : failureData) {
                Map<String, Object> failureItem = new HashMap<>();

                // 시간 추출 (HH:mm:ss)
                LocalDateTime createdAt = (LocalDateTime) row[0];
                String timeStr = createdAt.toLocalTime()
                        .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                failureItem.put("time", timeStr);

                // menu_path에서 > 뒤쪽 부분 추출
                String menuPath = (String) row[1];
                String menuName = menuPath;
                if (menuPath != null && menuPath.contains(">")) {
                    menuName = menuPath.substring(menuPath.lastIndexOf(">") + 1).trim();
                }
                failureItem.put("menu", menuName);

                // err_code
                String errCode = (String) row[2];
                failureItem.put("errCode", errCode);

                apiFailureSummary.add(failureItem);
            }

            log.info("API 실패 요약 데이터 조회 완료 - {}건", apiFailureSummary.size());

            // 가장 많이 사용한 메뉴 조회 (상위 5개) - searchType에 따라 다른 기간
            LocalDateTime menuStartDate, menuEndDate;
            if ("week".equals(searchType)) {
                // week: 현재 날짜부터 -7일까지
                menuStartDate = currentDate.minusDays(7);
                menuEndDate = currentDate.plusDays(1).minusSeconds(1);
            } else if ("day".equals(searchType)) {
                // day: 선택한 날만
                menuStartDate = currentDate;
                menuEndDate = currentDate.plusDays(1).minusSeconds(1);
            } else {
                // month: 선택한 연월 범위만 사용
                YearMonth yearMonth = YearMonth.from(normalizedSelectedDate);
                menuStartDate = yearMonth.atDay(1).atStartOfDay();
                menuEndDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            }
            List<Object[]> topMenuData;
            if (isPostgre()) {
                log.debug("외부망 환경: 가장 많이 사용한 메뉴 조회 (PostgreSQL SPLIT_PART 사용)");
                topMenuData = userUsageMgmtRepository.findTopUsedMenusPostgresql(menuStartDate, menuEndDate,
                        projectType);
            } else {
                log.debug("내부망 환경: 가장 많이 사용한 메뉴 조회 (Tibero INSTR/SUBSTR 사용)");
                topMenuData = userUsageMgmtRepository.findTopUsedMenus(menuStartDate, menuEndDate, projectType);
            }
            List<Map<String, Object>> topUsedMenus = new ArrayList<>();

            for (Object[] row : topMenuData) {
                Map<String, Object> menuItem = new HashMap<>();

                // menu_path에서 > 뒤쪽 부분 추출
                String menuPath = (String) row[0];
                String menuName = menuPath;
                if (menuPath != null && menuPath.contains(">")) {
                    menuName = menuPath.substring(menuPath.lastIndexOf(">") + 1).trim();
                }
                menuItem.put("menu", menuName);

                // 카운트 (Tibero는 BigDecimal, PostgreSQL은 Long으로 반환될 수 있으므로 Number로 안전하게 변환)
                Long count = ((Number) row[1]).longValue();
                menuItem.put("count", count);

                topUsedMenus.add(menuItem);
            }

            log.info("가장 많이 사용한 메뉴 조회 완료 - {}건", topUsedMenus.size());

            // 응답 DTO 생성
            UserUsageMgmtStatsRes statsRes = UserUsageMgmtStatsRes.builder()
                    .searchType(searchType)
                    .selectedDate(selectedDate)
                    .projectType(projectType)
                    .loginSuccessCounts(loginSuccessCounts)
                    .totalLoginSuccessCount(totalLoginSuccessCount)
                    .apiSuccessCount(apiSuccessCount != null ? apiSuccessCount : 0L)
                    .apiFailureCount(apiFailureCount != null ? apiFailureCount : 0L)
                    .totalApiCalls(totalApiCalls)
                    .apiSuccessRate(apiSuccessRate)
                    .apiFailureRate(apiFailureRate)
                    .apiFailureSummary(apiFailureSummary)
                    .topUsedMenus(topUsedMenus)
                    .statisticsDate(LocalDateTime.now())
                    .periodStartDate(startDate)
                    .periodEndDate(endDate)
                    .build();

            return statsRes;

        } catch (RuntimeException e) {
            log.error("사용자 사용량 관리 통계 조회 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "통계 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 전체 프로젝트중 ONGOING 상태인 프로젝트 목록 조회
     *
     * @return 프로젝트 목록
     * @author sonmunwoo
     * @since 2025-10-21
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectRes> getAllProjects() {
        try {
            log.info("전체 프로젝트 목록 조회 시작");

            List<Project> projects = gpoProjectsRepository.findAll();

            log.info("프로젝트 조회 완료 - 총 {}건", projects.size());

            return projects.stream()
                    .filter(project -> project.getStatusNm() == ProjectStatus.ONGOING)
                    .map(ProjectRes::from)
                    .collect(Collectors.toList());

        } catch (org.springframework.dao.DataAccessException e) {
            log.error("프로젝트 목록 조회 실패 (데이터베이스 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "프로젝트 목록 조회에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("프로젝트 목록 조회 실패 (런타임 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "프로젝트 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectRes> getProjectsByName(String projectName) {
        try {
            log.info("프로젝트 목록 조회 (이름 검색) - keyword: {}", projectName);

            List<Project> projects = gpoProjectsRepository.findByPrjNmContainingIgnoreCase(projectName);

            log.info("프로젝트 조회 완료 (이름 검색) - keyword: {}, 총 {}건", projectName, projects.size());

            return projects.stream()
                    .map(ProjectRes::from)
                    .collect(Collectors.toList());

        } catch (org.springframework.dao.DataAccessException e) {
            log.error("프로젝트 목록 조회 실패 (데이터베이스 오류) - keyword: {}, error: {}", projectName, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "프로젝트 목록 조회에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("프로젝트 목록 조회 실패 (런타임 오류) - keyword: {}, error: {}", projectName, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "프로젝트 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public long deleteOldUserUsageMgmtData() {
        try {
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            log.info("30일 전 사용자 사용량 관리 데이터 삭제 시작 - 기준 날짜: {}", thirtyDaysAgo);

            // 삭제 전 개수 확인
            long countBeforeDelete = userUsageMgmtRepository.count();

            // 30일 전 데이터 삭제
            userUsageMgmtRepository.deleteByCreatedAtBefore(thirtyDaysAgo);

            // 삭제 후 개수 확인
            long countAfterDelete = userUsageMgmtRepository.count();
            long deletedCount = countBeforeDelete - countAfterDelete;

            log.info("30일 전 사용자 사용량 관리 데이터 삭제 완료 - 삭제된 레코드 수: {}건", deletedCount);

            return deletedCount;
        } catch (org.springframework.dao.DataAccessException e) {
            log.error("30일 전 사용자 사용량 관리 데이터 삭제 실패 (데이터베이스 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터 삭제에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("30일 전 사용자 사용량 관리 데이터 삭제 실패 (런타임 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "데이터 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    private LocalDate normalizeSelectedDate(String selectedDate) {
        if (selectedDate == null || selectedDate.trim().isEmpty()) {
            log.warn("selectedDate가 비어 있어 현재 날짜를 사용합니다.");
            return LocalDate.now();
        }

        String trimmed = selectedDate.trim();
        try {
            if (trimmed.length() >= 10) { // yyyy-MM-dd 또는 yyyy-MM-dd... 유지
                return LocalDate.parse(trimmed.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            if (trimmed.length() == 8) { // yy-MM-dd 형태
                return LocalDate.parse(trimmed, DateTimeFormatter.ofPattern("yy-MM-dd"));
            }
            return LocalDate.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            log.warn("선택된 날짜 파싱 실패: {} - 현재 날짜로 대체합니다.", selectedDate, e);
            return LocalDate.now();
        }
    }

    private UserUsageMgmtRes mapToResponseWithDetails(UserUsageMgmt entity) {
        UserUsageMgmtRes response = userUsageMgmtMapper.toResponse(entity);
        populateDetailContents(entity, response);
        return response;
    }

    private void applyRequestDetails(UserUsageMgmt entity, String requestContent) {
        if (entity == null) {
            return;
        }

        // 요청 본문이 없으면(정책상 null 저장) 기존 매핑된 상세값을 보존
        if (requestContent == null || requestContent.isEmpty()) {
            return;
        }

        String normalized = truncateContent(requestContent, 4000);
        String[] parts = splitContentIntoChunks(normalized, 1000, 4);

        // requestContent는 저장하지 않음 (detail 필드에만 저장)
        entity.setRequestContent(null);
        entity.setFirstRequestDetail(getChunk(parts, 0));
        entity.setSecondRequestDetail(getChunk(parts, 1));
        entity.setThirdRequestDetail(getChunk(parts, 2));
        entity.setFourthRequestDetail(getChunk(parts, 3));
    }

    private void applyResponseDetails(UserUsageMgmt entity, String responseContent) {
        if (entity == null) {
            return;
        }

        // 응답 본문이 없으면(정책상 null 저장) 기존 매핑된 상세값을 보존
        if (responseContent == null || responseContent.isEmpty()) {
            return;
        }

        String normalized = truncateContent(responseContent, 4000);
        String[] parts = splitContentIntoChunks(normalized, 1000, 4);

        // responseContent는 저장하지 않음 (detail 필드에만 저장)
        entity.setResponseContent(null);
        entity.setFirstResponseDetail(getChunk(parts, 0));
        entity.setSecondResponseDetail(getChunk(parts, 1));
        entity.setThirdResponseDetail(getChunk(parts, 2));
        entity.setFourthResponseDetail(getChunk(parts, 3));
    }

    private void populateDetailContents(UserUsageMgmt entity, UserUsageMgmtRes response) {
        if (entity == null || response == null) {
            return;
        }

        response.setRequestContent(buildFullRequestContent(entity));
        response.setResponseContent(buildFullResponseContent(entity));
    }

    private String[] splitContentIntoChunks(String content, int chunkSize, int chunkCount) {
        if (chunkCount <= 0) {
            return new String[0];
        }

        String[] result = new String[chunkCount];
        if (content == null || content.isEmpty() || chunkSize <= 0) {
            return result;
        }

        int length = content.length();
        for (int i = 0; i < chunkCount; i++) {
            int start = i * chunkSize;
            if (start >= length) {
                break;
            }
            int end = Math.min(start + chunkSize, length);
            String part = content.substring(start, end);
            result[i] = part.isEmpty() ? null : part;
        }
        return result;
    }

    private String buildFullRequestContent(UserUsageMgmt entity) {
        StringBuilder builder = new StringBuilder();
        appendSegment(builder, entity.getFirstRequestDetail());
        appendSegment(builder, entity.getSecondRequestDetail());
        appendSegment(builder, entity.getThirdRequestDetail());
        appendSegment(builder, entity.getFourthRequestDetail());

        if (builder.length() > 0) {
            return builder.toString();
        }
        return entity.getRequestContent();
    }

    private String buildFullResponseContent(UserUsageMgmt entity) {
        StringBuilder builder = new StringBuilder();
        appendSegment(builder, entity.getFirstResponseDetail());
        appendSegment(builder, entity.getSecondResponseDetail());
        appendSegment(builder, entity.getThirdResponseDetail());
        appendSegment(builder, entity.getFourthResponseDetail());

        if (builder.length() > 0) {
            return builder.toString();
        }
        return entity.getResponseContent();
    }

    private void appendSegment(StringBuilder builder, String segment) {
        if (builder == null || segment == null || segment.isEmpty()) {
            return;
        }
        builder.append(segment);
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null) {
            return null;
        }
        if (maxLength <= 0) {
            return content;
        }
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength);
    }

    private String getChunk(String[] parts, int index) {
        if (parts == null || index < 0 || index >= parts.length) {
            return null;
        }
        return parts[index];
    }

    private void enforceFieldConstraints(UserUsageMgmt entity) {
        if (entity == null) {
            return;
        }

        entity.setTargetAsset(truncateContent(entity.getTargetAsset(), TARGET_ASSET_MAX_LENGTH));
    }

}
