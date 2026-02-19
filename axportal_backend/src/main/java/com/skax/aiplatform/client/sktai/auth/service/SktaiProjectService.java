package com.skax.aiplatform.client.sktai.auth.service;

import com.skax.aiplatform.client.sktai.auth.SktaiProjectClient;
import com.skax.aiplatform.client.sktai.auth.dto.request.CreateClient;
import com.skax.aiplatform.client.sktai.auth.dto.request.CreateProjectRole;
import com.skax.aiplatform.client.sktai.auth.dto.request.UpdateClient;
import com.skax.aiplatform.client.sktai.auth.dto.response.ClientRead;
import com.skax.aiplatform.client.sktai.auth.dto.response.ClientsRead;
import com.skax.aiplatform.client.sktai.auth.dto.response.CreatedClientRead;
import com.skax.aiplatform.client.sktai.auth.dto.response.RoleBase;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI 프로젝트 관리 비즈니스 로직 서비스
 * 
 * <p>SKTAI Auth API의 프로젝트 관리 기능을 위한 비즈니스 로직을 제공하는 서비스입니다.
 * Feign Client를 래핑하여 예외 처리, 로깅, 데이터 변환 등의 추가 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>프로젝트 생명주기 관리</strong>: 생성, 조회, 수정, 삭제</li>
 *   <li><strong>프로젝트 역할 관리</strong>: 역할 생성, 사용자 매핑, 권한 제어</li>
 *   <li><strong>사용자 권한 관리</strong>: 프로젝트별 사용자 권한 할당 및 해제</li>
 *   <li><strong>통합 에러 처리</strong>: 외부 API 오류를 내부 예외로 변환</li>
 *   <li><strong>상세 로깅</strong>: 모든 API 호출과 결과를 추적</li>
 * </ul>
 * 
 * <h3>에러 처리 전략:</h3>
 * <ul>
 *   <li><strong>401/403 오류</strong>: 인증/권한 관련 BusinessException 발생</li>
 *   <li><strong>404 오류</strong>: 리소스 없음 BusinessException 발생</li>
 *   <li><strong>422 오류</strong>: 유효성 검증 실패 ValidationException 발생</li>
 *   <li><strong>기타 오류</strong>: 일반적인 외부 API 오류로 처리</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // 프로젝트 생성
 * CreateClient request = CreateClient.builder()
 *     .name("MyProject")
 *     .description("프로젝트 설명")
 *     .build();
 * CreatedClientRead result = sktaiProjectService.createProject(request);
 * 
 * // 프로젝트 목록 조회
 * ClientsRead projects = sktaiProjectService.getProjects(1, 20, null, null, null);
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see SktaiProjectClient 프로젝트 관리 Feign Client
 * @see ErrorCode 에러 코드 정의
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiProjectService {
    
    private final SktaiProjectClient sktaiProjectClient;
    
    /**
     * 새로운 프로젝트 생성
     * 
     * <p>SKTAI 시스템에 새로운 프로젝트를 생성합니다.
     * 프로젝트 생성과 함께 기본 네임스페이스가 할당됩니다.</p>
     * 
     * @param request 프로젝트 생성 요청 정보
     * @return 생성된 프로젝트 정보 (ID, 이름, 네임스페이스 ID 포함)
     * @throws BusinessException 프로젝트 생성 실패 시
     * 
     * @implNote 프로젝트 이름은 시스템 내에서 고유해야 합니다.
     */
    public CreatedClientRead createProject(CreateClient request) {
        log.info("SKTAI 프로젝트 생성 요청 - name: {}", request.getProject().getName());
        
        try {
            CreatedClientRead response = sktaiProjectClient.createProject(request);
            log.info("SKTAI 프로젝트 생성 성공 - projectId: {}, name: {}, namespaceId: {}",
                    response.getId(), response.getName(), response.getNamespaceId());
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 프로젝트 생성 실패 (BusinessException) - name: {}, message: {}", 
                    request.getProject().getName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 프로젝트 생성 실패 (예상치 못한 오류) - name: {}", 
                    request.getProject().getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, 
                    "프로젝트 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 프로젝트 목록 조회
     * 
     * <p>사용자가 접근 가능한 프로젝트 목록을 페이징하여 조회합니다.
     * 검색, 필터링, 정렬 기능을 지원합니다.</p>
     * 
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지당 항목 수
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색어
     * @return 페이징된 프로젝트 목록
     * @throws BusinessException 목록 조회 실패 시
     */
    public ClientsRead getProjects(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("SKTAI 프로젝트 목록 조회 - page: {}, size: {}, sort: {}, filter: {}, search: {}",
                page, size, sort, filter, search);
        
        try {
            ClientsRead response = sktaiProjectClient.getProjects(page, size, sort, filter, search);
            log.debug("SKTAI 프로젝트 목록 조회 성공 - 총 {}개 프로젝트",
                    response.getPayload().getPagination().getTotal());
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 프로젝트 목록 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 프로젝트 목록 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "프로젝트 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 프로젝트 상세 조회
     * 
     * <p>특정 프로젝트의 상세 정보를 조회합니다.
     * 프로젝트 기본 정보와 연결된 네임스페이스 정보를 포함합니다.</p>
     * 
     * @param projectId 프로젝트 고유 식별자
     * @return 프로젝트 상세 정보
     * @throws BusinessException 프로젝트 조회 실패 시
     */
    public ClientRead getProject(String projectId) {
        log.debug("SKTAI 프로젝트 상세 조회 - projectId: {}", projectId);
        
        try {
            ClientRead response = sktaiProjectClient.getProject(projectId);
            log.debug("SKTAI 프로젝트 상세 조회 성공 - projectId: {}, name: {}",
                    projectId, response.getProject().getName());
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 프로젝트 상세 조회 실패 (BusinessException) - projectId: {}, message: {}", 
                    projectId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 프로젝트 상세 조회 실패 (예상치 못한 오류) - projectId: {}", projectId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "프로젝트 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 프로젝트 정보 수정
     * 
     * <p>기존 프로젝트의 정보를 수정합니다.
     * 프로젝트명, 설명, 네임스페이스 설정 등을 변경할 수 있습니다.</p>
     * 
     * @param projectId 수정할 프로젝트 ID
     * @param request 프로젝트 수정 요청 정보
     * @return 수정된 프로젝트 정보
     * @throws BusinessException 프로젝트 수정 실패 시
     */
    public CreatedClientRead updateProject(String projectId, UpdateClient request) {
        log.info("SKTAI 프로젝트 수정 요청 - projectId: {}, name: {}", projectId, request.getProject().getName());
        
        try {
            CreatedClientRead response = sktaiProjectClient.updateProject(projectId, request);
            log.info("SKTAI 프로젝트 수정 성공 - projectId: {}, name: {}",
                    projectId, response.getName());
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 프로젝트 수정 실패 (BusinessException) - projectId: {}, message: {}", 
                    projectId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 프로젝트 수정 실패 (예상치 못한 오류) - projectId: {}", projectId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "프로젝트 수정에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 프로젝트 삭제
     * 
     * <p>프로젝트를 완전히 삭제합니다.
     * 관련된 리소스와 데이터도 함께 삭제되므로 주의가 필요합니다.</p>
     * 
     * @param projectId 삭제할 프로젝트 ID
     * @throws BusinessException 프로젝트 삭제 실패 시
     */
    public void deleteProject(String projectId) {
        log.warn("SKTAI 프로젝트 삭제 요청 - projectId: {}", projectId);
        
        try {
            sktaiProjectClient.deleteProject(projectId);
            log.warn("SKTAI 프로젝트 삭제 완료 - projectId: {}", projectId);
        } catch (BusinessException e) {
            log.error("SKTAI 프로젝트 삭제 실패 (BusinessException) - projectId: {}, message: {}", 
                    projectId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 프로젝트 삭제 실패 (예상치 못한 오류) - projectId: {}", projectId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "프로젝트 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 프로젝트 역할 생성
     * 
     * <p>프로젝트에 새로운 역할을 생성합니다.
     * 생성된 역할은 사용자에게 할당하여 권한을 제어할 수 있습니다.</p>
     * 
     * @param projectId 역할을 생성할 프로젝트 ID
     * @param request 역할 생성 요청 정보
     * @return 생성된 역할 정보
     * @throws BusinessException 역할 생성 실패 시
     */
    public RoleBase createProjectRole(String projectId, CreateProjectRole request) {
        log.info("SKTAI 프로젝트 역할 생성 요청 - projectId: {}, roleName: {}",
                projectId, request.getName());
        
        try {
            RoleBase response = sktaiProjectClient.createProjectRole(projectId, request);
            log.info("SKTAI 프로젝트 역할 생성 성공 - projectId: {}, roleId: {}, roleName: {}",
                    projectId, response.getId(), response.getName());
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 프로젝트 역할 생성 실패 (BusinessException) - projectId: {}, roleName: {}, message: {}",
                    projectId, request.getName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 프로젝트 역할 생성 실패 (예상치 못한 오류) - projectId: {}, roleName: {}",
                    projectId, request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "프로젝트 역할 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 프로젝트 역할 목록 조회
     * 
     * <p>특정 프로젝트의 모든 역할 목록을 조회합니다.
     * 기본 시스템 역할과 사용자 정의 역할을 모두 포함합니다.</p>
     * 
     * @param projectId 역할을 조회할 프로젝트 ID
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색어
     * @return 프로젝트 역할 목록
     * @throws BusinessException 역할 목록 조회 실패 시
     */
    public Object getProjectRoles(String projectId, Integer page, Integer size, String sort, String filter, String search) {
        log.debug("SKTAI 프로젝트 역할 목록 조회 - projectId: {}", projectId);
        
        try {
            Object response = sktaiProjectClient.getProjectRoles(projectId, page, size, sort, filter, search);
            log.debug("SKTAI 프로젝트 역할 목록 조회 성공 - projectId: {}", projectId);
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 프로젝트 역할 목록 조회 실패 (BusinessException) - projectId: {}, message: {}", 
                    projectId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 프로젝트 역할 목록 조회 실패 (예상치 못한 오류) - projectId: {}", projectId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "프로젝트 역할 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 프로젝트 역할 삭제
     * 
     * <p>프로젝트의 특정 역할을 삭제합니다.
     * 해당 역할을 가진 사용자들은 권한을 잃게 됩니다.</p>
     * 
     * @param projectId 프로젝트 ID
     * @param roleId 삭제할 역할 ID
     * @throws BusinessException 역할 삭제 실패 시
     */
    public void deleteProjectRole(String projectId, String roleId) {
        log.warn("SKTAI 프로젝트 역할 삭제 요청 - projectId: {}, roleId: {}", projectId, roleId);
        
        try {
            sktaiProjectClient.deleteProjectRole(projectId, roleId);
            log.warn("SKTAI 프로젝트 역할 삭제 완료 - projectId: {}, roleId: {}", projectId, roleId);
        } catch (BusinessException e) {
            log.error("SKTAI 프로젝트 역할 삭제 실패 (BusinessException) - projectId: {}, roleId: {}, message: {}", 
                    projectId, roleId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 프로젝트 역할 삭제 실패 (예상치 못한 오류) - projectId: {}, roleId: {}", 
                    projectId, roleId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "프로젝트 역할 삭제에 실패했습니다: " + e.getMessage());
        }
    }
}
