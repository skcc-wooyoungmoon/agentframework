package com.skax.aiplatform.repository.admin;

import com.skax.aiplatform.entity.project.Project;
import com.skax.aiplatform.entity.project.ProjectStatus;
import com.skax.aiplatform.entity.user.DormantStatus;
import com.skax.aiplatform.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProjectMgmtRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByUuid(String prjId);

    Optional<Project> findByPrjNm(String prjNm);

    Optional<Project> findByPrjSeq(Long prjSeq);

    /**
     * 사용자 ID와 프로젝트명으로 프로젝트 조회
     *
     * @param memberId 사용자 ID
     * @param prjNm    프로젝트명
     * @return 프로젝트
     */
    @Query(value = """
             SELECT p FROM Project p
             INNER JOIN ProjectUserRole pur ON pur.project.prjSeq = p.prjSeq
             WHERE pur.user.memberId = :memberId
             AND p.prjNm = :prjNm
            """)
    Optional<Project> findByMemberIdAndPrjNm(String memberId, String prjNm);

    @Modifying
    @Query("DELETE FROM Project p WHERE p.prjSeq = :prjSeq")
    void deleteByPrjSeq(Long prjSeq);

    /**
     * 검색 조건을 통한 프로젝트 전체 조회
     *
     * @param pageable   페이지 정보
     * @param filterType 검색 유형
     * @param keyword    검색어
     * @return 프로젝트 목록(페이징)
     */
    @Query(value = """
             SELECT p FROM Project p
             WHERE p.statusNm = :statusNm
             AND (
               :keyword IS NULL OR :keyword = '' OR (
                 (:filterType = 'prjNm' OR :filterType IS NULL) AND p.prjNm LIKE CONCAT('%', :keyword, '%')
                 OR (:filterType = 'dtlCtnt' AND p.dtlCtnt LIKE CONCAT('%', :keyword, '%'))
               )
             )
             ORDER BY CASE WHEN p.prjSeq = -999 THEN 0 ELSE 1 END ASC, p.fstCreatedAt DESC
            """)
    Page<Project> findProjectsBySearch(Pageable pageable, String filterType, String keyword, ProjectStatus statusNm);

    /**
     * 프로젝트 관리자 역할을 가진 사용자의 프로젝트 목록 조회
     *
     * @param pageable              페이지 정보
     * @param filterType            검색 유형
     * @param keyword               검색어
     * @param statusNm              프로젝트 상태£
     * @param memberId              사용자 ID
     * @param projectManagerRoleSeq 프로젝트 관리자 역할 SEQ
     * @return 프로젝트 목록(페이징)
     */
    @Query(value = """
             SELECT p FROM Project p
             INNER JOIN ProjectUserRole pur ON pur.project.prjSeq = p.prjSeq
             WHERE p.statusNm = :statusNm
             AND pur.user.memberId = :memberId
             AND pur.role.roleSeq = :projectManagerRoleSeq
             AND (
               :keyword IS NULL OR :keyword = '' OR (
                 (:filterType = 'prjNm' OR :filterType IS NULL) AND p.prjNm LIKE CONCAT('%', :keyword, '%')
                 OR (:filterType = 'dtlCtnt' AND p.dtlCtnt LIKE CONCAT('%', :keyword, '%'))
               )
             )
             ORDER BY p.fstCreatedAt DESC
            """)
    Page<Project> findProjectsByProjectManagerRole(
            Pageable pageable,
            String filterType,
            String keyword,
            ProjectStatus statusNm,
            String memberId,
            Long projectManagerRoleSeq
    );

    /**
     * 프로젝트 역할에 배정된 구성원 목록 조회
     *
     * @param projectSeq 프로젝트 시퀀스
     * @param roleSeq    역할 시퀀스
     * @param pageable   페이지 정보
     * @param statusNm   활성화 상태
     * @param filterType 검색 유형
     * @param keyword    검색어
     * @param dmcStatus  계정 상태
     * @param retrJkwYn  퇴직 여부
     */
    @Query("""
            SELECT pur.user
            FROM ProjectUserRole pur
            WHERE pur.project.prjSeq = :projectSeq
              AND pur.role.roleSeq = :roleSeq
              AND (:dmcStatus IS NULL OR pur.user.dmcStatus = :dmcStatus)
              AND (:retrJkwYn IS NULL OR pur.user.retrJkwYn = :retrJkwYn)
              AND (
                    :keyword IS NULL OR :keyword = '' OR (
                        ((:filterType = 'jkwNm' OR :filterType IS NULL) AND pur.user.jkwNm LIKE CONCAT('%', :keyword, '%'))
                        OR (:filterType = 'deptNm' AND pur.user.deptNm LIKE CONCAT('%', :keyword, '%'))
                    )
              )
            ORDER BY pur.user.lstLoginAt DESC
            """)
    Page<User> getProjectRoleUsers(
            Long projectSeq,
            Long roleSeq,
            Pageable pageable,
            String filterType,
            String keyword,
            DormantStatus dmcStatus,
            String retrJkwYn
    );

    /**
     * 프로젝트 구성원 목록 조회
     */
    @Query("""
            SELECT pur.user
            FROM ProjectUserRole pur
            WHERE pur.project.prjSeq = :projectSeq
              AND (:dmcStatus IS NULL OR pur.user.dmcStatus = :dmcStatus)
              AND (:retrJkwYn IS NULL OR pur.user.retrJkwYn = :retrJkwYn)
              AND (
               :keyword IS NULL OR :keyword = '' OR (
                 (:filterType = 'jkwNm' OR :filterType IS NULL) AND pur.user.jkwNm LIKE CONCAT('%', :keyword, '%')
                 OR (:filterType = 'deptNm' AND pur.user.deptNm LIKE CONCAT('%', :keyword, '%'))
               )
             )
            ORDER BY pur.user.lstLoginAt DESC
            """)
    Page<User> findProjectUsers(
            Long projectSeq,
            Pageable pageable,
            String filterType,
            String keyword,
            DormantStatus dmcStatus,
            String retrJkwYn
    );

    /**
     * 프로젝트에 참여하지 않은 구성원 목록 조회
     */
    @Query("""
            SELECT u
            FROM User u
            WHERE u.dmcStatus = :dmcStatus
              AND u.retrJkwYn = :retrJkwYn
              AND NOT EXISTS (
                  SELECT 1
                  FROM ProjectUserRole pur
                  WHERE pur.project.prjSeq = :projectSeq
                    AND pur.user.memberId = u.memberId
              )
              AND (
                 :keyword IS NULL OR :keyword = '' OR (
                     (:filterType = 'jkwNm' OR :filterType IS NULL) AND u.jkwNm LIKE CONCAT('%', :keyword, '%')
                     OR (:filterType = 'deptNm' AND u.deptNm LIKE CONCAT('%', :keyword, '%'))
               )
             )
            ORDER BY u.lstLoginAt DESC
            """)
    Page<User> getProjectInvitableUsers(
            Long projectSeq,
            Pageable pageable,
            DormantStatus dmcStatus,
            Integer retrJkwYn,
            String filterType,
            String keyword
    );

}
