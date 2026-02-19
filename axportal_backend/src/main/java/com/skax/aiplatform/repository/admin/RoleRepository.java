package com.skax.aiplatform.repository.admin;

import com.skax.aiplatform.entity.role.Role;
import com.skax.aiplatform.entity.role.RoleStatus;
import com.skax.aiplatform.entity.role.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * 역할(Role) 리포지토리
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByUuid(String uuid);

    List<Role> findByUuidIn(List<String> uuids);

    /**
     * 프로젝트 시퀀스와 역할명으로 역할을 조회합니다.
     *
     * @param prjSeq 프로젝트 시퀀스
     * @param roleNm 역할명
     * @return List<Role> 조회된 역할 목록
     */
    @Query(value = """
                SELECT r FROM Role r
                WHERE r.roleNm = :roleNm
                AND (r.prjSeq = :prjSeq OR r.prjSeq IS NULL)
            """)
    Optional<Role> findByPrjSeqAndRoleNm(Long prjSeq, String roleNm);

    /**
     * 프로젝트 시퀀스 번호로 해당 프로젝트의 모든 역할 삭제 (프로젝트 종료시 사용)
     *
     * @param prjSeq 삭제할 역할들의 프로젝트 시퀀스
     */
    @Modifying
    @Query("DELETE FROM Role r WHERE r.prjSeq = :prjSeq")
    void deleteByPrjSeq(Long prjSeq);

    /**
     * 포탈 전역 역할만 조회
     */
    @Query(value = """
            SELECT r FROM Role r
            WHERE r.rightScopCtnt = 'PORTAL'
            AND (:roleType IS NULL OR r.roleType = :roleType)
            AND (
               :keyword IS NULL OR :keyword = '' OR (
                 (:filterType = 'roleNm' OR :filterType IS NULL) AND r.roleNm LIKE CONCAT('%', :keyword, '%')
                 OR (:filterType = 'dtlCtnt' AND r.dtlCtnt LIKE CONCAT('%', :keyword, '%'))
               )
            )
            ORDER BY r.fstCreatedAt DESC, r.roleSeq DESC
            """)
    Page<Role> findPortalRoles(Pageable pageable, RoleType roleType, String filterType, String keyword);

    /**
     * 포탈 전역 역할을 제외하고, 특정 프로젝트의 역할만 조회
     */
    @Query(value = """
            SELECT r FROM Role r
            WHERE r.rightScopCtnt <> 'PORTAL'
              AND (r.prjSeq = :prjSeq or r.prjSeq is null)
              AND r.statusNm = :statusNm
            AND (:roleType IS NULL OR r.roleType = :roleType)
            AND (
               :keyword IS NULL OR :keyword = '' OR (
                 (:filterType = 'roleNm' OR :filterType IS NULL) AND r.roleNm LIKE CONCAT('%', :keyword, '%')
                 OR (:filterType = 'dtlCtnt' AND r.dtlCtnt LIKE CONCAT('%', :keyword, '%'))
               )
            )
            ORDER BY CASE WHEN r.roleType = 'DEFAULT' THEN 0 ELSE 1 END ASC,
              r.fstCreatedAt DESC
            """)
    Page<Role> findProjectRolesExcludePortal(Long prjSeq, RoleStatus statusNm, Pageable pageable, RoleType roleType, String filterType, String keyword);

    Optional<Role> findByRoleSeq(Long roleSeq);

}
