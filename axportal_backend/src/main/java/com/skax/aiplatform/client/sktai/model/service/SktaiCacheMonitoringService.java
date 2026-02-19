package com.skax.aiplatform.client.sktai.model.service;

import com.skax.aiplatform.client.sktai.model.SktaiCacheMonitoringClient;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Cache Monitoring 서비스
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiCacheMonitoringService {

    private final SktaiCacheMonitoringClient sktaiCacheMonitoringClient;

    /**
     * Cache 통계 조회
     */
    public Object getCacheStats(String prefix) {
        log.debug("Cache 통계 조회 요청 - prefix: {}", prefix);
        
        try {
            Object response = sktaiCacheMonitoringClient.getCacheStats(prefix);
            log.debug("Cache 통계 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Cache 통계 조회 실패 - prefix: {}", prefix, e);
            throw e;
        } catch (Exception e) {
            log.error("Cache 통계 조회 실패 - prefix: {}", prefix, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Cache 통계 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Cache 통계 초기화
     */
    public Object resetCacheStats(String prefix) {
        log.debug("Cache 통계 초기화 요청 - prefix: {}", prefix);
        
        try {
            Object response = sktaiCacheMonitoringClient.resetCacheStats(prefix);
            log.debug("Cache 통계 초기화 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Cache 통계 초기화 실패 - prefix: {}", prefix, e);
            throw e;
        } catch (Exception e) {
            log.error("Cache 통계 초기화 실패 - prefix: {}", prefix, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Cache 통계 초기화에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Cache 키 목록 조회
     */
    public Object getCacheKeys(String pattern, Integer maxCount) {
        log.debug("Cache 키 목록 조회 요청 - pattern: {}, maxCount: {}", pattern, maxCount);
        
        try {
            Object response = sktaiCacheMonitoringClient.getCacheKeys(pattern, maxCount);
            log.debug("Cache 키 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Cache 키 목록 조회 실패 - pattern: {}", pattern, e);
            throw e;
        } catch (Exception e) {
            log.error("Cache 키 목록 조회 실패 - pattern: {}", pattern, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Cache 키 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 패턴으로 Cache 키 삭제
     */
    public Object deleteCacheKeysByPattern(String pattern, Integer count) {
        log.debug("패턴으로 Cache 키 삭제 요청 - pattern: {}, count: {}", pattern, count);
        
        try {
            Object response = sktaiCacheMonitoringClient.deleteCacheKeysByPattern(pattern, count);
            log.debug("패턴으로 Cache 키 삭제 성공");
            return response;
        } catch (BusinessException e) {
            log.error("패턴으로 Cache 키 삭제 실패 - pattern: {}", pattern, e);
            throw e;
        } catch (Exception e) {
            log.error("패턴으로 Cache 키 삭제 실패 - pattern: {}", pattern, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "패턴으로 Cache 키 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Cache 전체 플러시
     */
    public Object flushCache(Boolean confirm) {
        log.debug("Cache 전체 플러시 요청 - confirm: {}", confirm);
        
        try {
            Object response = sktaiCacheMonitoringClient.flushCache(confirm);
            log.debug("Cache 전체 플러시 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Cache 전체 플러시 실패", e);
            throw e;
        } catch (Exception e) {
            log.error("Cache 전체 플러시 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Cache 전체 플러시에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Redis 정보 조회
     */
    public Object getRedisInfo(Boolean detail) {
        log.debug("Redis 정보 조회 요청 - detail: {}", detail);
        
        try {
            Object response = sktaiCacheMonitoringClient.getRedisInfo(detail);
            log.debug("Redis 정보 조회 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Redis 정보 조회 실패", e);
            throw e;
        } catch (Exception e) {
            log.error("Redis 정보 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Redis 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }
}
