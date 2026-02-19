package com.skax.aiplatform.service.admin;

import com.skax.aiplatform.dto.admin.request.MenuPermitSearchReq;
import com.skax.aiplatform.dto.admin.request.PermitDetailSearchReq;
import com.skax.aiplatform.dto.admin.response.MenuPermitRes;
import com.skax.aiplatform.dto.admin.response.PermitDetailRes;
import org.springframework.data.domain.Page;

/**
 * 권한 관리 서비스 인터페이스
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-09-25
 */
public interface PermitMgmtService {

    /**
     * 메뉴 권한 목록 조회
     */
    Page<MenuPermitRes> getMenuPermits(MenuPermitSearchReq searchReq);

    /**
     * 상세 권한 목록 조회
     */
    Page<PermitDetailRes> getPermitDetails(PermitDetailSearchReq searchReq);

}
