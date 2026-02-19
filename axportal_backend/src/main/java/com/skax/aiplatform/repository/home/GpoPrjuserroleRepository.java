package com.skax.aiplatform.repository.home;

import com.skax.aiplatform.entity.mapping.ProjectUserRole;
import com.skax.aiplatform.entity.mapping.ProjectUserRoleStatus;
import com.skax.aiplatform.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GpoPrjuserroleRepository extends JpaRepository<ProjectUserRole, Long> {

    /**
     * 프로젝트내 특정 역할 조회
     *
     * @return 역할 정보
     */
    @Query("""
            SELECT r
            FROM ProjectUserRole r
            WHERE r.user.memberId = :memberId
            AND r.project.prjSeq= :prjSeq
            """)
    Optional<ProjectUserRole> findByMemberIdAndPrjSeq(String memberId, Long prjSeq);

    /**
     * 멤버 ID와 프로젝트 시퀀스로 프로젝트 사용자 역할을 조회 (Role 정보 포함 - LazyInitializationException 방지)
     * 
     * @param memberId 멤버 ID
     * @param prjSeq 프로젝트 시퀀스
     * @return 프로젝트 사용자 역할 정보 (Role과 User 정보 포함)
     */
    @Query("""
            SELECT r
            FROM ProjectUserRole r
            LEFT JOIN FETCH r.role
            LEFT JOIN FETCH r.user
            WHERE r.user.memberId = :memberId
            AND r.project.prjSeq = :prjSeq
            """)
    Optional<ProjectUserRole> findByMemberIdAndPrjSeqWithRole(@Param("memberId") String memberId, 
                                                              @Param("prjSeq") Long prjSeq);

    /**
     * 특정 역할(ROLE_SEQ)에 매핑된 사용자 MEMBER_ID 목록 조회 (중복 제거)
     */
    @Query("""
            SELECT DISTINCT r.user.memberId
            FROM ProjectUserRole r
            WHERE r.role.roleSeq = :roleSeq
            """)
    List<String> findMemberIdsByRoleSeq(Long roleSeq);

    @Query("""
            SELECT DISTINCT r.user.memberId
            FROM ProjectUserRole r
            WHERE r.role.roleSeq = :roleSeq and r.project.uuid = :projectUuid
            """)
    List<String> findMemberIdsByRoleSeqAndProjectUuid(Long roleSeq, String projectUuid);

    /**
     * 특정 프로젝트(PRJ_SEQ)와 역할(ROLE_SEQ)에 매핑된 사용자 MEMBER_ID 목록 조회
     */
    @Query("""
            SELECT DISTINCT r.user.memberId
            FROM ProjectUserRole r
            WHERE r.project.prjSeq = :prjSeq
            AND r.role.roleSeq = :roleSeq
            """)
    List<String> findMemberIdsByPrjSeqAndRoleSeq(@Param("prjSeq") Long prjSeq, @Param("roleSeq") Long roleSeq);

    void deleteByUser_MemberId(String userMemberId);

    /**
     * 관리자 기본 매핑을 직접 추가 (네이티브)
     */
    @Modifying
    @Query(value = "INSERT INTO GPO_PRJUSERROLE_MAP_MAS (SEQ_NO, STATUS_NM, MEMBER_ID, PRJ_SEQ, ROLE_SEQ, FST_CREATED_AT, LST_UPDATED_AT, CREATED_BY, UPDATED_BY) " +
            "VALUES (:seqNo, :statusNm, :memberId, :prjSeq, :roleSeq, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, :createdBy, :updatedBy)", nativeQuery = true)
    void insertMapping(@Param("seqNo") Long seqNo,
                       @Param("statusNm") String statusNm,
                       @Param("memberId") String memberId,
                       @Param("prjSeq") Long prjSeq,
                       @Param("roleSeq") Long roleSeq,
                       @Param("createdBy") String createdBy,
                       @Param("updatedBy") String updatedBy);

    @Query("""
            SELECT r
            FROM ProjectUserRole r
            WHERE r.project.uuid = :projectUuid
            """)
    List<ProjectUserRole> findByProjectUuid(@Param("projectUuid") String projectUuid);

    @Query("""
            SELECT r
            FROM ProjectUserRole r
            WHERE r.project.uuid = :projectUuid
              and r.user.memberId = :memberId
            """)
    List<ProjectUserRole> findByProjectUuidAnmMemberId(@Param("projectUuid") String projectUuid,
                                                       @Param("memberId") String memberId);


    @Query("""
            SELECT r
            FROM ProjectUserRole r
            WHERE r.user.memberId = :memberId
              AND r.statusNm = :statusNm
            """)
    ProjectUserRole findByMemberIdAndStatusNm(String memberId, ProjectUserRoleStatus statusNm);

    ProjectUserRoleStatus user(User user);
}
