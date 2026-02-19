package com.skax.aiplatform.service.notice;

import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.notice.request.GetNoticeReq;
import com.skax.aiplatform.dto.notice.response.GetNoticeByIdRes;
import com.skax.aiplatform.dto.notice.response.GetNoticeRes;
import org.springframework.data.domain.Pageable;

public interface NoticeService {
    PageResponse<GetNoticeRes> getNoticeList(
            Pageable pageable,
            GetNoticeReq req
    );

    GetNoticeByIdRes getNoticeById(String notiId);
}
