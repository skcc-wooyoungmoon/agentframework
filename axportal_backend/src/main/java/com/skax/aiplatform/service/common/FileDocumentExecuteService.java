package com.skax.aiplatform.service.common;

import com.skax.aiplatform.dto.common.request.FileDocumentRequest;
import com.skax.aiplatform.dto.common.response.FileDocumentResponse;

/**
 * 파일 다큐먼트 서비스 인터페이스
 * 
 * <p>
 * 프론트엔드에서 전달받은 파일 다큐먼트 안전하게 실행하는 서비스입니다.
 * </p>
 * 
 * @author Generated
 * @since 2025-01-XX
 */
public interface FileDocumentExecuteService {

    /**
     * 파일 다큐먼트 실행
     * 
     * <ul>
     * 
     * </ul>
     * 
     * @param request 실행 요청
     * @return 실행 결과
     * @throws IllegalArgumentException 잘못된 경우
     * @throws RuntimeException         오류가 발생한 경우
     */
    FileDocumentResponse executeFileDocument(FileDocumentRequest request);
}
