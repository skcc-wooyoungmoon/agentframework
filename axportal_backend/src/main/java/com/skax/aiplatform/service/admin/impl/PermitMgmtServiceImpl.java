package com.skax.aiplatform.service.admin.impl;

import com.skax.aiplatform.dto.admin.request.MenuPermitSearchReq;
import com.skax.aiplatform.dto.admin.request.PermitDetailSearchReq;
import com.skax.aiplatform.dto.admin.response.MenuPermitRes;
import com.skax.aiplatform.dto.admin.response.PermitDetailRes;
import com.skax.aiplatform.entity.auth.GpoAuthorityMas;
import com.skax.aiplatform.repository.admin.GpoAuthorityMasRepository;
import com.skax.aiplatform.service.admin.PermitMgmtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * 권한 관리 서비스 구현체
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-09-25
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermitMgmtServiceImpl implements PermitMgmtService {

    private final GpoAuthorityMasRepository permissionRepository;

    /**
     * 메뉴 권한 목록 조회
     */
    @Override
    public Page<MenuPermitRes> getMenuPermits(MenuPermitSearchReq searchReq) {
        log.info("[서비스] - 메뉴 권한 목록 조회 요청: {}", searchReq);

        Page<GpoAuthorityMas> authorities = permissionRepository.findMenuPermits(searchReq.toPageable(),
                searchReq.getOneDphMenu(), searchReq.getTwoDphMenu(), searchReq.getFilterType(),
                searchReq.getKeyword());
        Page<MenuPermitRes> permits = authorities.map(MenuPermitRes::from);

        log.info("메뉴 권한 목록 조회 완료 - count={}", permits.getTotalElements());
        return permits;
    }

    /**
     * 상세 권한 목록 조회
     */
    @Override
    public Page<PermitDetailRes> getPermitDetails(PermitDetailSearchReq searchReq) {
        if (searchReq.getAuthorityIds() == null || searchReq.getAuthorityIds().isEmpty()) {
            log.info("상세 권한 목록 조회 요청 - 전달된 ID가 없습니다.");
            return Page.empty();
        }

        log.info("상세 권한 목록 조회 요청 - authorityIds={}, twoDphMenu={}, filterType={}, keyword={}",
                searchReq.getAuthorityIds(), searchReq.getTwoDphMenu(), searchReq.getFilterType(),
                searchReq.getKeyword());

        Page<GpoAuthorityMas> authorities = permissionRepository.findAllByHrnkAuthorityIdIn(
                searchReq.toPageable(),
                searchReq.getAuthorityIds(),
                searchReq.getTwoDphMenu(),
                searchReq.getFilterType(),
                searchReq.getKeyword()
        );
        Page<PermitDetailRes> details = authorities.map(PermitDetailRes::from);

        log.info("상세 권한 목록 조회 완료 - count={}", details.getTotalElements());
        return details;
    }

}
