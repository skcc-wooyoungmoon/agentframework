package com.skax.aiplatform.repository.admin;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.skax.aiplatform.entity.auth.GpoAuthorityMas;

public interface GpoAuthorityMasRepository extends JpaRepository<GpoAuthorityMas, String> {

    /**
     * 메뉴 권한 목록 조회
     */
    @Query(value = """
            SELECT a FROM GpoAuthorityMas a
            WHERE (a.authorityNm IS NULL OR a.authorityNm = '')
                AND a.hrnkAuthorityId IS NOT NULL
                AND (:oneDphMenu IS NULL OR :oneDphMenu = '' OR a.oneDphMenu = :oneDphMenu)
                AND (:twoDphMenu IS NULL OR :twoDphMenu = '' OR a.twoDphMenu = :twoDphMenu)
                AND (:keyword IS NULL OR :keyword = ''
                    OR (:filterType = 'oneDphMenu' AND a.oneDphMenu LIKE CONCAT('%', :keyword, '%'))
                    OR (:filterType = 'twoDphMenu' AND a.twoDphMenu LIKE CONCAT('%', :keyword, '%'))
                    OR (:filterType = 'all' AND (a.oneDphMenu LIKE CONCAT('%', :keyword, '%') OR a.twoDphMenu LIKE CONCAT('%', :keyword, '%')))
                )
            ORDER BY a.authorityId
            """)
    Page<GpoAuthorityMas> findMenuPermits(
            Pageable pageable,
            String oneDphMenu,
            String twoDphMenu,
            String filterType,
            String keyword
    );

    /**
     * 상세 권한 목록 조회
     */
    @Query(value = """
                SELECT a FROM GpoAuthorityMas a
                WHERE a.hrnkAuthorityId IN :authorityIds
                    AND (:twoDphMenu IS NULL OR :twoDphMenu = '' OR a.twoDphMenu = :twoDphMenu)
                    AND (:keyword IS NULL OR :keyword = ''
                    OR (:filterType = 'authorityNm' AND a.authorityNm LIKE CONCAT('%', :keyword, '%'))
                    OR (:filterType = 'dtlCtnt' AND a.dtlCtnt LIKE CONCAT('%', :keyword, '%'))
                    OR (:filterType = 'all' AND (a.authorityNm LIKE CONCAT('%', :keyword, '%')
                                             OR a.dtlCtnt LIKE CONCAT('%', :keyword, '%')))
                )
                ORDER BY a.authorityId
            """)
    Page<GpoAuthorityMas> findAllByHrnkAuthorityIdIn(
            Pageable pageable,
            List<String> authorityIds,
            String twoDphMenu,
            String filterType,
            String keyword
    );

    @Query("""
            SELECT ram.id.authorityId
            FROM GpoRoleAuthMapMas ram
            WHERE ram.role.roleSeq = :roleSeq
            """)
    List<String> findAuthKeysByRoleSeq(Long roleSeq);

    /**
     * 역할에 직접 매핑된 권한의 상위 권한 ID 목록 조회
     * - role_seq에 직접 매핑된 권한의 hrnk_authority_id 반환
     */
    @Query("""
            SELECT DISTINCT a.hrnkAuthorityId
            FROM GpoRoleAuthMapMas ram
            JOIN ram.authority a
            WHERE ram.role.roleSeq = :roleSeq
            AND a.hrnkAuthorityId IS NOT NULL
            """)
    List<String> findDirectMenuAuthKeysByRoleSeq(Long roleSeq);

    /**
     * 역할에 간접 매핑된 권한의 상위 권한 ID 목록 조회
     * - 직접 매핑된 권한을 authority_id로 갖는 권한의 hrnk_authority_id 반환
     */
    @Query("""
            SELECT DISTINCT a.hrnkAuthorityId
            FROM GpoAuthorityMas a
            WHERE a.authorityId IN (
                SELECT ram.authority.hrnkAuthorityId
                FROM GpoRoleAuthMapMas ram
                WHERE ram.role.roleSeq = :roleSeq
                AND ram.authority.hrnkAuthorityId IS NOT NULL
            )
            AND a.hrnkAuthorityId IS NOT NULL
            """)
    List<String> findIndirectMenuAuthKeysByRoleSeq(Long roleSeq);

    /**
     * 역할에 매핑된 권한의 상위 권한 ID 목록 조회 (직접 매핑 + 간접 매핑)
     * - 직접 매핑: role_seq에 직접 매핑된 권한의 hrnk_authority_id
     * - 간접 매핑: 직접 매핑된 권한을 authority_id로 갖는 권한의 hrnk_authority_id
     * <p>
     * JPQL은 UNION을 지원하지 않으므로 두 개의 JPQL 쿼리 결과를 조합
     */
    default List<String> findMenuAuthKeysByRoleSeq(Long roleSeq) {
        List<String> directKeys = findDirectMenuAuthKeysByRoleSeq(roleSeq);
        List<String> indirectKeys = findIndirectMenuAuthKeysByRoleSeq(roleSeq);

        return Stream.concat(directKeys.stream(), indirectKeys.stream())
                .distinct()
                .toList();
    }

}
