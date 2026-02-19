package com.skax.aiplatform.client.sktai.knowledge.service;

import com.skax.aiplatform.client.sktai.knowledge.SktaiToolsClient;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.ToolCreate;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.ToolUpdate;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.ToolsResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.ToolResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.ArgResponse;
import java.util.List;

/**
 * SKTAI Knowledge Tool 서비스
 * 
 * <p>SKTAI Knowledge API의 Data Ingestion Tool 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * Feign Client를 래핑하여 예외 처리, 로깅, 비즈니스 규칙을 적용합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>Tool 관리</strong>: 문서 처리 도구의 생성, 조회, 수정, 삭제</li>
 *   <li><strong>연결 관리</strong>: 외부 서비스와의 연결 설정 및 상태 확인</li>
 *   <li><strong>에러 처리</strong>: 외부 API 호출 실패에 대한 일관된 예외 처리</li>
 *   <li><strong>로깅</strong>: Tool 작업에 대한 상세한 로깅 및 추적</li>
 * </ul>
 * 
 * <h3>지원하는 Tool 유형:</h3>
 * <ul>
 *   <li><strong>AzureDocumentIntelligence</strong>: Azure AI 기반 문서 분석</li>
 *   <li><strong>NaverOCR</strong>: 네이버 클로바 OCR 서비스</li>
 *   <li><strong>Docling</strong>: IBM 문서 처리 엔진</li>
 *   <li><strong>SynapsoftDA</strong>: Synapsoft 문서 분석</li>
 *   <li><strong>SKTDocumentInsight</strong>: SKT AI 문서 인사이트</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // Tool 생성
 * ToolCreate createRequest = ToolCreate.builder()
 *     .projectId("project-123")
 *     .name("Azure OCR Tool")
 *     .toolType("AzureDocumentIntelligence")
 *     .connectionInfo(connectionData)
 *     .build();
 * ToolResponse tool = sktaiToolsService.createTool(createRequest);
 * 
 * // Tool 목록 조회
 * MultiResponse tools = sktaiToolsService.getTools(1, 10, null, null, "Azure");
 * 
 * // Tool 수정
 * ToolUpdate updateRequest = ToolUpdate.builder()
 *     .name("Updated Azure Tool")
 *     .build();
 * sktaiToolsService.updateTool(tool.getId(), updateRequest);
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiToolsService {

    private final SktaiToolsClient sktaiToolsClient;

    /**
     * Data Ingestion Tool 목록 조회
     * 
     * <p>프로젝트에 등록된 데이터 수집 도구 목록을 페이징 형태로 조회합니다.
     * 검색어, 필터, 정렬 조건을 적용하여 필요한 Tool을 효율적으로 찾을 수 있습니다.</p>
     * 
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지당 항목 수
     * @param sort 정렬 조건 (예: "name,asc", "created_at,desc")
     * @param filter 필터 조건
     * @param search 검색어 (Tool 이름 및 연결정보에서 검색)
     * @return 페이징된 Tool 목록
     */
    public ToolsResponse getTools(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("Tool 목록 조회 요청 - page: {}, size: {}, sort: {}, filter: {}, search: {}", 
                  page, size, sort, filter, search);
        
        try {
            ToolsResponse response = sktaiToolsClient.getTools(page, size, sort, filter, search);
            log.debug("Tool 목록 조회 성공 - 조회된 항목 수: {}", 
                      response.getData() != null ? "응답 데이터 존재" : "응답 데이터 없음");
            return response;
        } catch (BusinessException e) {
            log.error("Tool 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}", page, size, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Tool 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Tool 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 신규 Tool 등록
     * 
     * <p>프로젝트에 새로운 데이터 수집 도구를 등록합니다.
     * Tool 유형에 맞는 연결 정보를 검증하고, 외부 서비스와의 연결을 확인한 후 등록합니다.</p>
     * 
     * @param request Tool 생성 요청 정보
     * @return 생성된 Tool 정보 (고유 ID 포함)
     * 
     * @throws BusinessException Tool 생성 실패 또는 연결 검증 실패 시
     */
    public ToolResponse createTool(ToolCreate request) {
        log.debug("Tool 생성 요청 - projectId: {}, name: {}, type: {}", 
                  request.getProjectId(), request.getName(), request.getToolType());
        
        try {
            ToolResponse response = sktaiToolsClient.createTool(request);
            log.info("Tool 생성 성공 - toolId: {}, name: {}, type: {}", 
                     response.getId(), response.getName(), response.getType());
            return response;
        } catch (BusinessException e) {
            log.error("Tool 생성 실패 (BusinessException) - projectId: {}, name: {}, message: {}", 
                      request.getProjectId(), request.getName(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Tool 생성 실패 (예상치 못한 오류) - projectId: {}, name: {}, type: {}", 
                      request.getProjectId(), request.getName(), request.getToolType(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "Tool 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Tool 상세 조회
     * 
     * <p>특정 Tool의 상세 정보를 조회합니다.
     * Tool ID를 통해 설정, 연결 상태, 메타데이터를 확인할 수 있습니다.</p>
     * 
     * @param toolId Tool 고유 식별자 (UUID)
     * @return Tool 상세 정보
     * 
     * @throws BusinessException Tool 조회 실패 또는 Tool이 존재하지 않는 경우
     */
    public ToolResponse getTool(String toolId) {
        log.debug("Tool 상세 조회 요청 - toolId: {}", toolId);
        
        try {
            ToolResponse response = sktaiToolsClient.getTool(toolId);
            log.debug("Tool 상세 조회 성공 - toolId: {}", toolId);
            return response;
        } catch (BusinessException e) {
            log.error("Tool 상세 조회 실패 (BusinessException) - toolId: {}, message: {}", toolId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Tool 상세 조회 실패 (예상치 못한 오류) - toolId: {}", toolId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "Tool 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Tool 정보 수정
     * 
     * <p>기존 Tool의 설정 정보를 수정합니다.
     * 이름, 연결 정보 등을 변경할 수 있으며, 연결 정보 변경 시 새로운 연결을 검증합니다.</p>
     * 
     * @param toolId Tool 고유 식별자 (UUID)
     * @param request Tool 수정 요청 정보
     * @return 수정 처리 결과
     * 
     * @throws BusinessException Tool 수정 실패 또는 연결 검증 실패 시
     */
    public Object updateTool(String toolId, ToolUpdate request) {
        log.debug("Tool 수정 요청 - toolId: {}, name: {}", toolId, request.getName());
        
        try {
            Object response = sktaiToolsClient.updateTool(toolId, request);
            log.info("Tool 수정 성공 - toolId: {}, name: {}", toolId, request.getName());
            return response;
        } catch (BusinessException e) {
            log.error("Tool 수정 실패 (BusinessException) - toolId: {}, name: {}, message: {}", toolId, request.getName(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Tool 수정 실패 (예상치 못한 오류) - toolId: {}, name: {}", toolId, request.getName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "Tool 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Tool 삭제
     * 
     * <p>특정 Tool을 시스템에서 삭제합니다.
     * 삭제 전에 연관된 Repository나 진행 중인 작업이 있는지 확인하고, 안전하게 제거합니다.</p>
     * 
     * @param toolId Tool 고유 식별자 (UUID)
     * 
     * @throws BusinessException Tool 삭제 실패 또는 참조 중인 리소스가 있는 경우
     */
    public void deleteTool(String toolId) {
        log.debug("Tool 삭제 요청 - toolId: {}", toolId);
        
        try {
            sktaiToolsClient.deleteTool(toolId);
            log.info("Tool 삭제 성공 - toolId: {}", toolId);
        } catch (BusinessException e) {
            log.error("Tool 삭제 실패 (BusinessException) - toolId: {}, message: {}", toolId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Tool 삭제 실패 (예상치 못한 오류) - toolId: {}", toolId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "Tool 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Tool 연결 정보 조회
     * 
     * <p> Tool의 연결 정보 정보를 조회합니다.</p>
     * 
     * @return Tool 상세 정보
     * 
     * @apiNote 반환되는 정보에는 연결 설정과 상태 정보가 포함됩니다.
     */
    public List<ArgResponse> getConnectionArgs() {
        try {
            return sktaiToolsClient.getConnectionArgs();
        } catch (BusinessException e) {
            log.error("Tool 연결 정보 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Tool 연결 정보 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Tool 연결 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }
}
