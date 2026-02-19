package com.skax.aiplatform.client.udp.embedding.service;

import com.skax.aiplatform.client.udp.embedding.UdpKtEmbeddingClient;
import com.skax.aiplatform.client.udp.embedding.dto.request.KtEmbeddingRequest;
import com.skax.aiplatform.client.udp.embedding.dto.response.KtEmbeddingResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * UDP KT Embedding API 서비스
 * 
 * <p>UDP KT Embedding 클라이언트를 래핑하여 비즈니스 로직과 예외 처리를 담당하는 서비스입니다.
 * KT 임베딩 추론 관련 API에 대한 서비스 메서드를 제공합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UdpKtEmbeddingService {

    private final UdpKtEmbeddingClient udpKtEmbeddingClient;

    /**
     * KT 임베딩 추론
     * 
     * <p>입력된 텍스트를 KT 임베딩 모델을 사용하여 벡터로 변환합니다.
     * 생성된 벡터는 텍스트 유사도 계산, 검색, 클러스터링 등에 활용할 수 있습니다.</p>
     * 
     * @param request 임베딩 생성 요청 정보
     * @return 생성된 임베딩 벡터 및 메타데이터
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public KtEmbeddingResponse generateEmbedding(KtEmbeddingRequest request) {
        try {
            log.info("🟠 UDP KT 임베딩 생성 요청 - model: {}, 텍스트 수: {}, normalize: {}", 
                    request.getModel(), request.getTexts().size(), request.getNormalize());
            
            KtEmbeddingResponse response = udpKtEmbeddingClient.generateEmbedding(request);
            
            log.info("🟠 UDP KT 임베딩 생성 성공 - model: {}, 임베딩 벡터 수: {}, 차원: {}", 
                    request.getModel(), 
                    response.getEmbeddings() != null ? response.getEmbeddings().size() : 0,
                    response.getDimension() != null ? response.getDimension() : 0);
                    
            return response;
        } catch (BusinessException e) {
            log.error("🟠 UDP KT 임베딩 생성 실패 - model: {}, BusinessException: {}", request.getModel(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟠 UDP KT 임베딩 생성 실패 - model: {}, 텍스트 수: {}, 예상치 못한 오류", 
                    request.getModel(), request.getTexts().size(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "KT 임베딩 생성에 실패했습니다: " + e.getMessage());
        }
    }
}