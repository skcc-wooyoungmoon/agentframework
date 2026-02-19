package com.skax.aiplatform.service.admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.dto.admin.request.NoticeManagementCreateReq;
import com.skax.aiplatform.dto.admin.request.NoticeManagementSearchReq;
import com.skax.aiplatform.dto.admin.request.NoticeManagementUpdateReq;
import com.skax.aiplatform.dto.admin.response.NoticeManagementRes;
import com.skax.aiplatform.entity.NoticeFile;


public interface NoticeManagementService {



    /**
     * 공지사항 생성
     * 
     * @param req 공지사항 정보
     * @return 생성된 공지사항
     */
    NoticeManagementRes createNotice(NoticeManagementCreateReq req);

    /**
     * 파일과 함께 공지사항 생성
     * 
     * @param req 공지사항 정보
     * @param files 업로드할 파일들
     * @return 생성된 공지사항
     */
    NoticeManagementRes createNoticeWithFiles(NoticeManagementCreateReq req, MultipartFile[] files);

    /**
     * 공지사항 목록 조회 (페이징)
     * 
     * @param pageable 페이징 정보
     * @param searchReq 검색 조건
     * @return 공지사항 목록 페이지
     */
    Page<NoticeManagementRes> getNotices(Pageable pageable, NoticeManagementSearchReq searchReq);

    /**
     * 공지사항 상세 조회
     * 
     * @param notiId 공지사항 ID
     * @return 공지사항 상세 정보
     */
    NoticeManagementRes getNotice(Long notiId);

    /**
     * 공지사항 수정
     * 
     * @param notiId      공지사항 ID
     * @param noticeManagement 수정할 공지사항 정보
     * @return 수정된 공지사항
     */
    NoticeManagementRes updateNotice(Long id, NoticeManagementUpdateReq req);

    /**
     * 공지사항 삭제
     * 
     * @param notiId 공지사항 ID
     */
    void deleteNotice(Long notiId);

    /**
     * 공지사항 첨부파일 조회
     * 
     * @param noticeId 공지사항 ID
     * @param fileId 파일 ID
     * @return 파일 정보
     */
    NoticeFile getNoticeFile(Long noticeId, Long fileId);
    
    /**
     * 파일과 함께 공지사항 수정
     * 
     * @param id 공지사항 ID
     * @param req 수정할 공지사항 정보
     * @param newFiles 새로 업로드할 파일들
     * @param deleteFileIds 삭제할 파일 ID들
     * @return 수정된 공지사항
     */
    NoticeManagementRes updateNoticeWithFiles(Long id, NoticeManagementUpdateReq req, MultipartFile[] newFiles, Long[] deleteFileIds);
}
