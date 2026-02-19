package com.skax.aiplatform.controller.admin;


import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.admin.request.MenuPermitSearchReq;
import com.skax.aiplatform.dto.admin.request.PermitDetailSearchReq;
import com.skax.aiplatform.dto.admin.response.MenuPermitRes;
import com.skax.aiplatform.dto.admin.response.PermitDetailRes;
import com.skax.aiplatform.service.admin.PermitMgmtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/permit")
@RequiredArgsConstructor
@Tag(name = "권한 관리 API")
public class PermitController {

    private final PermitMgmtService permitMgmtService;

    /**
     * 메뉴 진입 설정을 위한 권한(메뉴) 목록 조회
     */
    @GetMapping("/menus")
    @Operation(
            summary = "메뉴 권한 목록 조회",
            description = "선택 가능한 메뉴 진입 권한 목록을 반환합니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1"),
                    @Parameter(name = "size", description = "페이지 크기", example = "10"),
                    @Parameter(name = "oneDphMenu", description = "상위 메뉴명 필터 (드롭다운용)", example = "사용자 관리"),
                    @Parameter(name = "twoDphMenu", description = "하위 메뉴명 필터 (드롭다운용)", example = "사용자 목록"),
                    @Parameter(name = "filterType", description = "검색어 적용 필드 (검색창용)", example = "oneDphMenu"),
                    @Parameter(name = "keyword", description = "검색어 (검색창용)", example = "사용자")
            }
    )
    @ApiResponse(responseCode = "200", description = "메뉴 권한 목록 조회 성공")
    public AxResponseEntity<PageResponse<MenuPermitRes>> getMenuPermits(MenuPermitSearchReq searchReq) {
        log.info("[컨트롤러] 메뉴 목록 조회 요청: {}", searchReq);

        Page<MenuPermitRes> authorities = permitMgmtService.getMenuPermits(searchReq);

        return AxResponseEntity.okPage(authorities, "메뉴 권한 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 선택된 메뉴 기반 상세 권한 목록 조회
     */
    @GetMapping("/details")
    @Operation(
            summary = "권한 상세 목록 조회",
            description = "선택한 메뉴 권한을 기반으로 상세 권한 정보를 조회합니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호 (1부터 시작)", example = "1"),
                    @Parameter(name = "size", description = "페이지 크기", example = "10"),
                    @Parameter(name = "authorityIds", description = "선택된 권한 ID 목록", example = "AUTH001,AUTH002"),
                    @Parameter(name = "twoDphMenu", description = "하위 메뉴명 필터 (드롭다운용)", example = "데이터 저장소"),
                    @Parameter(name = "filterType", description = "검색어 적용 필드 (검색창용)", example = "authorityNm"),
                    @Parameter(name = "keyword", description = "검색어 (검색창용)", example = "조회")
            }
    )
    @ApiResponse(responseCode = "200", description = "권한 상세 목록 조회 성공")
    public AxResponseEntity<PageResponse<PermitDetailRes>> getPermitDetails(PermitDetailSearchReq searchReq) {
        log.info("권한 상세 목록 조회 - searchReq={}", searchReq);

        Page<PermitDetailRes> authorityDetails = permitMgmtService.getPermitDetails(searchReq);

        return AxResponseEntity.okPage(authorityDetails, "권한 상세 목록을 성공적으로 조회했습니다.");
    }

}
