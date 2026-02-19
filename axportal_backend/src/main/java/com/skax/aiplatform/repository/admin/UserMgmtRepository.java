package com.skax.aiplatform.repository.admin;

import com.skax.aiplatform.entity.mapping.ProjectUserRole;
import com.skax.aiplatform.entity.project.Project;
import com.skax.aiplatform.entity.project.ProjectStatus;
import com.skax.aiplatform.entity.role.Role;
import com.skax.aiplatform.entity.role.RoleStatus;
import com.skax.aiplatform.entity.user.DormantStatus;
import com.skax.aiplatform.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * 사용자 관리 리포지토리
 *
 * @author 장지원
 * @version 2.0.0
 * @since 2025-10-02
 */
public interface UserMgmtRepository extends JpaRepository<User, String> {

    /**
     * 검색 조건을 통한 사용자 전체 조회
     *
     * @param pageable   페이지 정보
     * @param filterType 검색 유형
     * @param keyword    검색어
     * @param retrJkwYn  인사 상태 (1:재직, 0:퇴직)
     * @param dmcStatus  계정 상태 (ACTIVE: 활성, DORMANT: 휴면)
     * @return 사용자 리스트(페이징)
     */
    @Query(value = """
             SELECT u
             FROM User u
             WHERE 1=1
             AND (
               :keyword IS NULL OR :keyword = '' OR (
                 (:filterType = 'jkwNm' OR :filterType IS NULL) AND u.jkwNm LIKE CONCAT('%', :keyword, '%')
                 OR (:filterType = 'deptNm' AND u.deptNm LIKE CONCAT('%', :keyword, '%'))
                 OR (:filterType = 'jkgpNm' AND u.jkgpNm LIKE CONCAT('%', :keyword, '%'))
                 OR (:filterType = 'memberId' AND u.memberId LIKE CONCAT('%', :keyword, '%'))
               )
             )
             AND (:retrJkwYn IS NULL OR u.retrJkwYn = :retrJkwYn)
             AND (:dmcStatus IS NULL OR u.dmcStatus = :dmcStatus)
             ORDER BY u.lstLoginAt DESC
            """)
    Page<User> findUsersBySearch(
            Pageable pageable,
            String filterType,
            String keyword,
            String retrJkwYn,
            DormantStatus dmcStatus
    );

    /**
     * 사용자가 참여한 프로젝트 조회
     *
     * @param memberId   사용자 ID
     * @param pageable   페이지 정보
     * @param filterType 검색 유형 (prjNm, dtlCtnt)
     * @param keyword    검색어
     * @param statusNm   프로젝트 상태 (ONGOING, COMPLETED)
     * @return 사용자가 참여한 프로젝트 목록(페이징)
     */
    @Query(value = """
                SELECT p
                FROM Project p
                INNER JOIN ProjectUserRole pul ON p.prjSeq = pul.project.prjSeq
                WHERE pul.user.memberId = :memberId
                AND p.statusNm = :statusNm
                AND (
                    :keyword IS NULL OR :keyword = '' OR (
                        (:filterType = 'prjNm' OR :filterType IS NULL) AND p.prjNm LIKE CONCAT('%', :keyword, '%')
                        OR (:filterType = 'dtlCtnt' AND p.dtlCtnt LIKE CONCAT('%', :keyword, '%'))
                    )
                )
                ORDER BY p.lstUpdatedAt DESC
            """)
    Page<Project> findUserProjectsBySearch(
            String memberId,
            Pageable pageable,
            String filterType,
            String keyword,
            ProjectStatus statusNm
    );

    /**
     * 사용자가 참여한 프로젝트의 상세 정보 및 역할 조회
     *
     * @param userId    사용자 ID
     * @param projectId 프로젝트 ID
     * @return 사용자가 해당 프로젝트에서 가진 상세 정보 및 역할
     */
    @Query(value = """
              SELECT pur
              FROM ProjectUserRole pur
              WHERE pur.user.memberId = :userId
              AND pur.project.uuid = :projectId
            """)
    Optional<ProjectUserRole> findUserProjectDetail(String userId, String projectId);


    /**
     * 사용자가 참여한 프로젝트에서 할당 가능한 역할 목록 조회 (프로젝트 단위 역할만, 포털 역할 제외)
     *
     * @param projectId  프로젝트 ID
     * @param pageable   페이징 정보
     * @param filterType 검색 타입
     * @param keyword    검색어
     * @param statusNm   역할 상태
     * @return 사용자가 참여한 프로젝트에서 할당 가능한 역할 목록
     */
    @Query("""
            SELECT DISTINCT r
            FROM Role r, Project p
            WHERE p.uuid = :projectId
            AND (
                (p.prjSeq = -999 AND r.prjSeq = p.prjSeq)
                OR
                (p.prjSeq <> -999 AND (r.prjSeq IS NULL OR r.prjSeq = p.prjSeq))
            )
            AND (
                :keyword IS NULL OR :keyword = '' OR (
                    (:filterType = 'roleNm' OR :filterType IS NULL) AND r.roleNm LIKE CONCAT('%', :keyword, '%')
                    OR (:filterType = 'dtlCtnt' AND r.dtlCtnt LIKE CONCAT('%', :keyword, '%'))
                )
            )
            AND r.statusNm = :statusNm
            ORDER BY r.fstCreatedAt DESC, r.roleSeq DESC
            """)
    Page<Role> findAssignableProjectRoles(
            String projectId,
            Pageable pageable,
            String filterType,
            String keyword,
            RoleStatus statusNm
    );

}
