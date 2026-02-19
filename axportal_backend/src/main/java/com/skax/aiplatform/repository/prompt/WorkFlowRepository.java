package com.skax.aiplatform.repository.prompt;

import com.skax.aiplatform.entity.prompt.WorkFlow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkFlowRepository extends JpaRepository<WorkFlow, String> {

    /**
     * workflowId로 최신 버전 조회
     */
    @Query("""
            SELECT w
            FROM WorkFlow w
            WHERE w.workflowId = :workflowId
            ORDER BY w.versionNo DESC
            LIMIT 1
            """)
    Optional<WorkFlow> findTopByWorkflowIdOrderByVersionNoDesc(@Param("workflowId") String workflowId);

    /**
     * workflowId 존재 여부 확인
     */
    @Query("""
            select count(w) > 0
            from WorkFlow w
            where w.workflowId = :workflowId
            """)
    boolean existsByWorkflowId(@Param("workflowId") String workflowId);

    /**
     * 조건부 워크플로우 조회 (페이징) - 각 workflowId의 최신 버전만 조회
     *
     * @param pageable   페이지 정보
     * @param projectSeq 프로젝트 SEQ
     * @param search     워크플로우명 검색어
     * @param tag        태그 검색어
     * @return 워크플로우   페이지
     */
    @Query(value = """
            SELECT w
              FROM WorkFlow w
             WHERE w.isActive = 1
               AND (:projectSeq IS NULL OR (w.projectSeq = :projectSeq OR w.projectSeq = -999))
               AND (:search IS NULL OR :search = '' OR w.workflowName LIKE CONCAT('%', :search, '%'))
               AND (:tag IS NULL   OR :tag = ''   OR w.tag          LIKE CONCAT('%', :tag, '%'))
               AND w.versionNo = (
                     SELECT MAX(w2.versionNo)
                       FROM WorkFlow w2
                      WHERE w2.workflowName = w.workflowName
                        AND w2.isActive = 1
               )
             ORDER BY w.fstCreatedAt DESC
            """, countQuery = """
            SELECT COUNT(DISTINCT w.workflowName)
              FROM WorkFlow w
             WHERE w.isActive = 1
               AND (:projectSeq IS NULL OR (w.projectSeq = :projectSeq OR w.projectSeq = -999))
               AND (:search IS NULL OR :search = '' OR w.workflowName LIKE CONCAT('%', :search, '%'))
               AND (:tag IS NULL   OR :tag = ''   OR w.tag          LIKE CONCAT('%', :tag, '%'))
               AND w.versionNo = (
                     SELECT MAX(w2.versionNo)
                       FROM WorkFlow w2
                      WHERE w2.workflowName = w.workflowName
                        AND w2.isActive = 1
               )
            """)
    Page<WorkFlow> findWorkFlowsBySearch(
            @Param("projectSeq") long projectSeq,
            @Param("search") String search,
            @Param("tag") String tag,
            Pageable pageable
    );

    /**
     * 프로젝트의 모든 태그 조회
     */
    @Query("""
                select distinct w.tag
                  from WorkFlow w
                 where w.tag is not null
                   and (w.projectSeq = :projectSeq or w.projectSeq = -999)
            """)
    List<String> findDistinctTags(@Param("projectSeq") long projectSeq);

    /**
     * workflowId에 해당하는 모든 버전 조회
     * workflowId로 해당 workflow의 이름을 찾고, 그 이름의 모든 버전을 조회
     */
    @Query("""
            select w
            from WorkFlow w
            where w.workflowName = (
                select w2.workflowName
                from WorkFlow w2
                where w2.workflowId = :workflowId
            )
            order by w.versionNo desc
            """)
    List<WorkFlow> findVersionsByWorkflowId(@Param("workflowId") String workflowId);

    /**
     * workflowId의 최신 버전 조회
     */
    @Query("""
            SELECT w
            FROM WorkFlow w
            WHERE w.workflowName = (
                SELECT w2.workflowName
                FROM WorkFlow w2
                WHERE w2.workflowId = :workflowId
            )
            AND w.versionNo = (
                SELECT MAX(w3.versionNo)
                FROM WorkFlow w3
                WHERE w3.workflowName = (
                    SELECT w4.workflowName
                    FROM WorkFlow w4
                    WHERE w4.workflowId = :workflowId
                )
            )
            """)
    WorkFlow findLatestByWorkflowId(@Param("workflowId") String workflowId);

    /**
     * workflowId의 특정 버전 조회
     */
    @Query("""
            SELECT w
            FROM WorkFlow w
            WHERE w.workflowName = (
                SELECT w2.workflowName
                FROM WorkFlow w2
                WHERE w2.workflowId = :workflowId
            )
            AND w.versionNo = :versionNo
            """)
    WorkFlow findByWorkflowIdAndVersion(@Param("workflowId") String workflowId,
                                        @Param("versionNo") Integer versionNo);

    /**
     * workflowId의 최대 버전 번호 조회
     */
    @Query("""
            select coalesce(max(w.versionNo), 0) 
            from WorkFlow w 
            where w.workflowName = (
                select w2.workflowName
                from WorkFlow w2
                where w2.workflowId = :workflowId
            )
            """)
    int findMaxVersionNo(@Param("workflowId") String workflowId);

    /**
     * workflowId의 모든 버전의 이름 변경
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update WorkFlow w 
            set w.workflowName = :newName 
            where w.workflowName = (
                select w2.workflowName
                from WorkFlow w2
                where w2.workflowId = :workflowId
            )
            """)
    void renameAllByWorkflowId(@Param("workflowId") String workflowId,
                               @Param("newName") String newName);

    /**
     * workflowId의 모든 버전 삭제
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from WorkFlow w 
            where w.workflowName = (
                select w2.workflowName
                from WorkFlow w2
                where w2.workflowId = :workflowId
            )
            """)
    int deleteAllByWorkflowId(@Param("workflowId") String workflowId);

    /**
     * workflowId의 모든 버전의 projectSeq를 -999로 업데이트 (공개 정책)
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update WorkFlow w
            set w.projectSeq = -999,
                w.updatedBy = :memberId,
                w.lstUpdatedAt = CURRENT_TIMESTAMP,
                w.projectScope = concat(:memberId, ' | ', :prjNm)
            where w.workflowId = :workflowId
            """)
    int updateProjectSeqToPublic(@Param("workflowId") String workflowId, String memberId, String prjNm);
}
