package com.skax.aiplatform.client.sktai.auth.service;

import com.skax.aiplatform.client.sktai.auth.SktaiGroupClient;
import com.skax.aiplatform.client.sktai.auth.dto.response.GroupResponse;
import com.skax.aiplatform.client.sktai.auth.dto.response.GroupsResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI 그룹 관리 비즈니스 로직 서비스
 * 
 * <p>SKTAI Auth API의 그룹 관리 기능을 위한 비즈니스 로직을 제공하는 서비스입니다.
 * Feign Client를 래핑하여 예외 처리, 로깅, 데이터 변환 등의 추가 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>그룹 조회</strong>: 시스템 그룹 목록 조회 및 검색</li>
 *   <li><strong>필터링</strong>: 그룹 유형, 활성 상태 기반 필터링</li>
 *   <li><strong>검색</strong>: 그룹명, 설명 기반 검색</li>
 *   <li><strong>페이징</strong>: 대용량 그룹 데이터 효율적 처리</li>
 *   <li><strong>통합 에러 처리</strong>: 외부 API 오류를 내부 예외로 변환</li>
 *   <li><strong>상세 로깅</strong>: 모든 API 호출과 결과를 추적</li>
 * </ul>
 * 
 * <h3>그룹 유형별 관리:</h3>
 * <ul>
 *   <li><strong>Department</strong>: 부서/조직 그룹 관리</li>
 *   <li><strong>Project</strong>: 프로젝트 기반 그룹 관리</li>
 *   <li><strong>Role</strong>: 역할 기반 그룹 관리</li>
 *   <li><strong>Custom</strong>: 사용자 정의 그룹 관리</li>
 * </ul>
 * 
 * <h3>검색 기능:</h3>
 * <ul>
 *   <li><strong>그룹명 검색</strong>: name 필드 기반 부분 일치</li>
 *   <li><strong>설명 검색</strong>: description 필드 기반 부분 일치</li>
 *   <li><strong>태그 검색</strong>: tags 필드 기반 부분 일치</li>
 *   <li><strong>통합 검색</strong>: 여러 필드에서 동시 검색</li>
 * </ul>
 * 
 * <h3>에러 처리 전략:</h3>
 * <ul>
 *   <li><strong>401/403 오류</strong>: 인증/권한 관련 BusinessException 발생</li>
 *   <li><strong>422 오류</strong>: 유효성 검증 실패 BusinessException 발생</li>
 *   <li><strong>기타 오류</strong>: 일반적인 외부 API 오류로 처리</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // 기본 그룹 목록 조회
 * Object groups = sktaiGroupService.getGroups(1, 20, null, null, null);
 * 
 * // 부서 그룹만 조회
 * Object departmentGroups = sktaiGroupService.getDepartmentGroups(1, 10, null);
 * 
 * // 특정 그룹 검색
 * Object searchResult = sktaiGroupService.searchGroups("development", 1, 10);
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see SktaiGroupClient 그룹 관리 Feign Client
 * @see ErrorCode 에러 코드 정의
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiGroupService {
    
    private final SktaiGroupClient sktaiGroupClient;
    
    /**
     * 그룹 목록 조회
     * 
     * <p>시스템의 그룹 목록을 페이징하여 조회합니다.
     * 검색어, 필터, 정렬 옵션을 통해 원하는 그룹을 찾을 수 있습니다.</p>
     * 
     * <h3>검색 기능 지원:</h3>
     * <ul>
     *   <li><strong>그룹명</strong>: name 필드에서 부분 일치 검색</li>
     *   <li><strong>설명</strong>: description 필드에서 부분 일치 검색</li>
     *   <li><strong>태그</strong>: tags 필드에서 부분 일치 검색</li>
     * </ul>
     * 
     * <h3>필터링 옵션:</h3>
     * <ul>
     *   <li><strong>department</strong>: 부서/조직 그룹만 조회</li>
     *   <li><strong>project</strong>: 프로젝트 기반 그룹만 조회</li>
     *   <li><strong>role</strong>: 역할 기반 그룹만 조회</li>
     *   <li><strong>custom</strong>: 사용자 정의 그룹만 조회</li>
     *   <li><strong>active</strong>: 활성 그룹만 조회</li>
     *   <li><strong>inactive</strong>: 비활성 그룹만 조회</li>
     * </ul>
     * 
     * <h3>정렬 옵션:</h3>
     * <ul>
     *   <li><strong>name</strong>: 그룹명 오름차순</li>
     *   <li><strong>created_at desc</strong>: 생성일 내림차순</li>
     *   <li><strong>member_count desc</strong>: 멤버 수 내림차순</li>
     * </ul>
     * 
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 10, 최대값: 100)
     * @param sort 정렬 조건 (예: "name", "created_at desc")
     * @param filter 필터 조건 (department, project, role, custom, active, inactive 등)
     * @param search 검색어 (그룹명, 설명, 태그에서 검색)
     * @return 페이징된 그룹 목록과 메타데이터
     * @throws BusinessException 그룹 목록 조회 실패 시
     * 
     * @implNote 그룹 멤버 정보는 별도 API를 통해 조회해야 합니다.
     * @apiNote 그룹 관리 권한이 필요한 API입니다.
     */
    public GroupsResponse getGroups(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("SKTAI 그룹 목록 조회 - page: {}, size: {}, sort: {}, filter: {}, search: {}",
                page, size, sort, filter, search);
        
        try {
            GroupsResponse response = sktaiGroupClient.getGroups(page, size, sort, filter, search);
            log.debug("SKTAI 그룹 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI 그룹 목록 조회 실패 (BusinessException) - page: {}, size: {}, filter: {}, search: {}, message: {}",
                    page, size, filter, search, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 그룹 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}, filter: {}, search: {}",
                    page, size, filter, search, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "그룹 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 그룹 생성
     * 
     * <p>새로운 그룹을 생성합니다.
     * 그룹명과 유형은 필수이며, 추가 속성은 선택적으로 제공할 수 있습니다.</p>
     * 
     * @param request 그룹 생성 요청 데이터
     * @return 생성된 그룹 정보
     * @throws BusinessException 그룹 생성 실패 시
     * 
     * @apiNote 그룹 관리 권한이 필요한 API입니다.
     */
    public GroupResponse createGroup(String groupName) {
        log.debug("SKTAI 그룹 생성 - name: {}", groupName);
        
        try {
            GroupResponse response = sktaiGroupClient.createGroup(groupName);
            log.info("SKTAI 그룹 생성 성공 - name: {}, id: {}", groupName, response.getId());
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI 그룹 생성 실패 (BusinessException) - name: {}, message: {}", 
                    groupName, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 그룹 생성 실패 (예상치 못한 오류) - name: {}", groupName, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "그룹 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 그룹 삭제
     * 
     * <p>기존 그룹을 삭제합니다.
     * 그룹에 멤버가 있는 경우 삭제가 실패할 수 있습니다.</p>
     * 
     * @param groupId 그룹 ID
     * @throws BusinessException 그룹 삭제 실패 시
     * 
     * @apiNote 그룹 관리 권한이 필요한 API입니다.
     */
    public void deleteGroup(String groupId) {
        log.debug("SKTAI 그룹 삭제 - groupId: {}", groupId);
        
        try {
            sktaiGroupClient.deleteGroup(groupId);
            log.info("SKTAI 그룹 삭제 성공 - groupId: {}", groupId);
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI 그룹 삭제 실패 (BusinessException) - groupId: {}, message: {}", 
                    groupId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 그룹 삭제 실패 (예상치 못한 오류) - groupId: {}", groupId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "그룹 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 부서 그룹 목록 조회
     * 
     * <p>부서/조직 유형의 그룹들만 조회하는 편의 메서드입니다.
     * 조직 관리나 부서별 권한 설정 시 주로 사용됩니다.</p>
     * 
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param search 검색어 (선택사항)
     * @return 부서 그룹 목록
     * @throws BusinessException 조회 실패 시
     */
    public Object getDepartmentGroups(Integer page, Integer size, String search) {
        log.debug("SKTAI 부서 그룹 목록 조회 - page: {}, size: {}, search: {}", page, size, search);
        
        try {
            Object response = sktaiGroupClient.getGroups(page, size, null, "department", search);
            log.debug("SKTAI 부서 그룹 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI 부서 그룹 목록 조회 실패 (BusinessException) - page: {}, size: {}, search: {}, message: {}",
                    page, size, search, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 부서 그룹 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}, search: {}",
                    page, size, search, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "부서 그룹 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 프로젝트 그룹 목록 조회
     * 
     * <p>프로젝트 기반 그룹들만 조회하는 편의 메서드입니다.
     * 프로젝트 관리나 프로젝트별 팀 구성 시 주로 사용됩니다.</p>
     * 
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param search 검색어 (선택사항)
     * @return 프로젝트 그룹 목록
     * @throws BusinessException 조회 실패 시
     */
    public Object getProjectGroups(Integer page, Integer size, String search) {
        log.debug("SKTAI 프로젝트 그룹 목록 조회 - page: {}, size: {}, search: {}", page, size, search);
        
        try {
            Object response = sktaiGroupClient.getGroups(page, size, null, "project", search);
            log.debug("SKTAI 프로젝트 그룹 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI 프로젝트 그룹 목록 조회 실패 (BusinessException) - page: {}, size: {}, search: {}, message: {}",
                    page, size, search, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 프로젝트 그룹 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}, search: {}",
                    page, size, search, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "프로젝트 그룹 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 역할 기반 그룹 목록 조회
     * 
     * <p>역할 기반 그룹들만 조회하는 편의 메서드입니다.
     * 권한 관리나 역할별 사용자 그룹화 시 주로 사용됩니다.</p>
     * 
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param search 검색어 (선택사항)
     * @return 역할 기반 그룹 목록
     * @throws BusinessException 조회 실패 시
     */
    public Object getRoleGroups(Integer page, Integer size, String search) {
        log.debug("SKTAI 역할 기반 그룹 목록 조회 - page: {}, size: {}, search: {}", page, size, search);
        
        try {
            Object response = sktaiGroupClient.getGroups(page, size, null, "role", search);
            log.debug("SKTAI 역할 기반 그룹 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI 역할 기반 그룹 목록 조회 실패 (BusinessException) - page: {}, size: {}, search: {}, message: {}",
                    page, size, search, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 역할 기반 그룹 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}, search: {}",
                    page, size, search, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "역할 기반 그룹 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 활성 그룹 목록 조회
     * 
     * <p>활성 상태인 그룹들만 조회하는 편의 메서드입니다.
     * 현재 사용 중인 그룹들을 파악하거나 그룹 할당 시 주로 사용됩니다.</p>
     * 
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param search 검색어 (선택사항)
     * @return 활성 그룹 목록
     * @throws BusinessException 조회 실패 시
     */
    public Object getActiveGroups(Integer page, Integer size, String search) {
        log.debug("SKTAI 활성 그룹 목록 조회 - page: {}, size: {}, search: {}", page, size, search);
        
        try {
            Object response = sktaiGroupClient.getGroups(page, size, null, "active", search);
            log.debug("SKTAI 활성 그룹 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI 활성 그룹 목록 조회 실패 (BusinessException) - page: {}, size: {}, search: {}, message: {}",
                    page, size, search, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 활성 그룹 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}, search: {}",
                    page, size, search, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "활성 그룹 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 그룹 검색
     * 
     * <p>특정 검색어로 그룹을 검색하는 편의 메서드입니다.
     * 그룹명, 설명, 태그에서 검색어와 일치하는 그룹을 찾습니다.</p>
     * 
     * @param searchKeyword 검색어
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @return 검색 결과
     * @throws BusinessException 검색 실패 시
     */
    public GroupsResponse searchGroups(String searchKeyword, Integer page, Integer size) {
        log.debug("SKTAI 그룹 검색 - keyword: {}, page: {}, size: {}", searchKeyword, page, size);
        
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            log.warn("검색어가 비어있어 전체 그룹 목록을 조회합니다");
            return getGroups(page, size, null, null, null);
        }
        
        try {
            GroupsResponse response = sktaiGroupClient.getGroups(page, size, null, null, searchKeyword.trim());
            log.debug("SKTAI 그룹 검색 성공 - keyword: {}", searchKeyword);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI 그룹 검색 실패 (BusinessException) - keyword: {}, message: {}", 
                    searchKeyword, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 그룹 검색 실패 (예상치 못한 오류) - keyword: {}", searchKeyword, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "그룹 검색에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 멤버 수 기준 그룹 조회
     * 
     * <p>멤버 수가 많은 순으로 그룹을 정렬하여 조회합니다.
     * 대규모 그룹 식별이나 그룹 사용 현황 분석에 활용됩니다.</p>
     * 
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @return 멤버 수 기준 그룹 목록
     * @throws BusinessException 조회 실패 시
     */
    public Object getGroupsByMemberCount(Integer page, Integer size) {
        log.debug("SKTAI 멤버 수 기준 그룹 조회 - page: {}, size: {}", page, size);
        
        try {
            Object response = sktaiGroupClient.getGroups(page, size, "member_count desc", "active", null);
            log.debug("SKTAI 멤버 수 기준 그룹 조회 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI 멤버 수 기준 그룹 조회 실패 (BusinessException) - page: {}, size: {}, message: {}", 
                    page, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 멤버 수 기준 그룹 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "멤버 수 기준 그룹 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 사용자 정의 그룹 목록 조회
     * 
     * <p>사용자가 직접 생성한 커스텀 그룹들만 조회하는 편의 메서드입니다.
     * 특별한 목적으로 구성된 그룹들을 관리할 때 사용됩니다.</p>
     * 
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param search 검색어 (선택사항)
     * @return 사용자 정의 그룹 목록
     * @throws BusinessException 조회 실패 시
     */
    public Object getCustomGroups(Integer page, Integer size, String search) {
        log.debug("SKTAI 사용자 정의 그룹 목록 조회 - page: {}, size: {}, search: {}", page, size, search);
        
        try {
            Object response = sktaiGroupClient.getGroups(page, size, null, "custom", search);
            log.debug("SKTAI 사용자 정의 그룹 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("SKTAI 사용자 정의 그룹 목록 조회 실패 (BusinessException) - page: {}, size: {}, search: {}, message: {}",
                    page, size, search, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("SKTAI 사용자 정의 그룹 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}, search: {}",
                    page, size, search, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "사용자 정의 그룹 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
}
