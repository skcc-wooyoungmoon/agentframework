package com.skax.aiplatform.service.admin;

import java.util.List;

import org.springframework.data.domain.Page;

import com.skax.aiplatform.dto.admin.request.CreateImageReq;
import com.skax.aiplatform.dto.admin.request.SearchImageReq;
import com.skax.aiplatform.dto.admin.request.UpdateImageReq;
import com.skax.aiplatform.dto.admin.request.UpdateImageResourceReq;
import com.skax.aiplatform.dto.admin.response.DwAccountRes;
import com.skax.aiplatform.dto.admin.response.ImageDetailRes;
import com.skax.aiplatform.dto.admin.response.ImageListRes;
import com.skax.aiplatform.dto.admin.response.ImageResourceRes;

/**
 * IDE 관리 서비스 인터페이스
 */
public interface IdeMgmtService {

    /**
     * 이미지 목록 조회 (페이징)
     */
    Page<ImageListRes> getImageList(SearchImageReq request);

    /**
     * 이미지 목록 조회
     */
    ImageDetailRes getImage(String uuid);

    /**
     * 이미지 생성
     * @return 생성된 이미지 UUID
     */
    String createImage(CreateImageReq request);

    /**
     * 이미지 수정
     */
    void updateImage(String uuid, UpdateImageReq request);

    /**
     * 이미지 삭제 (다중)
     */
    void deleteImages(List<String> uuids);

    // == Dw Account == //

    /**
     * DW 계정 목록 조회
     */
    List<DwAccountRes> getDwAccounts();

    // == Resource == //

    /**
     * 리소스 환경 설정 조회
     */
    List<ImageResourceRes> getImageResources();

    /**
     * 이미지 리소스 환경 설정
     */
    void updateImageResource(List<UpdateImageResourceReq> requests);

}
