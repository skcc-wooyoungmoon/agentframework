package com.skax.aiplatform.repository.admin;

import com.skax.aiplatform.entity.NoticeManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface NoticeRepository extends JpaRepository<NoticeManagement, Long> {


    // @Query("SELECT n FROM NoticeMgmt n WHERE n.id = :id AND n.isDeleted = false")
    // Optional<NoticeMgmt> findByIdAndNotDeleted(@Param("id") Long id);

    // @Query("SELECT n FROM NoticeMgmt n WHERE n.isDeleted = false")
    // Page<NoticeMgmt> findAllNotDeleted(Pageable pageable);

    @Query("""
            SELECT n FROM NoticeManagement n
             WHERE 1=1
               AND n.expFrom <= CURRENT_TIMESTAMP AND n.expTo > CURRENT_TIMESTAMP
               AND (
                     (:useUpdated = true AND n.updateAt >= :fromDt AND n.updateAt < :toDtPlus)
                  OR (:useCreated = true AND n.createAt  >= :fromDt AND n.createAt  < :toDtPlus)
               )
               AND (:ntype IS NULL OR :ntype = '전체' OR n.type = :ntype)
               AND (
                     :searchValue IS NULL OR :searchValue = '' OR
                     (
                       (:searchType IS NULL AND (LOWER(n.title) LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%'))
                                              OR  LOWER(n.msg)   LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%'))
                                              OR  LOWER(n.firstDetail)   LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%'))
                                              OR  LOWER(n.secondDetail)   LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%'))
                                              OR  LOWER(n.thirdDetail)   LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%'))
                                              OR  LOWER(n.fourthDetail)   LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%'))))
                       OR (:searchType = 'title'   AND LOWER(n.title) LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%')))
                       OR (:searchType = 'content' AND LOWER(n.msg)   LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%')))
                       OR (:searchType = 'content' AND LOWER(n.firstDetail)   LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%')))
                       OR (:searchType = 'content' AND LOWER(n.secondDetail)   LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%')))
                       OR (:searchType = 'content' AND LOWER(n.thirdDetail)   LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%')))
                       OR (:searchType = 'content' AND LOWER(n.fourthDetail)   LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%')))
                     )
                   )
               AND (n.useYn IS NULL OR n.useYn = 1)
            """)
    Page<NoticeManagement> findNoticesByFilters(
            @Param("useUpdated") boolean useUpdated,
            @Param("useCreated") boolean useCreated,
            @Param("searchType") String searchType,
            @Param("searchValue") String searchValue,
            @Param("fromDt") LocalDateTime fromDt,
            @Param("toDtPlus") LocalDateTime toDtPlus,
            @Param("ntype") String noticeType,
            Pageable pageable
    );

    @Query("""
            SELECT n FROM NoticeManagement n
             WHERE 1=1
               AND (:dateType IS NULL OR :dateType = '' OR
                    (:dateType = '수정일시' AND n.updateAt >= :fromDt AND n.updateAt < :toDtPlus))
               AND (:ntype IS NULL OR :ntype = '' OR n.type = :ntype)
               AND (:status IS NULL OR :status = '' OR
                    (:status = '게시' AND n.useYn = 1 AND n.expFrom <= CURRENT_TIMESTAMP AND n.expTo >= CURRENT_TIMESTAMP) OR
                    (:status = '임시저장' AND n.useYn = 0) OR
                    (:status = '만료' AND n.useYn = 1 AND (n.expFrom > CURRENT_TIMESTAMP OR n.expTo < CURRENT_TIMESTAMP)))
               AND (:searchValue IS NULL OR :searchValue = '' OR
                    ((:searchType IS NULL OR :searchType = '') AND (LOWER(n.title) LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%')) OR LOWER(n.msg) LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%')))) OR
                    (:searchType = '제목' AND LOWER(n.title) LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%'))) OR
                    (:searchType = '내용' AND LOWER(n.msg) LIKE LOWER(CONCAT('%', LOWER(:searchValue), '%'))))
            """)
    Page<NoticeManagement> findNoticesByAdminFilters(
            @Param("dateType") String dateType,
            @Param("searchType") String searchType,
            @Param("searchValue") String searchValue,
            @Param("fromDt") LocalDateTime fromDt,
            @Param("toDtPlus") LocalDateTime toDtPlus,
            @Param("ntype") String noticeType,
            @Param("status") String status,
            Pageable pageable
    );
}

