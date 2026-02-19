package com.skax.aiplatform.service.admin;


import java.util.Map;

/**
 * 자원 관리 서비스 인터페이스
 * 
 * @author SonMunWoo
 * @since 2025-09-27
 * @version 1.0
 */
public interface ResrcMgmtService {
    

    /**
     * 포탈 IDE 자원 현황 조회
     * 2025-12-29 smw 개선
     * @return 포탈 IDE 자원 현황 정보
     */
    Map<String, Object> getPortalIdeResources(String searchType, String searchValue);

    /**
     * 포탈 자원 현황 조회
     * 
     * @return 포탈 자원 현황 정보
     */
    Map<String, Object> getPortalResources();
    
    /**
     * GPU 노드별 자원 현황 조회
     * 
     * @return GPU 노드별 자원 현황 정보
     */
    Map<String, Object> getGpuNodeResources();
    
    /**
     * 솔루션 자원 현황 조회
     * 
     * @return 솔루션별 자원 현황 정보
     */
    Map<String, Object> getSolutionResources();
    
    /**
     * 포탈 에이전트 파드별 자원 현황 조회
     * 
     * @return 에이전트 파드별 자원 현황 정보
     */
    Map<String, Object> getPortalAgentPodResources();
    

    /**
     * GPU 노드별 자원 현황 상세 조회
     * 
     * @param nodeName 노드 이름
     * @param fromDate 시작 날짜
     * @param toDate 종료 날짜
     * @param durationParam 기간 파라미터 (초 단위 + "s")
     * @param fromTimestamp 시작 타임스탬프
     * @param toTimestamp 종료 타임스탬프
     * @param workloadName 워크로드 이름 (선택사항)
     * @return GPU 노드별 상세 자원 현황 정보
     */
    Map<String, Object> getGpuNodeDetailResources(String nodeName, String fromDate, String toDate, String durationParam, long fromTimestamp, long toTimestamp, String workloadName);
    
    /**
     * 솔루션별 자원 현황 상세 조회
     * 
     * @param nameSpace 네임스페이스
     * @param podName Pod 이름 (선택)
     * @param fromDate 시작 날짜
     * @param toDate 종료 날짜
     * @param durationParam 기간 파라미터 (초 단위 + "s")
     * @return 솔루션별 상세 자원 현황 정보
     */
    Map<String, Object> getSolutionDetailResources(String nameSpace, String podName, String fromDate, String toDate, String durationParam);
    
    /**
     * 솔루션 정보 조회
     * 
     * @param nameSpace 네임스페이스
     * @return 솔루션 정보
     */
    Map<String, Object> getSolutionInfo(String nameSpace);
    
}
