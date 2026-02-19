package com.skax.aiplatform.repository.admin;

import java.util.List;
import java.util.Optional;

import com.skax.aiplatform.entity.mapping.ProjectUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 프로젝트 역할 구성원 조회 리포지토리
 */
public interface ProjectUserRoleRepository extends JpaRepository<ProjectUserRole, Long> {

    @Modifying
    @Query("DELETE FROM ProjectUserRole pur WHERE pur.project.prjSeq = :prjSeq")
    void deleteByPrjSeq(Long prjSeq);

    boolean existsByRoleRoleSeq(Long roleSeq);

    Optional<ProjectUserRole> findByProjectPrjSeqAndUserMemberId(Long prjSeq, String memberId);

    List<ProjectUserRole> findByRoleRoleSeq(Long roleSeq);

    List<ProjectUserRole> findByRoleRoleSeqAndUserMemberIdIn(Long roleSeq, List<String> memberIds);

    List<ProjectUserRole> findByProjectPrjSeq(Long prjSeq);

    List<ProjectUserRole> findByProjectPrjSeqAndUserMemberIdIn(Long prjSeq, List<String> memberIds);

    /**
     * 사용자의 활성 프로젝트 UUID와 역할명 조회
     *
     * <p>여러 활성 프로젝트가 있을 경우 첫 번째 결과만 반환합니다.
     * Tibero 호환을 위해 ORDER BY와 함께 사용합니다.</p>
     *
     * @param memberId 사용자 ID
     * @return [0]: 프로젝트 UUID, [1]: 역할명
     */
    @Query("SELECT p.uuid, r.roleNm FROM ProjectUserRole pur " +
            "JOIN pur.project p " +
            "JOIN pur.role r " +
            "WHERE pur.user.memberId = :memberId " +
            "AND pur.statusNm = 'ACTIVE' " +
            "ORDER BY pur.project.prjSeq ASC")
    List<Object[]> findActiveProjectAndRoleByMemberId(String memberId);

    /**
     * 사용자의 활성 프로젝트 시퀀스 조회
     *
     * @param memberId 사용자 ID
     * @return 활성 프로젝트 시퀀스 (첫 번째)
     */
    @Query("SELECT p.prjSeq FROM ProjectUserRole pur " +
            "JOIN pur.project p " +
            "WHERE pur.user.memberId = :memberId " +
            "AND pur.statusNm = 'ACTIVE'")
    Optional<Long> findActivePrjSeqByMemberId(String memberId);

    /**
     * 사용자의 모든 프로젝트-역할 매핑 조회
     *
     * <p>GPO_PRJUSERROLE_MAP_MAS 테이블에서 memberId에 해당하는 레코드를 조회하며,
     * GPO_PROJECTS_MAS 테이블과 INNER JOIN하여 유효한 프로젝트가 존재하는 레코드만 반환합니다.
     * 이를 통해 프로젝트가 삭제되었으나 매핑 테이블에 남아있는 고아 레코드로 인한 오류를 방지합니다.</p>
     *
     * @param memberId 사용자 ID
     * @return 프로젝트-역할 매핑 목록 (유효한 프로젝트가 존재하는 항목만)
     */
    @Query("SELECT pur FROM ProjectUserRole pur " +
            "JOIN pur.project p " +
            "WHERE pur.user.memberId = :memberId")
    List<ProjectUserRole> findByUserMemberId(String memberId);

}
