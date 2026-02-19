package com.skax.aiplatform.service.admin.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.auth.SktaiProjectClient;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyItem;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.dto.response.*;
import com.skax.aiplatform.client.sktai.auth.service.SktaiAuthService;
import com.skax.aiplatform.client.sktai.auth.service.SktaiGroupService;
import com.skax.aiplatform.client.sktai.auth.service.SktaiUserService;
import com.skax.aiplatform.common.context.AdminContext;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.util.TokenInfo;
import com.skax.aiplatform.dto.auth.TokenCacheData;
import com.skax.aiplatform.dto.auth.request.LoginReq;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.entity.project.Project;
import com.skax.aiplatform.repository.admin.ProjectMgmtRepository;
import com.skax.aiplatform.repository.admin.ProjectUserRoleRepository;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.auth.TokenCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 관리자 권한 관리 서비스 구현
 *
 * <p>기존 SktaiAuthService, SktaiUserService를 활용하되,
 * AdminContext를 통해 Admin 모드로 동작합니다.</p>
 *
 * @author Jongtae Park
 * @version 1.0.0
 * @since 2025-10-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    // 하드코딩된 Admin 계정 정보 (static final String 형태)
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "aisnb";

    private final SktaiAuthService sktaiAuthService;
    private final SktaiUserService sktaiUserService;
    private final GpoUsersMasRepository userRepository;
    private final TokenCacheService tokenCacheService;
    // private final PasswordEncoder passwordEncoder;
    private final SktaiGroupService sktaiGroupService;

    private final TokenInfo tokenInfo;
    private final ProjectUserRoleRepository projectUserRoleRepository;
    // private final RoleRepositoryV2 roleRepositoryV2;

    private final SktaiProjectClient sktaiProjectClient;

    private final GpoAssetPrjMapMasRepository assetPrjMapMasRepository;
    private final ProjectMgmtRepository projectMgmtRepository;

    // 프로젝트명 → 프로젝트 정보 캐시 (변하지 않는 값이므로 메모리에 1회 캐싱)
    private final ConcurrentMap<String, ClientRead> projectByNameCache = new ConcurrentHashMap<>();

    @Override
    public boolean ensureAdminToken() {
        try {
            // Admin 계정의 토큰 조회 (캐시에서)
            TokenCacheData adminToken = tokenCacheService.getTokenByUsername(ADMIN_USERNAME);
            GpoUsersMas adminUser = userRepository.findByMemberId(ADMIN_USERNAME).orElse(null);

            // 토큰이 유효하면 그대로 사용
            if (adminToken != null && adminToken.isValid()) {
                log.debug("Admin 토큰이 유효함");
                return true;
            }

            // 토큰이 없거나 만료된 경우: 새로 로그인
            log.info("Admin 토큰 없음 또는 만료됨. 새로 로그인 시도");

            LoginReq adminLoginReq = new LoginReq();
            adminLoginReq.setUsername(ADMIN_USERNAME);
            adminLoginReq.setPassword(ADMIN_PASSWORD);

            // SktaiAuthService.login()으로 SKTAI 토큰 발급
            // adxp 관리자 권한 획득을 위한 로그인 처리
            // 사용자의 시스템 로그인 개념이 아니므로 횟수제한 등 적용이 불필요함
            AccessTokenResponseWithProject response = sktaiAuthService.login(adminLoginReq);

            // 캐시에 Admin 토큰 저장 또는 업데이트
            if (adminToken == null) {
                adminToken = TokenCacheData.builder()
                        .uuid(adminUser != null ? adminUser.getUuid() : "")
                        .memberId(ADMIN_USERNAME)
                        .accessToken(response.getAccessToken())
                        .refreshToken(response.getRefreshToken())
                        .tokenType(StringUtils.hasText(response.getTokenType()) ? response.getTokenType() : "Bearer")
                        .tokenExpTimes(response.getExpiresIn())
                        .refreshTokenExpTimes(response.getRefreshExpiresIn())
                        .expAt(extractExpAtFromAccessToken(response.getAccessToken(), response.getExpiresIn()))
                        .issueAt(LocalDateTime.now())
                        .refreshTokenExpAt(LocalDateTime.now().plusSeconds(response.getRefreshExpiresIn()))
                        .tokenExpYn("N")
                        .build();
            } else {
                adminToken.setAccessToken(response.getAccessToken());
                adminToken.setRefreshToken(response.getRefreshToken());
                adminToken.setTokenType(StringUtils.hasText(response.getTokenType()) ? response.getTokenType() :
                        "Bearer");
                adminToken.setTokenExpTimes(response.getExpiresIn());
                adminToken.setRefreshTokenExpTimes(response.getRefreshExpiresIn());
                adminToken.setExpAt(extractExpAtFromAccessToken(response.getAccessToken(), response.getExpiresIn()));
                adminToken.setRefreshTokenExpAt(LocalDateTime.now().plusSeconds(response.getRefreshExpiresIn()));
                adminToken.setTokenExpYn("N");
            }

            tokenCacheService.cacheToken(adminToken);

            log.info("Admin 토큰 발급 및 캐시 저장 완료");
            return true;

        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            log.error("Admin 토큰 확보 실패 (BusinessException): {}", e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.error("Admin 토큰 확보 실패 (잘못된 인자): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Admin 인증 실패: " + e.getMessage());
        } catch (DataAccessException e) {
            // 데이터베이스 접근 오류
            log.error("Admin 토큰 확보 실패 (데이터베이스 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Admin 인증 실패: 데이터베이스 오류");
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("Admin 토큰 확보 실패 (예상치 못한 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Admin 인증 실패");
        }
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        log.info("사용자 삭제 시작 (Admin): userId={}", userId);

        try {
            AdminContext.setAdminMode(ADMIN_USERNAME);
            ensureAdminToken();

            GpoUsersMas user = userRepository.findById(userId)
                    .orElse(null);

            // 토큰 정보 삭제 (캐시에서)
            tokenCacheService.removeTokenFromCache(userId);

            // 사용자 삭제
            if (user != null) {
                userRepository.delete(user);
            }

            // ADXP ID 조회하여 사용자 삭제
            List<UserBase> users = sktaiUserService.getUsers(1, 100, null, null, userId.toLowerCase()).getData();
            if (users.size() == 1) {
                sktaiUserService.deleteUser(users.get(0).getId());
            }

            log.info("사용자 삭제 완료 (Admin): userId={}", userId);
        } finally {
            AdminContext.clear();
        }
    }

    @Override
    @Transactional
    public void assignUserToGroup(String userId, String groupNm) {
        log.info("권한 지정 시작 (Admin): userId={}, groupNm={}", userId, groupNm);

        try {
            AdminContext.setAdminMode(ADMIN_USERNAME);
            ensureAdminToken();

            sktaiUserService.addUserToGroup(
                    userRepository.findById(userId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                            .getUuid(),
                    sktaiGroupService.searchGroups(groupNm, 1, 100).getGroupList().stream()
                            .filter(groupResponse -> groupResponse.getName().equals(groupNm))
                            .findAny()
                            .orElseThrow()
                            .getId());

            log.info("권한 지정 완료 (Admin): userId={}", userId);
        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            log.warn("권한 지정 실패 (Admin, BusinessException): userId={}, error={}", userId, e.getMessage());
            throw e;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.warn("권한 지정 실패 (Admin, 잘못된 인자): userId={}, error={}", userId, e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예상치 못한 예외는 경고만 기록
            log.warn("권한 지정 실패 (Admin, 예상치 못한 오류): userId={}, error={}", userId, e.getMessage(), e);
        } finally {
            AdminContext.clear();
        }
    }

    @Override
    @Transactional
    public void assignUserToGroupWithAdxpId(String adxpUserId, String adxpGroupId) {
        log.info("권한 지정 시작 (Admin): adxpUserId={}, adxpGroupId={}", adxpUserId, adxpGroupId);

        try {
            AdminContext.setAdminMode(ADMIN_USERNAME);
            ensureAdminToken();

            sktaiUserService.addUserToGroup(adxpUserId, adxpGroupId);

            log.info("권한 지정 완료 (Admin): adxpUserId={}", adxpUserId);
        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            log.warn("권한 지정 실패 (Admin, BusinessException): adxpUserId={}, error={}", adxpUserId, e.getMessage());
            throw e;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.warn("권한 지정 실패 (Admin, 잘못된 인자): adxpUserId={}, error={}", adxpUserId, e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예상치 못한 예외는 경고만 기록
            log.warn("권한 지정 실패 (Admin, 예상치 못한 오류): adxpUserId={}, error={}", adxpUserId, e.getMessage(), e);
        } finally {
            AdminContext.clear();
        }
    }

    @Override
    @Transactional
    public void unassignUserFromGroup(String userId, String groupNm) {
        log.info("권한 해제 시작 (Admin): userId={}, groupNm={}", userId, groupNm);

        try {
            AdminContext.setAdminMode(ADMIN_USERNAME);
            ensureAdminToken();

            sktaiUserService.removeUserFromGroup(
                    userRepository.findById(userId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                            .getUuid(),
                    sktaiGroupService.searchGroups(groupNm, 1, 100).getGroupList().stream()
                            .filter(groupResponse -> groupResponse.getName().equals(groupNm))
                            .findAny()
                            .orElseThrow()
                            .getId()
            );

            log.info("권한 해제 완료 (Admin): userId={}", userId);
        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            log.warn("권한 해제 실패 (Admin, BusinessException): userId={}, error={}", userId, e.getMessage());
            throw e;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.warn("권한 해제 실패 (Admin, 잘못된 인자): userId={}, error={}", userId, e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예상치 못한 예외는 경고만 기록
            log.warn("권한 해제 실패 (Admin, 예상치 못한 오류): userId={}, error={}", userId, e.getMessage(), e);
        } finally {
            AdminContext.clear();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RoleAvailablePageResponseDto getUserRoleAvailable(String userId) {
        log.info("사용자 부여 가능 role (Admin): userId={}", userId);

        try {
            AdminContext.setAdminMode(ADMIN_USERNAME);
            ensureAdminToken();

            return sktaiUserService.getUserAvailableRoles(userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)).getUuid());
        } finally {
            AdminContext.clear();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void updateUserRoleMappingsFromAvailable(String userId,
                                                    List<RoleAvailablePageResponseDto.Item> availableItems) {
        log.info("사용자 role 부여 (Admin): userId={}, role={}", userId, availableItems);

        try {
            AdminContext.setAdminMode(ADMIN_USERNAME);
            ensureAdminToken();

            sktaiUserService.updateUserRoleMappingsFromAvailable(userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)).getUuid(), availableItems);
        } finally {
            AdminContext.clear();
        }
    }

    @Override
    @Transactional
    public String createGroup(String groupName) {
        log.info("그룹 생성 시작 (Admin): groupName={}", groupName);

        try {
            AdminContext.setAdminMode(ADMIN_USERNAME);
            ensureAdminToken();

            // SKTAI에 그룹 생성
            GroupResponse response = sktaiGroupService.createGroup(groupName);

            log.info("그룹 생성 완료 (Admin): groupName={}, groupId={}", groupName, response.getId());
            return response.getId();

        } finally {
            AdminContext.clear();
        }
    }

    @Override
    @Transactional
    public void deleteGroup(String groupName) {
        log.info("그룹 삭제 시작 (Admin): groupName={}", groupName);

        try {
            AdminContext.setAdminMode(ADMIN_USERNAME);
            ensureAdminToken();

            // 그룹명으로 실제 그룹 ID 조회
            String groupId = sktaiGroupService.searchGroups(groupName, 1, 100).getGroupList().stream()
                    .filter(groupResponse -> groupResponse.getName().equals(groupName))
                    .findAny()
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                            "그룹을 찾을 수 없습니다: " + groupName))
                    .getId();

            // SKTAI에서 그룹 삭제
            sktaiGroupService.deleteGroup(groupId);

            log.info("그룹 삭제 완료 (Admin): groupName={}, groupId={}", groupName, groupId);

        } finally {
            AdminContext.clear();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findGroupNamesByKeyword(String keyword) {
        log.info("그룹 키워드 검색 시작 (Admin): keyword={}", keyword);
        try {
            AdminContext.setAdminMode(ADMIN_USERNAME);
            ensureAdminToken();

            return sktaiGroupService.searchGroups(keyword, 1, 1000).getGroupList().stream()
                    .map(GroupResponse::getName)
                    .filter(name -> name.contains(keyword))
                    .toList();
        } finally {
            AdminContext.clear();
        }
    }

    @Override
    @Transactional
    public void deleteGroupsByKeyword(String keyword) {
        log.info("그룹 일괄 삭제 시작 (Admin): keyword={}", keyword);
        List<String> names = findGroupNamesByKeyword(keyword);
        for (String name : names) {
            try {
                deleteGroup(name);
            } catch (BusinessException ex) {
                // 비즈니스 예외 (예: 그룹을 찾을 수 없음)는 경고만 기록
                log.warn("그룹 삭제 실패 (무시, BusinessException): groupName={}, reason={}", name, ex.getMessage());
            } catch (IllegalArgumentException | NullPointerException ex) {
                // 잘못된 인자 예외는 경고만 기록
                log.warn("그룹 삭제 실패 (무시, 잘못된 인자): groupName={}, reason={}", name, ex.getMessage());
            } catch (Exception ex) {
                // 기타 예상치 못한 예외는 경고만 기록
                log.warn("그룹 삭제 실패 (무시): groupName={}, reason={}", name, ex.getMessage());
            }
        }
        log.info("그룹 일괄 삭제 완료 (Admin): keyword={}, count={}", keyword, names.size());
    }

    @Override
    @Transactional
    public List<PolicyRequest> updateResourcePolicy(String resourceUrl, List<PolicyRequest> policyRequests) {
        log.info("리소스 권한 정책 업데이트 시작 (Admin): resourceUrl={}", resourceUrl);

        try {
            AdminContext.setAdminMode(ADMIN_USERNAME);
            ensureAdminToken();

            // SKTAI에 권한 정책 업데이트
            List<PolicyRequest> response;

            try {
                response = sktaiAuthService.updatePolicy(resourceUrl, policyRequests);

                if (response == null || response.isEmpty()) {
                    log.debug("정책 업데이트 실패, 생성 시도: resourceUrl={}", resourceUrl);
                    response = sktaiAuthService.createPolicy(resourceUrl, policyRequests);
                }
            } catch (BusinessException e) {
                // 업데이트 실패 시 (예: 정책이 없음) 생성 시도
                log.debug("정책 업데이트 실패, 생성 시도: resourceUrl={}, error={}", resourceUrl, e.getMessage());
                response = sktaiAuthService.createPolicy(resourceUrl, policyRequests);
            } catch (IllegalArgumentException | NullPointerException e) {
                // 잘못된 인자 예외는 생성 시도
                log.debug("정책 업데이트 실패 (잘못된 인자), 생성 시도: resourceUrl={}", resourceUrl);
                response = sktaiAuthService.createPolicy(resourceUrl, policyRequests);
            } catch (Exception e) {
                // 기타 예외는 생성 시도
                log.debug("정책 업데이트 실패, 생성 시도: resourceUrl={}, error={}", resourceUrl, e.getMessage());
                response = sktaiAuthService.createPolicy(resourceUrl, policyRequests);
            }

            log.info("리소스 권한 정책 업데이트 완료 (Admin): resourceUrl={}, policies={}", resourceUrl, response.size());
            return response;
        } finally {
            AdminContext.clear();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MeResponse getCurrentUser() {
        log.info("현재 사용자 정보 조회 (Admin 컨텍스트)");

        try {
            AdminContext.setAdminMode(ADMIN_USERNAME);
            ensureAdminToken();

            MeResponse response = sktaiUserService.getCurrentUser();

            log.info("현재 사용자 정보 조회 성공 (Admin): userId={}", response.getId());
            return response;
        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            log.error("현재 사용자 정보 조회 실패 (Admin, BusinessException): {}", e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.error("현재 사용자 정보 조회 실패 (Admin, 잘못된 인자): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "현재 사용자 정보를 조회할 수 없습니다: " + e.getMessage());
        } catch (DataAccessException e) {
            // 데이터베이스 접근 오류
            log.error("현재 사용자 정보 조회 실패 (Admin, 데이터베이스 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "현재 사용자 정보를 조회할 수 없습니다: 데이터베이스 오류");
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("현재 사용자 정보 조회 실패 (Admin, 예상치 못한 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "현재 사용자 정보를 조회할 수 없습니다.");
        } finally {
            AdminContext.clear();
        }
    }

    /**
     * 현재 활성화된 프로젝트에 맞는 리소스 정책 설정
     *
     * @param resourceUrl 리소스 URL
     * @throws BusinessException 사용자 정보나 활성 프로젝트 조회 실패 시
     */
    @Override
    public void setResourcePolicyByCurrentGroup(String resourceUrl) {
        String username = tokenInfo.getUserName();
        if (!StringUtils.hasText(username)) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 정보 조회 실패");
        }

        // 현재 사용자의 활성 프로젝트 조회
        Long prjSeq = projectUserRoleRepository.findActivePrjSeqByMemberId(username)
                .orElse(null);
        if (prjSeq == null) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "활성 프로젝트 조회 실패");
        }

        this.setResourcePolicyByProjectSequence(resourceUrl, prjSeq);
    }

    /**
     * 현재 활성화된 프로젝트에 맞는 리소스 정책 설정
     *
     * @param resourceUrl 리소스 URL
     * @throws BusinessException 사용자 정보나 활성 프로젝트 조회 실패 시
     */
    @Override
    public void setResourcePolicyByCurrentGroupWithPut(String resourceUrl) {
        String username = tokenInfo.getUserName();
        if (!StringUtils.hasText(username)) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 정보 조회 실패");
        }

        // 현재 사용자의 활성 프로젝트 조회
        Long prjSeq = projectUserRoleRepository.findActivePrjSeqByMemberId(username)
                .orElse(null);
        if (prjSeq == null) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "활성 프로젝트 조회 실패");
        }

        this.setResourcePolicyByProjectSequence(resourceUrl, prjSeq, List.of("PUT"));
    }

    /**
     * 특정 프로젝트에 맞는 리소스 정책을 설정
     * <p>프라이빗 프로젝트의 경우 해당 프로젝트 시퀀스에 해당하는 그룹만 접근할 수 있도록 정책을 설정합니다.</p>
     *
     * @param resourceUrl 정책을 적용할 리소스의 URL
     * @param projectSeq  프로젝트 시퀀스
     * @throws BusinessException 정책 설정 실패 시
     */
    @Override
    public void setResourcePolicyByProjectSequence(String resourceUrl, long projectSeq) {
        setResourcePolicyByProjectSequence(resourceUrl, projectSeq, Collections.emptyList());
    }

    public void setResourcePolicyByProjectSequence(String resourceUrl, long projectSeq, List<String> allowedMethods) {
        log.info("프로젝트 리소스 정책 설정 시작: resourceUrl={}", resourceUrl);
        try {
            AdminContext.setAdminMode(ADMIN_USERNAME);
            ensureAdminToken();

            List<PolicyRequest> policyRequests;
            List<String> defaultGetMethods = new ArrayList<>(List.of("GET"));

            if (allowedMethods != null && !allowedMethods.isEmpty()) {
                defaultGetMethods.addAll(allowedMethods);
            }

            // 정책 생성
            if (projectSeq == -999) {
                policyRequests = List.of(
                        PolicyRequest.builder()
                                .scopes(Arrays.asList("GET", "POST", "PUT", "DELETE"))
                                .policies(List.of(PolicyItem.builder()
                                        .type("regex")
                                        .logic("POSITIVE")
                                        .targetClaim("current_group")
                                        .pattern("^/P\\-999_R\\-199$")
                                        .build()))
                                .logic("POSITIVE")
                                .decisionStrategy("AFFIRMATIVE")
                                .cascade(false)
                                .build(),
                        PolicyRequest.builder()
                                // .scopes(Arrays.asList("GET"))
                                .scopes(defaultGetMethods)
                                .policies(List.of(PolicyItem.builder()
                                        .type("group")
                                        .logic("POSITIVE")
                                        .names(List.of(
                                                "P-999_R-199",
                                                "P-999_R-198",
                                                "P-999_R-197",
                                                "P-999_R-196",
                                                "P-999_R-195"
                                        ))
                                        .build()))
                                .logic("POSITIVE")
                                .decisionStrategy("AFFIRMATIVE")
                                .cascade(false)
                                .build()
                );
            } else {
                policyRequests = List.of(
                        PolicyRequest.builder()
                                .scopes(Arrays.asList("GET", "POST", "PUT", "DELETE"))
                                .policies(List.of(
                                        PolicyItem.builder()
                                                .type("regex")
                                                .targetClaim("current_group")
                                                .pattern("^/P%s_R.+$".formatted(String.valueOf(projectSeq)).replaceAll("-", "\\\\-"))
                                                .logic("POSITIVE")
                                                .build(),
                                        PolicyItem.builder()
                                                .type("regex")
                                                .targetClaim("current_group")
                                                .pattern("^/P%s_R-297$".formatted(String.valueOf(projectSeq)).replaceAll("-", "\\\\-"))
                                                .logic("NEGATIVE")
                                                .build()
                                ))
                                .logic("POSITIVE")
                                .decisionStrategy("UNANIMOUS") // AND 조건
                                .cascade(false)
                                .build(),
                        // 테스터는 조회권한만
                        PolicyRequest.builder()
                                .scopes(Arrays.asList("GET"))
                                .policies(List.of(PolicyItem.builder()
                                        .type("regex")
                                        .targetClaim("current_group")
                                        .pattern("^/P%s_R\\-297$".formatted(String.valueOf(projectSeq).replaceAll("-", "\\\\-")))
                                        .logic("POSITIVE")
                                        .build()))
                                .logic("POSITIVE")
                                .decisionStrategy("AFFIRMATIVE")
                                .cascade(false)
                                .build()
                );
            }

            // SKTAI에 정책 반영
            List<PolicyRequest> response;

            try {
                response = sktaiAuthService.updatePolicy(resourceUrl, policyRequests);

                if (response == null || response.isEmpty()) {
                    log.debug("정책 업데이트 실패, 생성 시도: resourceUrl={}", resourceUrl);
                    response = sktaiAuthService.createPolicy(resourceUrl, policyRequests);
                }
            } catch (BusinessException e) {
                // 업데이트 실패 시 (예: 정책이 없음) 생성 시도
                sktaiAuthService.createPolicy(resourceUrl, policyRequests);
            } catch (IllegalArgumentException | NullPointerException e) {
                // 잘못된 인자 예외는 생성 시도
                log.debug("정책 업데이트 실패 (잘못된 인자), 생성 시도: resourceUrl={}", resourceUrl);
                sktaiAuthService.createPolicy(resourceUrl, policyRequests);
            } catch (Exception e) {
                // 기타 예외는 생성 시도
                log.debug("정책 업데이트 실패, 생성 시도: resourceUrl={}, error={}", resourceUrl, e.getMessage());
                sktaiAuthService.createPolicy(resourceUrl, policyRequests);
            }

            // 정책 반영 이력(매핑) 저장: 기존 레코드 삭제 후 신규 입력
            try {
                assetPrjMapMasRepository.deleteByAsstUrl(resourceUrl);
            } catch (DataAccessException e) {
                // 삭제 시 존재하지 않아도 무시 (데이터베이스 예외)
                log.debug("기존 매핑 레코드 삭제 실패 (무시, DataAccessException): resourceUrl={}, error={}", resourceUrl, e.getMessage());
            } catch (IllegalArgumentException e) {
                // 잘못된 인자 예외도 무시
                log.debug("기존 매핑 레코드 삭제 실패 (무시, 잘못된 인자): resourceUrl={}, error={}", resourceUrl, e.getMessage());
            } catch (Exception e) {
                // 기타 예외도 무시 (레코드가 없을 수 있음)
                log.debug("기존 매핑 레코드 삭제 실패 (무시): resourceUrl={}, error={}", resourceUrl, e.getMessage());
            }

            GpoAssetPrjMapMas mapping = GpoAssetPrjMapMas.builder()
                    .asstUrl(resourceUrl)
                    .fstPrjSeq(Math.toIntExact(projectSeq))
                    .lstPrjSeq(Math.toIntExact(projectSeq))
                    .build();
            assetPrjMapMasRepository.save(mapping);

            log.info("프라이빗 프로젝트 리소스 정책 설정 완료: resourceUrl={}, prjSeq={}", resourceUrl, projectSeq);
        } catch (BusinessException ex) {
            // 비즈니스 예외는 그대로 전파
            log.error("프라이빗 프로젝트 리소스 정책 설정 실패 (BusinessException): {}", ex.getMessage(), ex);
            throw ex;
        } catch (IllegalArgumentException | NullPointerException ex) {
            // 잘못된 인자나 null 참조 예외
            log.error("프라이빗 프로젝트 리소스 정책 설정 실패 (잘못된 인자): {}", ex.getMessage(), ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "리소스 정책 설정 실패: " + ex.getMessage());
        } catch (DataAccessException ex) {
            // 데이터베이스 접근 오류
            log.error("프라이빗 프로젝트 리소스 정책 설정 실패 (데이터베이스 오류): {}", ex.getMessage(), ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "리소스 정책 설정 실패: 데이터베이스 오류");
        } catch (Exception ex) {
            // 기타 예상치 못한 예외
            log.error("프라이빗 프로젝트 리소스 정책 설정 실패 (예상치 못한 오류): {}", ex.getMessage(), ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "리소스 정책 설정 실패");
        } finally {
            AdminContext.clear();
        }
    }

    /**
     * 프라이빗 프로젝트 정책을 기반으로 공개 프로젝트 정책을 설정
     *
     * <p>관리자는 쓰기(GET/POST/PUT/DELETE) 권한을 가지며, 일반 사용자는 조회(GET) 권한만 허용됩니다.</p>
     *
     * @param resourceUrl 정책을 적용할 리소스의 URL
     * @throws BusinessException 정책 설정 실패 시
     */
    @Override
    public void setResourcePublicPolicyFromPrivate(String resourceUrl) {
        log.info("리소스 공개프로젝트 정책 설정 시작: resourceUrl={}", resourceUrl);
        try {
            AdminContext.setAdminMode(ADMIN_USERNAME);
            ensureAdminToken();

            // 관리자만 수정 / 나머지는 조회만 가능
            List<PolicyRequest> policyRequests = List.of(
                    PolicyRequest.builder()
                            .scopes(Arrays.asList("GET", "POST", "PUT", "DELETE"))
                            .policies(List.of(PolicyItem.builder()
                                    .type("regex")
                                    .logic("POSITIVE")
                                    .targetClaim("current_group")
                                    .pattern("^/P\\-999_R\\-199$")
                                    .build()))
                            .logic("POSITIVE")
                            .decisionStrategy("AFFIRMATIVE")
                            .cascade(false)
                            .build(),
                    PolicyRequest.builder()
                            .scopes(Arrays.asList("GET"))
                            .policies(List.of(PolicyItem.builder()
                                    .type("group")
                                    .logic("POSITIVE")
                                    .names(List.of(
                                            "P-999_R-199",
                                            "P-999_R-198",
                                            "P-999_R-197",
                                            "P-999_R-196",
                                            "P-999_R-195"
                                    ))
                                    .build()))
                            .logic("POSITIVE")
                            .decisionStrategy("AFFIRMATIVE")
                            .cascade(false)
                            .build()
            );

            // 공개 정책 반영 전 검증: 매핑 데이터 존재 여부 확인
            GpoAssetPrjMapMas existing = assetPrjMapMasRepository.findByAsstUrl(resourceUrl).orElse(null);
            if (existing == null) {
                log.error("gpo_asstprj_map_mas에 해당 리소스 URL이 존재하지 않습니다: {}", resourceUrl);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "공개 프로젝트로 전환할 에셋이 존재하지 않습니다.");
            }

            // SKTAI에 정책 반영
            sktaiAuthService.createPolicy(resourceUrl, policyRequests);

            // 성공 시 lst_prj_seq만 -999로 업데이트
            existing.setLstPrjSeq(-999);
            assetPrjMapMasRepository.save(existing);

            log.info("리소스 공개프로젝트 정책 설정 완료: resourceUrl={}", resourceUrl);
        } catch (BusinessException ex) {
            // 비즈니스 예외는 그대로 전파
            log.error("리소스 공개프로젝트 정책 설정 실패 (BusinessException): {}", ex.getMessage(), ex);
            throw ex;
        } catch (IllegalArgumentException | NullPointerException ex) {
            // 잘못된 인자나 null 참조 예외
            log.error("리소스 공개프로젝트 정책 설정 실패 (잘못된 인자): {}", ex.getMessage(), ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "리소스 정책 설정 실패: " + ex.getMessage());
        } catch (DataAccessException ex) {
            // 데이터베이스 접근 오류
            log.error("리소스 공개프로젝트 정책 설정 실패 (데이터베이스 오류): {}", ex.getMessage(), ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "리소스 정책 설정 실패: 데이터베이스 오류");
        } catch (Exception ex) {
            // 기타 예상치 못한 예외
            log.error("리소스 공개프로젝트 정책 설정 실패 (예상치 못한 오류): {}", ex.getMessage(), ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "리소스 정책 설정 실패");
        } finally {
            AdminContext.clear();
        }
    }

    /**
     * 현재 활성 프로젝트 그룹 기준으로 정책 요청 목록 조회
     *
     * <p>현재 로그인한 사용자의 활성 프로젝트를 조회한 후, 해당 프로젝트 시퀀스에 맞는 정책 요청 목록을 반환합니다.</p>
     *
     * @return 정책 요청 목록
     * @throws BusinessException 사용자 정보 또는 활성 프로젝트 조회 실패 시
     */
    @Override
    public List<PolicyRequest> getPolicyRequestsByCurrentGroup() {
        String username = tokenInfo.getUserName();
        if (!StringUtils.hasText(username)) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 정보 조회 실패");
        }

        // 현재 사용자의 활성 프로젝트 조회
        Long prjSeq = projectUserRoleRepository.findActivePrjSeqByMemberId(username)
                .orElse(null);
        if (prjSeq == null) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "활성 프로젝트 조회 실패");
        }

        return getPolicyRequestsByCurrentProjectSequence(prjSeq);
    }

    /**
     * 특정 사용자 ID 기준으로 정책 요청 목록 조회
     *
     * <p>사용자 ID를 받아 해당 사용자의 활성 프로젝트를 조회한 후,
     * 해당 프로젝트 시퀀스에 맞는 정책 요청 목록을 반환합니다.</p>
     *
     * @param userId 사용자 ID
     * @return 정책 요청 목록
     * @throws BusinessException 사용자 정보 또는 활성 프로젝트 조회 실패 시
     */
    @Override
    public List<PolicyRequest> getPolicyRequestsByUserId(String userId) {
        log.info("getPolicyRequestsByUserId 호출 - userId: [{}]", userId);
        if (!StringUtils.hasText(userId)) {
            log.error("getPolicyRequestsByUserId - userId가 비어있음");
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "사용자 ID가 필요합니다.");
        }

        // 특정 사용자의 활성 프로젝트 조회
        log.info("getPolicyRequestsByUserId - 활성 프로젝트 조회 시작 - userId: [{}]", userId);
        Long prjSeq = projectUserRoleRepository.findActivePrjSeqByMemberId(userId)
                .orElse(null);
        if (prjSeq == null) {
            log.error("getPolicyRequestsByUserId - 활성 프로젝트 조회 실패 - userId: [{}]", userId);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    String.format("사용자의 활성 프로젝트 조회 실패 - userId: %s", userId));
        }

        log.info("getPolicyRequestsByUserId - 사용자 활성 프로젝트 조회 완료 - userId: [{}], prjSeq: {}", userId, prjSeq);
        return getPolicyRequestsByCurrentProjectSequence(prjSeq);
    }

    /**
     * 사용자 ID와 프로젝트명 기준으로 정책 요청 목록 조회
     *
     * <p>프로젝트명(GPO_PRJ_NM)으로 프로젝트를 조회한 후,
     * 해당 프로젝트 시퀀스에 맞는 정책 요청 목록을 반환합니다.</p>
     *
     * @param memberId    사용자 ID (검증용, 선택적)
     * @param projectName 프로젝트명 (GPO_PRJ_NM)
     * @return 정책 요청 목록
     * @throws BusinessException 프로젝트 조회 실패 시
     */
    @Override
    public List<PolicyRequest> getPolicyRequestsByMemberIdAndProjectName(String memberId, String projectName) {
        log.info("getPolicyRequestsByMemberIdAndProjectName 호출 - memberId: [{}], projectName: [{}]", memberId, projectName);

        if (!StringUtils.hasText(projectName)) {
            log.error("getPolicyRequestsByMemberIdAndProjectName - projectName이 비어있음");
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "프로젝트명이 필요합니다.");
        }

        // 프로젝트명으로 프로젝트 조회
        log.info("getPolicyRequestsByMemberIdAndProjectName - 프로젝트 조회 시작 - projectName: [{}]", projectName);
        Project project = projectMgmtRepository.findByPrjNm(projectName)
                .orElseThrow(() -> {
                    log.error("getPolicyRequestsByMemberIdAndProjectName - 프로젝트 조회 실패 - projectName: [{}]", projectName);
                    return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                            String.format("프로젝트를 찾을 수 없습니다: %s", projectName));
                });

        Long prjSeq = project.getPrjSeq();
        if (prjSeq == null) {
            log.error("getPolicyRequestsByMemberIdAndProjectName - 프로젝트 시퀀스가 null - projectName: [{}]", projectName);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    String.format("프로젝트 시퀀스를 찾을 수 없습니다: %s", projectName));
        }

        log.info("getPolicyRequestsByMemberIdAndProjectName - 프로젝트 조회 완료 - projectName: [{}], prjSeq: {}", projectName, prjSeq);

        // 프로젝트 시퀀스로 정책 생성
        return getPolicyRequestsByCurrentProjectSequence(prjSeq);
    }

    /**
     * 특정 프로젝트 시퀀스 기준으로 정책 요청 목록 조회
     *
     * <p>프로젝트 시퀀스에 따라 적절한 접근 제어 정책을 생성합니다.</p>
     *
     * @param projectSeq 프로젝트 시퀀스 (-999: 공개 프로젝트, 그 외: 프라이빗 프로젝트)
     * @return 정책 요청 목록
     */
    @Override
    public List<PolicyRequest> getPolicyRequestsByCurrentProjectSequence(long projectSeq) {
        List<PolicyRequest> policyRequests;

        // 정책 생성
        if (projectSeq == -999) {
            policyRequests = List.of(
                    PolicyRequest.builder()
                            .scopes(Arrays.asList("GET", "POST", "PUT", "DELETE"))
                            .policies(List.of(PolicyItem.builder()
                                    .type("regex")
                                    .logic("POSITIVE")
                                    .targetClaim("current_group")
                                    .pattern("^/P\\-999_R\\-199$")
                                    .build()))
                            .logic("POSITIVE")
                            .decisionStrategy("AFFIRMATIVE")
                            .cascade(false)
                            .build(),
                    PolicyRequest.builder()
                            .scopes(Arrays.asList("GET"))
                            .policies(List.of(PolicyItem.builder()
                                    .type("group")
                                    .logic("POSITIVE")
                                    .names(List.of(
                                            "P-999_R-199",
                                            "P-999_R-198",
                                            "P-999_R-197",
                                            "P-999_R-196",
                                            "P-999_R-195"
                                    ))
                                    .build()))
                            .logic("POSITIVE")
                            .decisionStrategy("AFFIRMATIVE")
                            .cascade(false)
                            .build()
            );
        } else {
            policyRequests = List.of(
                    PolicyRequest.builder()
                            .scopes(Arrays.asList("GET", "POST", "PUT", "DELETE"))
                            .policies(List.of(
                                    PolicyItem.builder()
                                            .type("regex")
                                            .targetClaim("current_group")
                                            .pattern("^/P%s_R.+$".formatted(String.valueOf(projectSeq)).replaceAll("-", "\\\\-"))
                                            .logic("POSITIVE")
                                            .build(),
                                    PolicyItem.builder()
                                            .type("regex")
                                            .targetClaim("current_group")
                                            .pattern("^/P%s_R-297$".formatted(String.valueOf(projectSeq)).replaceAll("-", "\\\\-"))
                                            .logic("NEGATIVE")
                                            .build()
                            ))
                            .logic("POSITIVE")
                            .decisionStrategy("UNANIMOUS") // AND 조건
                            .cascade(false)
                            .build(),
                    // 테스터는 조회권한만
                    PolicyRequest.builder()
                            .scopes(Arrays.asList("GET"))
                            .policies(List.of(PolicyItem.builder()
                                    .type("regex")
                                    .targetClaim("current_group")
                                    .pattern("^/P%s_R\\-297$".formatted(String.valueOf(projectSeq).replaceAll("-", "\\\\-")))
                                    .logic("POSITIVE")
                                    .build()))
                            .logic("POSITIVE")
                            .decisionStrategy("AFFIRMATIVE")
                            .cascade(false)
                            .build()
            );
        }

        return policyRequests;
    }

    /**
     * 프로젝트명을 기준으로 프로젝트 정보를 조회 (Admin 권한 필요)
     *
     * <p>SKTAI의 프로젝트 목록 조회 API를 사용해 검색 후, 이름이 파라미터와 일치하는 항목을 반환합니다.
     * 조회된 결과는 캐시에 저장되어 이후 동일한 프로젝트명 조회 시 캐시에서 반환됩니다.</p>
     *
     * @param projectName 프로젝트명
     * @return 일치하는 프로젝트 정보(ClientRead), 없으면 null
     * @throws BusinessException 프로젝트 조회 실패 시
     */
    @Override
    @Transactional(readOnly = true)
    public ClientRead getProjectByName(String projectName) {
        log.info("프로젝트명으로 프로젝트 조회 시작 (Admin): projectName={}", projectName);
        if (!StringUtils.hasText(projectName)) {
            log.warn("프로젝트명이 비어있습니다.");
            return null;
        }

        // 1차: 캐시 조회 (변하지 않는 값이므로 최초 1회만 원격 호출)
        ClientRead cached = projectByNameCache.get(projectName);
        if (cached != null) {
            log.debug("프로젝트 조회 캐시 히트: projectName={}", projectName);
            return cached;
        }

        try {
            AdminContext.setAdminMode(ADMIN_USERNAME);
            ensureAdminToken();

            ClientsRead clients = sktaiProjectClient.getProjects(1, 1000, null, null, projectName);
            if (clients == null || clients.getData() == null) {
                return null;
            }
            ClientRead found = clients.getData().stream()
                    .filter(cr -> cr != null && cr.getProject() != null && projectName.equals(cr.getProject().getName()))
                    .findFirst()
                    .orElse(null);

            // 조회 성공 시 캐시에 저장 (null은 캐시하지 않음)
            if (found != null) {
                projectByNameCache.putIfAbsent(projectName, found);
            }
            return found;
        } catch (BusinessException e) {
            // 비즈니스 예외는 그대로 전파
            log.error("프로젝트 조회 실패 (Admin, BusinessException): {}", e.getMessage(), e);
            throw e;
        } catch (IllegalArgumentException | NullPointerException e) {
            // 잘못된 인자나 null 참조 예외
            log.error("프로젝트 조회 실패 (Admin, 잘못된 인자): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "프로젝트 조회 실패: " + e.getMessage());
        } catch (DataAccessException e) {
            // 데이터베이스 접근 오류
            log.error("프로젝트 조회 실패 (Admin, 데이터베이스 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "프로젝트 조회 실패: 데이터베이스 오류");
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.error("프로젝트 조회 실패 (Admin, 예상치 못한 오류): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "프로젝트 조회 실패");
        } finally {
            AdminContext.clear();
        }
    }

    private LocalDateTime extractExpAtFromAccessToken(String accessToken, Long fallbackExpiresInSec) {
        try {
            if (!StringUtils.hasText(accessToken)) {
                throw new IllegalArgumentException("accessToken is blank");
            }
            String[] parts = accessToken.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid JWT format");
            }
            byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
            String payloadJson = new String(decoded, java.nio.charset.StandardCharsets.UTF_8);
            JsonNode node = new ObjectMapper().readTree(payloadJson);

            if (node.has("exp") && !node.get("exp").isNull()) {
                long expSeconds = node.get("exp").asLong();
                return Instant.ofEpochSecond(expSeconds)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }
        } catch (IllegalArgumentException e) {
            // 잘못된 JWT 형식 또는 Base64 디코딩 실패
            log.debug("Failed to extract exp from access token (IllegalArgumentException), fallback to expiresIn: {}", e.getMessage());
        } catch (JsonProcessingException e) {
            // JSON 파싱 실패
            log.debug("Failed to extract exp from access token (JsonProcessingException), fallback to expiresIn: {}", e.getMessage());
        } catch (NullPointerException e) {
            // null 참조 예외
            log.debug("Failed to extract exp from access token (NullPointerException), fallback to expiresIn: {}", e.getMessage());
        } catch (Exception e) {
            // 기타 예상치 못한 예외
            log.debug("Failed to extract exp from access token, fallback to expiresIn: {}", e.getMessage());
        }

        long fallback = fallbackExpiresInSec != null ? fallbackExpiresInSec : 0L;
        return LocalDateTime.now().plusSeconds(fallback);
    }

    /**
     * 사용자 ID와 프로젝트명으로 리소스 정책 설정
     *
     * @param resourceUrl 리소스 URL
     * @param memberId    사용자 ID
     * @param projectName 프로젝트명
     * @throws BusinessException 프로젝트 조회 실패 시
     */
    @Override
    public void setResourcePolicyByMemberIdAndProjectName(String resourceUrl, String memberId, String projectName) {

        // 현재 사용자의 활성 프로젝트 조회
        Project project = projectMgmtRepository.findByMemberIdAndPrjNm(memberId, projectName)
                .orElseThrow(() -> {
                    log.error(
                            "setResourcePolicyByMemberIdAndProjectName - 프로젝트 조회 실패 - memberId: [{}], projectName: [{}]",
                            memberId, projectName);
                    return new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "활성 프로젝트 조회 실패");
                });

        if (project.getPrjSeq() == null) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "프로젝트 시퀀스가 없습니다");
        }

        this.setResourcePolicyByProjectSequence(resourceUrl, project.getPrjSeq());
    }

}
