package com.skax.aiplatform.service.home;

import java.util.List;

import org.springframework.data.domain.Page;

import com.skax.aiplatform.dto.home.request.IdeCreateReq;
import com.skax.aiplatform.dto.home.request.IdeDeleteReq;
import com.skax.aiplatform.dto.home.request.IdeExtendReq;
import com.skax.aiplatform.dto.home.request.SearchIdeStatusReq;
import com.skax.aiplatform.dto.home.response.IdeCreateRes;
import com.skax.aiplatform.dto.home.response.IdeImageRes;
import com.skax.aiplatform.dto.home.response.IdeStatusRes;
import com.skax.aiplatform.dto.vertica.response.DwAccountListRes;

public interface IDEService {

    IdeImageRes getIdeImage();

    List<DwAccountListRes> getDWAccount(String user_id);

    IdeCreateRes createIde(IdeCreateReq request);

    /**
     * IDE 생성 가능 여부 확인
     * @param ideType IDE 타입 (JUPYTER | VSCODE)
     * @param userId 사용자 ID
     * @return 생성 가능 여부
     */
    boolean isIdeCreateAvailable(String ideType, String userId);

    void deleteIde(IdeDeleteReq ideItem);

    void deleteIdeBatch();

    Page<IdeStatusRes> getIdeStatus(String memberId, SearchIdeStatusReq request);

    void extendIdeExpiration(String statusUuid, IdeExtendReq request);

}
