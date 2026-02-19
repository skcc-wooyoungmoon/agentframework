package com.skax.aiplatform.repository.model;

import com.skax.aiplatform.entity.model.GpoUseGnynModelMas;
import com.skax.aiplatform.enums.ModelGardenStatus;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ModelGardenRepository extends JpaRepository<GpoUseGnynModelMas, Long> {

    GpoUseGnynModelMas findBySeqNo(Long seqNo);
    
    // 모델 가든 관리 용
    GpoUseGnynModelMas findBySeqNoAndDelYn(Long seqNo, Integer delYn);

    // 모델 반입 절차에 활용
    GpoUseGnynModelMas findByModelNmAndDelYn(String modelNm, Integer delYn);

    // 모델 카탈로그 용
    GpoUseGnynModelMas findByCallUrlAndDelYn(String callUrl, Integer delYn);

//     /**
//      * 모델 가든 적재 목록 조회
//      *
//      * @param pageable   페이지 정보
//      * @param dplyTyp    배포 유형 [필수 값] // self_hosting, serverless
//      * @param search    검색어
//      * @param status   모델 상태
//      */
//     @Query("""
//             SELECT g
//             FROM GpoUseGnynModelMas g
//             WHERE g.delYn = 'N' 
//                 AND (:dplyTyp IS NULL OR :dplyTyp = '' OR g.dplyTyp = :dplyTyp)
//                 AND (:search IS NULL OR :search = '' OR (
//                         (g.modelNm LIKE CONCAT('%', :search, '%'))
//                         OR (g.dtlCtn LIKE CONCAT('%', :search, '%'))
//                         )
//                 )
//                 AND (:status IS NULL OR g.statusNm = :status)       
//                 AND (:type IS NULL OR :type = '' OR g.modelTyp = :type)
//             ORDER BY g.fstCreatedAt DESC
//             """)
//     Page<GpoUseGnynModelMas> getGardenInfo(
//             Pageable pageable,
//             String dplyTyp,
//             String search,
//             ModelGardenStatus status,
//             String type
//     );
    
//     /**
//      * value로 그룹화된 상태 조회
//      */
//     @Query("""
//             SELECT g
//             FROM GpoUseGnynModelMas g
//             WHERE g.delYn = 'N' 
//                 AND (:dplyTyp IS NULL OR :dplyTyp = '' OR g.dplyTyp = :dplyTyp)
//                 AND (:search IS NULL OR :search = '' OR (
//                         (g.modelNm LIKE CONCAT('%', :search, '%'))
//                         OR (g.dtlCtn LIKE CONCAT('%', :search, '%'))
//                         )
//                 )
//          AND (g.statusNm IN :statusList)       
//             ORDER BY g.fstCreatedAt DESC
//             """)
//     Page<GpoUseGnynModelMas> getGardenInfoByValue(
//             Pageable pageable,
//             String dplyTyp,
//             String search,
//             List<ModelGardenStatus> statusList
//     );

    /**
     * 모델과 도입 정보를 함께 조회 (JPA 관계 매핑 활용)
     */
    @Query("""
            SELECT g
            FROM GpoUseGnynModelMas g
                LEFT JOIN FETCH g.doipInfo d
            WHERE g.delYn = 0 
                AND (:dplyTyp IS NULL OR :dplyTyp = '' OR g.dplyTyp = :dplyTyp)
                AND (:search IS NULL OR :search = '' OR (
                        (g.modelNm LIKE CONCAT('%', :search, '%'))
                        OR (g.dtlCtn LIKE CONCAT('%', :search, '%'))
                        )
                )
                AND (:status IS NULL OR d.statusNm = :status)
                AND (:type IS NULL OR :type = '' OR g.modelTyp = :type)
            ORDER BY g.fstCreatedAt DESC
            """)
    Page<GpoUseGnynModelMas> getGardenInfoWithDoip(
            Pageable pageable,
            String dplyTyp,
            String search,
            ModelGardenStatus status,
            String type
    );


    
    /**
     * value로 그룹화된 상태 조회
     */
    @Query("""
            SELECT g
            FROM GpoUseGnynModelMas g
                LEFT JOIN FETCH g.doipInfo d
            WHERE g.delYn = 0 
                AND (:dplyTyp IS NULL OR :dplyTyp = '' OR g.dplyTyp = :dplyTyp)
                AND (:search IS NULL OR :search = '' OR (
                        (g.modelNm LIKE CONCAT('%', :search, '%'))
                        OR (g.dtlCtn LIKE CONCAT('%', :search, '%'))
                        )
                )
         AND (d.statusNm IN :statusList)       
            ORDER BY g.fstCreatedAt DESC
            """)
    Page<GpoUseGnynModelMas> getGardenInfoWithDoipByStatusList(
            Pageable pageable,
            String dplyTyp,
            String search,
            List<ModelGardenStatus> statusList
    );

    /**
     * 도입 요청이 있는 모델만 조회 (JPA 관계 매핑 활용)
     */
//     @Query("""
//             SELECT g
//             FROM GpoUseGnynModelMas g
//             INNER JOIN FETCH g.doipInfo d
//             WHERE g.delYn = 'N' 
//                 AND (:dplyTyp IS NULL OR :dplyTyp = '' OR g.dplyTyp = :dplyTyp)
//                 AND (:search IS NULL OR :search = '' OR (
//                         (g.modelNm LIKE CONCAT('%', :search, '%'))
//                         OR (g.dtlCtn LIKE CONCAT('%', :search, '%'))
//                         )
//                 )
//                 AND (:status IS NULL OR :status = '' OR g.statusNm = :status)
//                 AND (:doipStatus IS NULL OR :doipStatus = '' OR d.statusNm = :doipStatus)
//             ORDER BY g.fstCreatedAt DESC
//             """)
//     Page<GpoUseGnynModelMas> getModelsWithDoipRequest(
//             Pageable pageable,
//             String dplyTyp,
//             String search,
//             ModelGardenStatus status,
//             ModelGardenStatus doipStatus
//     );

    /**
     * 특정 모델의 상세 정보와 도입 정보 조회
     */
    @Query("""
            SELECT g
            FROM GpoUseGnynModelMas g
            LEFT JOIN FETCH g.doipInfo d
            WHERE g.seqNo = :seqNo AND g.delYn = 0
            """)
    GpoUseGnynModelMas findModelWithDoipInfo(@Param("seqNo") Long seqNo);

//     /**
//      * 도입 요청이 없는 모델만 조회
//      */
//     @Query("""
//             SELECT g
//             FROM GpoUseGnynModelMas g
//             LEFT JOIN g.doipInfo d
//             WHERE g.delYn = 'N' 
//                 AND d IS NULL
//                 AND (:dplyTyp IS NULL OR :dplyTyp = '' OR g.dplyTyp = :dplyTyp)
//                 AND (:search IS NULL OR :search = '' OR (
//                         (g.modelNm LIKE CONCAT('%', :search, '%'))
//                         OR (g.dtlCtn LIKE CONCAT('%', :search, '%'))
//                         )
//                 )
//                 AND (:status IS NULL OR :status = '' OR g.statusNm = :status)
//             ORDER BY g.fstCreatedAt DESC
//             """)
//     Page<GpoUseGnynModelMas> getModelsWithoutDoipRequest(
//             Pageable pageable,
//             String dplyTyp,
//             String search,
//             ModelGardenStatus status
//     );

    
}
