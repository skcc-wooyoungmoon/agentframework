package com.skax.aiplatform.service.batch.impl;

import com.skax.aiplatform.entity.GpoGroupcoJkwMas;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.user.DormantStatus;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.common.GpoGroupcoJkwMasRepository;
import com.skax.aiplatform.service.batch.HrDataBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * HR 데이터 배치 처리 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HrDataBatchServiceImpl implements HrDataBatchService {

    private final GpoGroupcoJkwMasRepository gpoGroupcoJkwMasRepository;
    private final GpoUsersMasRepository userRepository;

    @Value("${batch.hr.file.directory:/gapdat/HR}")
    private String hrFileDirectory;

    @Value("#{'${batch.hr.file.prefixes:tempfile}'.split(',')}")
    private List<String> hrFilePrefixes;

    private static final String DELIMITER = "\\|";
    private static final int EXPECTED_FIELD_COUNT = 31;
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("^(\\w+)_(\\d{8})\\.txt$");

    // 퇴직 여부 상수
    private static final int RETR_STATUS_ACTIVE = 0;    // 재직
    private static final int RETR_STATUS_RETIRED = 1;   // 퇴직

    /**
     * HR 데이터 파일을 읽어서 데이터베이스에 저장
     */
    @Override
    @Transactional
    public void processHrDataFile() {
        log.info("=== HR 데이터 배치 처리 시작 ===");

        try {
            // 1. 각 prefix별 최신 파일 찾기
            List<Path> latestFiles = findLatestHrFiles();

            if (latestFiles.isEmpty()) {
                log.warn("처리할 HR 파일을 찾을 수 없습니다. 디렉토리: {}, prefixes: {}", hrFileDirectory, hrFilePrefixes);
                return;
            }

            log.info("총 {}개의 HR 파일 처리 시작", latestFiles.size());

            // 2. 기존 'SH' 데이터 삭제
            log.info("기존 grpco_c='SH' 데이터 삭제 시작");
            gpoGroupcoJkwMasRepository.deleteByGrpcoC("SH");
            log.info("기존 grpco_c='SH' 데이터 삭제 완료");

            // 3. 남은 데이터를 메모리에 로드 (member_id 기준으로 Map 생성)
            List<GpoGroupcoJkwMas> existingRecords = gpoGroupcoJkwMasRepository.findAll();
            Map<String, GpoGroupcoJkwMas> existingMap = new HashMap<>();
            for (GpoGroupcoJkwMas record : existingRecords) {
                existingMap.put(record.getMemberId(), record);
            }
            log.info("기존 데이터 {}건을 메모리에 로드", existingMap.size());

            // 4. 전체 통계를 위한 카운터
            int totalInsertCount = 0;
            int totalUpdateCount = 0;

            // 5. 각 파일을 순차적으로 처리
            for (Path filePath : latestFiles) {
                log.info("처리 대상 파일: {}", filePath.getFileName());

                // 파일 읽기 및 파싱
                List<GpoGroupcoJkwMas> employeeList = parseHrFile(filePath);

                if (employeeList.isEmpty()) {
                    log.warn("파일 {}에서 유효한 데이터를 찾을 수 없습니다.", filePath.getFileName());
                    continue;
                }

                // 데이터베이스에 저장 (upsert 방식 - 메모리 기반 체크)
                int insertCount = 0;
                int updateCount = 0;

                for (GpoGroupcoJkwMas employee : employeeList) {
                    GpoGroupcoJkwMas existing = existingMap.get(employee.getMemberId());

                    if (existing != null) {
                        // 기존 레코드가 있으면 업데이트 (fstCreatedAt 유지)
                        GpoGroupcoJkwMas updated = GpoGroupcoJkwMas.builder()
                                .memberId(employee.getMemberId())
                                .grpcoC(employee.getGrpcoC())
                                .jkwNm(employee.getJkwNm())
                                .jkwiNm(employee.getJkwiNm())
                                .deptNm(employee.getDeptNm())
                                .grpcoNm(employee.getGrpcoNm())
                                .jkgpNm(employee.getJkgpNm())
                                .hpNo(employee.getHpNo())
                                .retrJkwYn(employee.getRetrJkwYn())
                                .fstCreatedAt(existing.getFstCreatedAt()) // 기존 생성일시 유지
                                .lstUpdatedAt(LocalDateTime.now()) // 수정일시 갱신
                                .build();
                        gpoGroupcoJkwMasRepository.save(updated);
                        updateCount++;
                    } else {
                        // 새로운 레코드 삽입
                        gpoGroupcoJkwMasRepository.save(employee);
                        // existingMap에도 추가하여 다음 파일 처리 시 중복 방지
                        existingMap.put(employee.getMemberId(), employee);
                        insertCount++;
                    }
                }

                log.info("파일 {} 처리 완료: 신규 {}건, 수정 {}건",
                        filePath.getFileName(), insertCount, updateCount);

                totalInsertCount += insertCount;
                totalUpdateCount += updateCount;
            }

            log.info("=== HR 데이터 배치 처리 완료 === 총 {}건 처리 (신규: {}건, 수정: {}건)",
                    totalInsertCount + totalUpdateCount, totalInsertCount, totalUpdateCount);

        } catch (IOException e) {
            // 파일 읽기/쓰기 오류
            log.error("HR 데이터 배치 처리 중 파일 I/O 오류 발생", e);
            throw new RuntimeException("HR 데이터 배치 처리 실패: 파일 처리 중 오류가 발생했습니다.", e);
        } catch (DataAccessException e) {
            // 데이터베이스 접근 오류
            log.error("HR 데이터 배치 처리 중 데이터베이스 오류 발생", e);
            throw new RuntimeException("HR 데이터 배치 처리 실패: 데이터베이스 오류가 발생했습니다.", e);
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.error("HR 데이터 배치 처리 중 잘못된 인자 오류 발생", e);
            throw new RuntimeException("HR 데이터 배치 처리 실패: 잘못된 인자로 인해 오류가 발생했습니다.", e);
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("HR 데이터 배치 처리 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("HR 데이터 배치 처리 실패", e);
        }
    }

    /**
     * 직원 원장 테이블과 유저 테이블을 비교하여 동기화 처리
     */
    @Override
    @Transactional
    public void syncUsersWithHrData() {
        log.info("=== HR 데이터 동기화 시작 ===");

        // 통계 카운터
        int processCount = 0;       // 처리된 총 사용자 수
        int updateCount = 0;        // 필드가 변경된 사용자 수
        int noChangeCount = 0;      // 변경 없는 사용자 수
        int notInHrCount = 0;       // HR 원장에 없는 사용자 수

        List<GpoUsersMas> registeredUsers = userRepository.findAll();

        if (registeredUsers.isEmpty()) {
            log.info("유저 테이블에 등록된 사용자가 없습니다.");
            return;
        }
        log.info("등록된 사용자 수: {}명", registeredUsers.size());

        Set<String> memberIds = registeredUsers.stream()
                .map(GpoUsersMas::getMemberId)
                .collect(toSet());
        Map<String, GpoGroupcoJkwMas> hrMap = createHrMap(memberIds);

        for (GpoUsersMas user : registeredUsers) {
            String memberId = user.getMemberId();
            GpoGroupcoJkwMas hrRecord = hrMap.get(memberId);

            if (hrRecord == null) {
                log.warn("회원 ID '{}'에 해당하는 직원 원장 데이터가 없습니다.", memberId);
                notInHrCount++;
                processCount++;
                continue;
            }

            boolean hasChanges = syncSingleUser(user, hrRecord);

            if (hasChanges) {
                updateCount++;
            } else {
                noChangeCount++;
            }

            processCount++;
        }

        log.info("=== HR 데이터 동기화 완료 ===");
        log.info("총 처리: {}명 (변경: {}명, 변경 없음: {}명, HR 없음: {}명)",
                processCount, updateCount, noChangeCount, notInHrCount);
    }


    // == 유틸리티 메서드들 ==

    /**
     * /gapdat/HR 디렉토리에서 각 prefix별 최신 파일 찾기
     */
    private List<Path> findLatestHrFiles() throws IOException {
        Path directory = Paths.get(hrFileDirectory);

        if (!Files.exists(directory)) {
            log.error("HR 파일 디렉토리가 존재하지 않습니다: {}", hrFileDirectory);
            return new ArrayList<>();
        }

        if (!Files.isDirectory(directory)) {
            log.error("지정된 경로가 디렉토리가 아닙니다: {}", hrFileDirectory);
            return new ArrayList<>();
        }

        // 각 prefix별로 최신 파일 정보를 저장할 Map
        Map<String, Path> latestFileMap = new HashMap<>();
        Map<String, String> latestDateMap = new HashMap<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.txt")) {
            for (Path file : stream) {
                String fileName = file.getFileName().toString();
                Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);

                if (matcher.matches()) {
                    String prefix = matcher.group(1);
                    String dateStr = matcher.group(2);

                    // 설정된 prefix 목록에 포함되는지 확인
                    if (hrFilePrefixes.contains(prefix)) {
                        // 해당 prefix의 기존 날짜와 비교
                        String existingDate = latestDateMap.get(prefix);
                        if (existingDate == null || dateStr.compareTo(existingDate) > 0) {
                            latestDateMap.put(prefix, dateStr);
                            latestFileMap.put(prefix, file);
                        }
                    }
                }
            }
        }

        // 결과를 리스트로 변환하고 로그 출력
        List<Path> latestFiles = new ArrayList<>(latestFileMap.values());
        for (Map.Entry<String, Path> entry : latestFileMap.entrySet()) {
            String prefix = entry.getKey();
            Path file = entry.getValue();
            String date = latestDateMap.get(prefix);
            log.info("최신 HR 파일 발견 [{}]: {} (날짜: {})", prefix, file.getFileName(), date);
        }

        return latestFiles;
    }

    /**
     * HR 파일 파싱
     */
    private List<GpoGroupcoJkwMas> parseHrFile(Path filePath) throws IOException {
        List<GpoGroupcoJkwMas> employeeList = new ArrayList<>();
        List<String> lines = Files.readAllLines(filePath);

        log.info("파일 읽기 완료. 총 {}줄", lines.size());

        int lineNumber = 0;
        int skippedLines = 0;

        for (String line : lines) {
            lineNumber++;

            // 빈 줄 건너뛰기
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            try {
                // 파이프(|)로 분할
                String[] fields = line.split(DELIMITER, -1);

                // 필드 개수 검증 (31개 미만이면 건너뛰기)
                if (fields.length < EXPECTED_FIELD_COUNT) {
                    log.warn("라인 {}: 필드 개수 부족 ({}개). 건너뜀.", lineNumber, fields.length);
                    skippedLines++;
                    continue;
                }

                // 직원 정보 생성
                GpoGroupcoJkwMas employee = createEmployeeFromFields(fields, lineNumber);

                if (employee != null) {
                    employeeList.add(employee);
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                // 배열 인덱스 범위 초과 오류
                log.error("라인 {} 파싱 중 배열 인덱스 오류: {}", lineNumber, e.getMessage());
                skippedLines++;
            } catch (IllegalArgumentException | NullPointerException e) {
                // 잘못된 인자나 null 참조 예외
                log.error("라인 {} 파싱 중 잘못된 인자 오류: {}", lineNumber, e.getMessage());
                skippedLines++;
            } catch (Exception e) {
                // 기타 예상치 못한 예외
                log.error("라인 {} 파싱 중 오류: {}", lineNumber, e.getMessage());
                skippedLines++;
            }
        }

        log.info("파싱 완료. 유효 데이터: {}건, 건너뛴 줄: {}건", employeeList.size(), skippedLines);

        return employeeList;
    }

    /**
     * 필드 배열에서 직원 정보 엔티티 생성
     * 매핑:
     * 0 -> grpco_c
     * 1 -> member_id
     * 2 -> jkw_nm
     * 4 -> jkwi_nm
     * 10 -> dept_nm
     * 12 -> grpco_nm
     * 14 -> jkgp_nm
     * 19 -> hp_no
     * 28 -> retr_jkw_yn (Y/N -> 1/0)
     */
    private GpoGroupcoJkwMas createEmployeeFromFields(String[] fields, int lineNumber) {
        try {
            // 필수 필드인 member_id 검증
            String memberId = fields[1].trim();
            if (memberId.isEmpty()) {
                log.warn("라인 {}: member_id가 비어있음. 건너뜀.", lineNumber);
                return null;
            }

            LocalDateTime now = LocalDateTime.now();

            return GpoGroupcoJkwMas.builder()
                    .memberId(memberId)
                    .grpcoC(getFieldValue(fields, 0))
                    .jkwNm(getFieldValue(fields, 2))
                    .jkwiNm(getFieldValue(fields, 4))
                    .deptNm(getFieldValue(fields, 7))
                    .grpcoNm(getFieldValue(fields, 9))
                    .jkgpNm(getFieldValue(fields, 11))
                    .hpNo(getFieldValue(fields, 16))
                    .retrJkwYn(convertYnToInteger(getFieldValue(fields, 24)))
                    .fstCreatedAt(now)
                    .lstUpdatedAt(now)
                    .build();

        } catch (ArrayIndexOutOfBoundsException e) {
            // 배열 인덱스 범위 초과 오류
            log.error("라인 {} 엔티티 생성 중 배열 인덱스 오류: {}", lineNumber, e.getMessage());
            return null;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.error("라인 {} 엔티티 생성 중 잘못된 인자 오류: {}", lineNumber, e.getMessage());
            return null;
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("라인 {} 엔티티 생성 중 오류: {}", lineNumber, e.getMessage());
            return null;
        }
    }

    /**
     * 배열에서 안전하게 필드 값 가져오기
     */
    private String getFieldValue(String[] fields, int index) {
        if (index < 0 || index >= fields.length) {
            return null;
        }
        String value = fields[index].trim();
        return value.isEmpty() ? null : value;
    }

    /**
     * Y/N 문자열을 1/0 정수로 변환
     * Y -> 1 (퇴직)
     * N -> 0 (재직)
     * 그 외 -> 0 (기본값: 재직)
     */
    private Integer convertYnToInteger(String yn) {
        if (yn == null) {
            return RETR_STATUS_ACTIVE;
        }

        String value = yn.trim().toUpperCase();

        if ("Y".equals(value)) {
            return RETR_STATUS_RETIRED;
        } else if ("N".equals(value)) {
            return RETR_STATUS_ACTIVE;
        } else {
            log.warn("예상치 못한 retr_jkw_yn 값: '{}'. 기본값(재직) 사용", yn);
            return RETR_STATUS_ACTIVE;
        }
    }

    /**
     * 직원 원장 테이블에서 member_id 목록으로 Map 생성
     */
    private Map<String, GpoGroupcoJkwMas> createHrMap(Set<String> memberIds) {
        return gpoGroupcoJkwMasRepository.findAllById(memberIds)
                .stream()
                .collect(toMap(
                        GpoGroupcoJkwMas::getMemberId,
                        Function.identity(),
                        (existing, duplicate) -> existing
                ));
    }

    /**
     * 단일 사용자 동기화
     *
     * @return 변경 여부
     */
    private boolean syncSingleUser(GpoUsersMas user, GpoGroupcoJkwMas hrRecord) {
        boolean hasChanges = false;
        List<String> changedFields = new ArrayList<>();

        // 1. 부서명
        String oldDeptNm = safeTrim(user.getDeptNm());
        String newDeptNm = safeTrim(hrRecord.getDeptNm());
        if (!Objects.equals(oldDeptNm, newDeptNm)) {
            user.setDeptNm(newDeptNm);
            changedFields.add("부서명");
            // log.info("  - 부서명: [{}] -> [{}]", oldDeptNm, newDeptNm);
            hasChanges = true;
        }

        // 2. 직급명
        String oldJkgpNm = safeTrim(user.getJkgpNm());
        String newJkgpNm = safeTrim(hrRecord.getJkgpNm());
        if (!Objects.equals(oldJkgpNm, newJkgpNm)) {
            user.setJkgpNm(newJkgpNm);
            changedFields.add("직급명");
            // log.info("  - 직급명: [{}] -> [{}]", oldJkgpNm, newJkgpNm);
            hasChanges = true;
        }

        // 3. 휴대폰번호
        String oldHpNo = safeTrim(user.getHpNo());
        String newHpNo = safeTrim(hrRecord.getHpNo());
        if (!Objects.equals(oldHpNo, newHpNo)) {
            user.setHpNo(newHpNo);
            changedFields.add("휴대폰번호");
            // log.info("  - 휴대폰번호: [{}] -> [{}]", oldHpNo, newHpNo);
            hasChanges = true;
        }

        // 4. 퇴직여부
        Integer oldRetrJkwYn = user.getRetrJkwYn();
        Integer newRetrJkwYn = hrRecord.getRetrJkwYn();
        if (!Objects.equals(oldRetrJkwYn, newRetrJkwYn)) {
            user.setRetrJkwYn(newRetrJkwYn);
            changedFields.add("퇴직여부");
            // log.info("  - 퇴직여부: [{}] -> [{}]", oldRetrJkwYn, newRetrJkwYn);
            hasChanges = true;
        }

        // 5. 계정 상태 (퇴직/휴면 처리)
        // 퇴직 처리
        if (newRetrJkwYn == RETR_STATUS_RETIRED && user.getDmcStatus() != DormantStatus.WITHDRAW) {
            DormantStatus oldStatus = user.getDmcStatus();
            user.setDmcStatus(DormantStatus.WITHDRAW);
            changedFields.add("계정상태(퇴직)");
            // log.info("  - 계정상태: [{}] -> [WITHDRAW]", oldStatus);
            hasChanges = true;
        }

        // 휴면 처리 (1년 이상 미로그인)
        if (newRetrJkwYn == RETR_STATUS_ACTIVE) {
            LocalDateTime lastLoginAt = user.getLstLoginAt();

            if (lastLoginAt != null) {
                LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

                if (lastLoginAt.isBefore(oneYearAgo) && user.getDmcStatus() != DormantStatus.DORMANT) {
                    DormantStatus oldStatus = user.getDmcStatus();
                    user.setDmcStatus(DormantStatus.DORMANT);
                    changedFields.add("계정상태(휴면)");
                    // log.info("  - 계정상태: [{}] -> [DORMANT] (1년 이상 미로그인)", oldStatus);
                    hasChanges = true;
                }
            }
        }

        /* 로그 출력 */
        // if (hasChanges) {
        //     log.info("회원 ID '{}' 업데이트 완료 - 변경된 필드: {}",
        //             user.getMemberId(), String.join(", ", changedFields));
        // } else {
        //     log.info("회원 ID '{}' 변경 사항 없음", user.getMemberId());
        // }

        return hasChanges;
    }

    /**
     * 앞뒤 공백 제거
     */
    private String safeTrim(String value) {
        return value != null ? value.trim() : null;
    }

}
