package com.skax.aiplatform.service.admin.impl;

import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.dto.admin.request.CreateImageReq;
import com.skax.aiplatform.dto.admin.request.SearchImageReq;
import com.skax.aiplatform.dto.admin.request.UpdateImageReq;
import com.skax.aiplatform.dto.admin.request.UpdateImageResourceReq;
import com.skax.aiplatform.dto.admin.response.DwAccountRes;
import com.skax.aiplatform.dto.admin.response.ImageDetailRes;
import com.skax.aiplatform.dto.admin.response.ImageListRes;
import com.skax.aiplatform.dto.admin.response.ImageResourceRes;
import com.skax.aiplatform.entity.ide.GpoIdeImageMas;
import com.skax.aiplatform.entity.ide.GpoIdeResourceMas;
import com.skax.aiplatform.repository.home.GpoIdeImageMasRepository;
import com.skax.aiplatform.repository.home.GpoIdeResourceMasRepository;
import com.skax.aiplatform.service.admin.IdeMgmtService;
import com.skax.aiplatform.service.kube.KubeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * IDE 관리 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IdeMgmtServiceImpl implements IdeMgmtService {

    private final GpoIdeImageMasRepository gpoIdeImageMasRepository;
    private final GpoIdeResourceMasRepository gpoIdeResourceMasRepository;
    private final KubeService kubeService;

    /**
     * 이미지 목록 조회 (검색)
     */
    @Override
    public Page<ImageListRes> getImageList(SearchImageReq request) {
        String keyword = request.getKeyword() != null
                ? request.getKeyword().toLowerCase()
                : null;

        Page<GpoIdeImageMas> images = gpoIdeImageMasRepository.findImagesBySearch(
                request.getImgG(),
                keyword,
                request.toPageable()
        );

        return images.map(ImageListRes::of);
    }

    /**
     * 이미지 상세 조회
     */
    @Override
    public ImageDetailRes getImage(String uuid) {
        GpoIdeImageMas image = findImageById(uuid);
        return ImageDetailRes.of(image);
    }

    /**
     * 이미지 생성
     */
    @Transactional
    @Override
    public String createImage(CreateImageReq request) {
        String imageName = request.getImgNm().trim();
        String description = request.getDtlCtnt().trim();
        String imageUrl = request.getImgUrl().trim();

        // 이미지명 중복 확인
        if (gpoIdeImageMasRepository.existsByImgNm(imageName)) {
            throw new BusinessException(ErrorCode.DUPLICATE_IMAGE_NAME);
        }

        GpoIdeImageMas image = GpoIdeImageMas.create(imageName, description, imageUrl, request.getImgG());
        GpoIdeImageMas savedImage = gpoIdeImageMasRepository.save(image);
        return savedImage.getUuid();
    }

    /**
     * 이미지 수정
     */
    @Transactional
    @Override
    public void updateImage(String uuid, @Valid UpdateImageReq request) {
        GpoIdeImageMas image = findImageById(uuid);

        String imageName = request.getImgNm().trim();
        String description = request.getDtlCtnt().trim();
        String imageUrl = request.getImgUrl().trim();

        // 이미지명 중복 확인 (현재 수정하는 이미지는 제외)
        if (gpoIdeImageMasRepository.existsByImgNmAndUuidNot(imageName, uuid)) {
            throw new BusinessException(ErrorCode.DUPLICATE_IMAGE_NAME);
        }

        image.update(imageName, description, imageUrl, request.getImgG());
    }

    /**
     * 이미지 다중 삭제
     */
    @Transactional
    @Override
    public void deleteImages(List<String> uuids) {
        gpoIdeImageMasRepository.deleteAllByIdIn(uuids);
    }

    // == Dw Account == //

    /**
     * DW 계정 목록 조회
     */
    @Override
    public List<DwAccountRes> getDwAccounts() {
        return kubeService.getDwAllAccountsForAdmin();
    }

    // == Resource == //

    /**
     * 리소스 환경 설정 조회
     */
    @Override
    public List<ImageResourceRes> getImageResources() {
        return gpoIdeResourceMasRepository.findAll().stream()
                .map(ImageResourceRes::of)
                .collect(Collectors.toList());
    }

    /**
     * 리소스 환경 설정 (이미지 생성 제한 개수)
     */
    @Transactional
    @Override
    public void updateImageResource(List<UpdateImageResourceReq> requests) {
        requests.forEach(req -> {
            GpoIdeResourceMas resource = gpoIdeResourceMasRepository.findById(req.getImgG())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
            resource.updateLimitCnt(req.getLimitCnt());
        });
    }

    // == Helper Method == //

    private GpoIdeImageMas findImageById(String uuid) {
        return gpoIdeImageMasRepository.findById(uuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));
    }

}
