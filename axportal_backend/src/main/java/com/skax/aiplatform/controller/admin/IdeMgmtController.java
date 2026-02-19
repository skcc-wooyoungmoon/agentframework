package com.skax.aiplatform.controller.admin;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.admin.request.CreateImageReq;
import com.skax.aiplatform.dto.admin.request.DeleteImageReq;
import com.skax.aiplatform.dto.admin.request.SearchImageReq;
import com.skax.aiplatform.dto.admin.request.UpdateImageReq;
import com.skax.aiplatform.dto.admin.request.UpdateImageResourceReq;
import com.skax.aiplatform.dto.admin.response.CreateImageRes;
import com.skax.aiplatform.dto.admin.response.DwAccountRes;
import com.skax.aiplatform.dto.admin.response.ImageDetailRes;
import com.skax.aiplatform.dto.admin.response.ImageListRes;
import com.skax.aiplatform.dto.admin.response.ImageResourceRes;
import com.skax.aiplatform.service.admin.IdeMgmtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin/ide")
@RequiredArgsConstructor
@Tag(name = "IDE 관리 API")
public class IdeMgmtController {

    private final IdeMgmtService ideMgmtService;

    @GetMapping
    @Operation(summary = "이미지 목록 조회")
    public AxResponseEntity<PageResponse<ImageListRes>> getImageList(@Valid SearchImageReq request) {
        Page<ImageListRes> images = ideMgmtService.getImageList(request);
        return AxResponseEntity.okPage(images, "이미지 목록을 성공적으로 조회했습니다.");
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "이미지 상세 조회")
    public AxResponseEntity<ImageDetailRes> getImage(@PathVariable String uuid) {
        ImageDetailRes image = ideMgmtService.getImage(uuid);
        return AxResponseEntity.ok(image, "이미지를 성공적으로 조회했습니다.");
    }

    @PostMapping
    @Operation(summary = "이미지 생성")
    public AxResponseEntity<CreateImageRes> createImage(@Valid @RequestBody CreateImageReq request) {
        String uuid = ideMgmtService.createImage(request);
        return AxResponseEntity.ok(CreateImageRes.of(uuid), "이미지를 성공적으로 생성했습니다.");
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "이미지 수정")
    public AxResponseEntity<Void> updateImage(@PathVariable String uuid, @Valid @RequestBody UpdateImageReq request) {
        ideMgmtService.updateImage(uuid, request);
        return AxResponseEntity.success("이미지를 성공적으로 수정했습니다.");
    }

    @DeleteMapping
    @Operation(summary = "이미지 삭제")
    public AxResponseEntity<Void> deleteImages(@Valid @RequestBody DeleteImageReq request) {
        ideMgmtService.deleteImages(request.getUuids());
        return AxResponseEntity.success("이미지를 성공적으로 삭제했습니다.");
    }

    // == Dw Account == //

    @GetMapping("/dw-accounts")
    @Operation(summary = "DW 계정 목록 조회")
    public AxResponseEntity<List<DwAccountRes>> getDwAccounts() {
        List<DwAccountRes> accounts = ideMgmtService.getDwAccounts();
        return AxResponseEntity.ok(accounts, "DW 계정 목록을 성공적으로 조회했습니다.");
    }

    // == Resource == //

    @GetMapping("/resource")
    @Operation(summary = "리소스 환경설정 조회")
    public AxResponseEntity<List<ImageResourceRes>> getImageResources() {
        log.info("[IdeMgmtController] 리소스 환경설정 조회 요청");

        List<ImageResourceRes> resources = ideMgmtService.getImageResources();
        return AxResponseEntity.ok(resources, "리소스 환경설정을 성공적으로 조회했습니다.");
    }

    @PutMapping("/resource")
    @Operation(summary = "리소스 환경설정")
    public AxResponseEntity<Void> updateImageResource(@Valid @RequestBody List<UpdateImageResourceReq> requests) {
        ideMgmtService.updateImageResource(requests);
        return AxResponseEntity.success("리소스 환경설정을 성공적으로 수정했습니다.");
    }

}
