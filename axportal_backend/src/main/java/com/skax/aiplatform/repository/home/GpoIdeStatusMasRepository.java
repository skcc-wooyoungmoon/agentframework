package com.skax.aiplatform.repository.home;

import com.skax.aiplatform.dto.home.response.IdeStatusDto;
import com.skax.aiplatform.dto.home.response.IdeStatusRes;
import com.skax.aiplatform.entity.ide.GpoIdeStatusMas;
import com.skax.aiplatform.entity.ide.ImageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * IDE 상태 Repository
 */
public interface GpoIdeStatusMasRepository extends JpaRepository<GpoIdeStatusMas, String> {

    /**
     * 사용자 ID로 조회
     */
    List<GpoIdeStatusMas> findByMemberId(String memberId);

    /**
     * 사용자 ID와 이미지 구분으로 IDE 개수 조회
     */
    @Query("SELECT COUNT(s) FROM GpoIdeStatusMas s JOIN GpoIdeImageMas i ON s.imgUuid = i.uuid WHERE s.memberId = " +
            ":memberId AND i.imgG = :imgG")
    long countByMemberIdAndImgG(@Param("memberId") String memberId, @Param("imgG") ImageType imgG);

    /**
     * 전체 IDE 상태 조회 (사용자명, 이미지명, 도구명 포함)
     */
    @Query(value = """
            SELECT new com.skax.aiplatform.dto.home.response.IdeStatusDto(
                u.memberId,
                u.jkwNm,
                i.imgG,
                i.imgNm,
                s.uuid,
                s.dwAccountId,
                s.cpuUseHaldngV,
                s.memUseHaldngV,
                s.expAt
            )
            FROM GpoIdeStatusMas s
                INNER JOIN GpoUsersMas u ON s.memberId = u.memberId
                INNER JOIN GpoIdeImageMas i ON s.imgUuid = i.uuid
            ORDER BY s.lstUpdatedAt DESC
            """)
    List<IdeStatusDto> findAllIdeStatus();

    /**
     * 사용자 ID와 검색어로 IDE 상태 목록 조회 (페이징)
     * 검색어는 도구명(imgG), 이미지명(imgNm), DW계정(dwAccountId)을 동시에 검색
     *
     * @param memberId 사용자 ID
     * @param keyword  검색어 (도구명, 이미지명, DW계정 통합 검색)
     * @param pageable 페이지 정보
     * @return 검색 결과 (페이징)
     */
    @Query("""
            SELECT new com.skax.aiplatform.dto.home.response.IdeStatusRes(
                s.uuid,
                s.svrUrlNm,
                s.dwAccountId,
                s.expAt,
                i.imgG,
                i.imgNm
            )
            FROM GpoIdeStatusMas s
                INNER JOIN GpoIdeImageMas i ON s.imgUuid = i.uuid
            WHERE s.memberId = :memberId
            AND (
                :keyword IS NULL
                OR :keyword = ''
                OR LOWER(CAST(i.imgG AS string)) LIKE CONCAT('%', :keyword, '%')
                OR LOWER(i.imgNm) LIKE CONCAT('%', :keyword, '%')
                OR LOWER(s.dwAccountId) LIKE CONCAT('%', :keyword, '%')
            )
            ORDER BY s.expAt
            """)
    Page<IdeStatusRes> findIdeStatusBySearch(String memberId, String keyword, Pageable pageable);

    /**
     * 만료된 IDE 조회
     * expAt 값이 지정되어 있고, 현재 시간을 기준으로 만료된 IDE를 조회
     *
     * @param now 현재 시간
     * @return 만료된 IDE 목록
     */
    @Query("select u from GpoIdeStatusMas u where u.expAt is not null and u.expAt < :now")
    List<GpoIdeStatusMas> findExpired(@Param("now") LocalDateTime now);

}
