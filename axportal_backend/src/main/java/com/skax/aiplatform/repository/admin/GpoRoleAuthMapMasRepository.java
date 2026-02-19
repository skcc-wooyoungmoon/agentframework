package com.skax.aiplatform.repository.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.skax.aiplatform.entity.mapping.GpoRoleAuthMapMas;
import com.skax.aiplatform.entity.mapping.RoleAuthorityId2;
import com.skax.aiplatform.entity.mapping.RoleAuthorityStatus;

/**
 * 역할-권한 매핑 리포지토리
 */
public interface GpoRoleAuthMapMasRepository extends JpaRepository<GpoRoleAuthMapMas, RoleAuthorityId2> {

    List<GpoRoleAuthMapMas> findByRoleRoleSeq(Long roleSeq);

    @Query("""
            SELECT gra
            FROM GpoRoleAuthMapMas gra
            WHERE gra.role.roleSeq = :roleSeq
              AND gra.authority.authorityId != 'A000001'
              AND gra.statusNm = :status
              AND (
                :keyword IS NULL OR :keyword = '' OR (
                    (:filterType = 'authorityNm' OR :filterType IS NULL) AND gra.authority.authorityNm LIKE CONCAT('%', :keyword, '%')
                    OR (:filterType = 'dtlCtnt' AND gra.authority.dtlCtnt LIKE CONCAT('%', :keyword, '%'))
                )
              )
              AND (:twoDepthMenu IS NULL OR :twoDepthMenu = '' OR gra.authority.twoDphMenu = :twoDepthMenu)
              ORDER BY gra.authority.authorityId
            """)
    Page<GpoRoleAuthMapMas> searchRoleAuthorities(
            Long roleSeq,
            RoleAuthorityStatus status,
            Pageable pageable,
            String filterType,
            String keyword,
            String twoDepthMenu
    );

    List<GpoRoleAuthMapMas> findAllByAuthority_AuthorityIdAndStatusNm(String authorityAuthorityId,
            RoleAuthorityStatus statusNm);

    /**
     * 프로젝트에 속한 모든 역할의 권한 매핑을 삭제합니다.
     *
     * @param prjSeq 프로젝트 시퀀스
     */
    @Modifying
    @Query("DELETE FROM GpoRoleAuthMapMas gra WHERE gra.role.prjSeq = :prjSeq")
    void deleteByRolePrjSeq(Long prjSeq);

}
