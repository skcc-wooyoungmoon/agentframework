package com.skax.aiplatform.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.skax.aiplatform.entity.NoticeFile;

import java.util.List;

@Repository
public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {
    
    /**
     * 공지사항 ID로 첨부파일 목록 조회
     */
    List<NoticeFile> findByNoticeIdAndUseYnOrderByUploadDateDesc(Long noticeId, Integer useYn);
    
    /**
     * 공지사항 ID로 모든 첨부파일 조회 (사용여부 무관)
     */
    List<NoticeFile> findByNoticeIdOrderByUploadDateDesc(Long noticeId);
    
    /**
     * 공지사항 ID로 첨부파일 개수 조회
     */
    @Query("SELECT COUNT(f) FROM NoticeFile f WHERE f.noticeId = :noticeId AND f.useYn = 1")
    long countByNoticeId(@Param("noticeId") Long noticeId);
}

