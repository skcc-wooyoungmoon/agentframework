package com.skax.aiplatform.repository.home;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.skax.aiplatform.entity.ide.GpoIdeImageMas;
import com.skax.aiplatform.entity.ide.ImageType;

/**
 * IDE 이미지 원장 Repository (v2)
 */
public interface GpoIdeImageMasRepository extends JpaRepository<GpoIdeImageMas, String> {

    /**
     * 검색어와 이미지 구분으로 이미지 목록 조회
     * 검색어는 이미지명과 설명을 동시에 검색
     *
     * @param imgG     이미지 구분 (ALL이거나 null이면 전체 조회)
     * @param keyword  검색어 (이미지명, 설명)
     * @param pageable 페이지 정보
     * @return 검색 결과
     */
    @Query("""
                SELECT i FROM GpoIdeImageMas i
                WHERE (
                    :imgG IS NULL
                    OR i.imgG = :imgG
                )
                AND (
                    :keyword IS NULL
                    OR :keyword = ''
                    OR LOWER(i.imgNm) LIKE CONCAT('%', :keyword, '%')
                    OR LOWER(i.dtlCtnt) LIKE CONCAT('%', :keyword, '%')
                )
                ORDER BY i.lstUpdatedAt DESC
            """)
    Page<GpoIdeImageMas> findImagesBySearch(ImageType imgG, String keyword, Pageable pageable);

    boolean existsByImgNm(String imgNm);

    /**
     * 특정 UUID를 제외하고 이미지명이 중복되는지 확인
     *
     * @param imgNm 확인할 이미지명
     * @param uuid 제외할 UUID
     * @return 중복 여부
     */
    boolean existsByImgNmAndUuidNot(String imgNm, String uuid);

    /**
     * 다중 UUID로 이미지 삭제 (단일 쿼리 사용)
     *
     * @param uuids 삭제할 UUID 목록
     */
    @Modifying
    @Query("DELETE FROM GpoIdeImageMas i WHERE i.uuid IN :uuids")
    void deleteAllByIdIn(List<String> uuids);

}
