package com.skax.aiplatform.service.data;

import com.skax.aiplatform.dto.data.response.SourceSystemInfo;
import java.util.List;

/**
 * 원천 시스템 서비스 인터페이스
 */
public interface DataCtlgSourceSystemService {

    /**
     * 원천 시스템 목록 조회
     * @return 원천 시스템 목록
     */
    List<SourceSystemInfo> getSourceSystems();
}

