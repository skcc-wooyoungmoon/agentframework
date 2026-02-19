package com.skax.aiplatform.service.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.skax.aiplatform.dto.admin.request.UserUsageMgmtReq;
import com.skax.aiplatform.dto.admin.response.ProjectRes;
import com.skax.aiplatform.dto.admin.response.UserUsageMgmtRes;
import com.skax.aiplatform.dto.admin.response.UserUsageMgmtStatsRes;

import java.util.List;
import java.util.Map;


public interface UserUsageMgmtService {
 
    
    /**
     * 사용자 사용량 관리 생성
     * 
     * @param userUsageMgmt 사용자 사용량 관리 정보
     * @return 생성된 사용자 사용량 관리
     */
    UserUsageMgmtRes createUserUsageMgmt(UserUsageMgmtReq userUsageMgmt);
    
    /**
     * 비동기 사용자 사용량 관리 생성
     * 
     * @param userUsageMgmt 사용자 사용량 관리 정보
     */
    void createUserUsageMgmtAsync(UserUsageMgmtReq userUsageMgmt);
    
    /**
     * 가장 가까운 로그인 로그에 사용자 정보 업데이트
     * 
     * @param currentLogId 현재 로그 ID (users/me 호출 로그)
     * @param userName 사용자명
     * @param userInfo 사용자 정보
     */
    void updateNearestLoginLogWithUserInfo(Long currentLogId, String userName, String userInfo);
    


    /**
     * 사용자 사용량 관리 수정
     * 
     * @param userUsageMgmt 사용자 사용량 관리 정보
     * @return 수정된 사용자 사용량 관리
     */
    UserUsageMgmtRes updateUserUsageMgmt(UserUsageMgmtReq userUsageMgmt);


    /**
     * 사용자 사용량 관리 조회
     * 
     * @param dateType 날짜 타입
     * @param projectName 프로젝트명
     * @param result 결과
     * @param searchType 검색 타입
     * @param searchValue 검색 값
     * @param fromDate 시작 날짜
     * @param toDate 종료 날짜
     * @param pageable 페이징 정보
     * @return 사용자 사용량 관리 목록
     */
    Page<UserUsageMgmtRes> getUserUsageMgmts(String dateType, String projectName, String result, 
                                            String searchType, String searchValue, String fromDate, 
                                            String toDate, Pageable pageable);


    /**
     * 사용자 사용량 관리 상세 조회
     * 
     * @param id 사용자 사용량 관리 ID
     * @return 사용자 사용량 관리 상세 정보
     */
    UserUsageMgmtRes getUserUsageMgmtById(Long id);



    /**
     * 커스텀 데이터로 사용자 사용량 관리 Excel 내보내기
     * 
     * @param headers 헤더 정보 목록
     * @param data 데이터 목록
     * @return Excel 형태의 바이트 배열
     */
    byte[] exportUserUsageMgmtsWithCustomData(List<Map<String, Object>> headers, List<Map<String, Object>> data);

    /**
     * 사용자 사용량 관리 통계 조회
     * 
     * @param searchType 조회 조건 (month, week, day)
     * @param selectedDate 선택된 날짜 (month: yyyy-MM, week/day: yyyy-MM-dd)
     * @param projectType 선택된 프로젝트
     * @return 사용자 사용량 관리 통계 정보
     */
    UserUsageMgmtStatsRes getUserUsageMgmtStats(String searchType, String selectedDate, String projectType);

    /**
     * 전체 프로젝트 목록 조회
     * 
     * @return 프로젝트 목록
     * @author sonmunwoo
     * @since 2025-10-21
     */
    List<ProjectRes> getAllProjects();

    List<ProjectRes> getProjectsByName(String projectName);

    /**
     * 30일 전 사용자 사용량 관리 데이터 삭제
     * 
     * @return 삭제된 레코드 수
     */
    long deleteOldUserUsageMgmtData();

}
