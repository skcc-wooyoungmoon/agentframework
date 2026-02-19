package com.skax.aiplatform.controller.home;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.util.TokenInfo;
import com.skax.aiplatform.dto.home.request.ProjBaseInfoCreateReq;
import com.skax.aiplatform.dto.home.request.ProjInfoReq;
import com.skax.aiplatform.dto.home.response.ProjDetailRes;
import com.skax.aiplatform.dto.home.response.ProjPrivateRes;
import com.skax.aiplatform.dto.home.response.ProjUserRes;
import com.skax.aiplatform.dto.home.response.ProjectRes;
import com.skax.aiplatform.service.home.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/home/project")
@RequiredArgsConstructor
@Tag(name = "Home Project", description = "프로젝트생성 API")
public class ProjectController {

    private final ProjectService projectService;
    private final TokenInfo tokenInfo;

    @PostMapping("")
    @Operation(
            summary = "프로젝트 생성",
            description = "사용자명과 프로젝트 정보로 생성를 진행 함."
    )
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    public AxResponseEntity<ProjBaseInfoCreateReq> createPorject(
            @Valid @RequestBody ProjBaseInfoCreateReq projBaseInfoCreateReq
    ) {
        log.debug("+++++++++++++++++++++++++ create project ++++++++++++++++++++++++++++++++" + projBaseInfoCreateReq.toString());

        ProjBaseInfoCreateReq newProject = projectService.createProject(projBaseInfoCreateReq);
        return AxResponseEntity.ok(newProject, "프로젝트를 성공적으로 생성되었습니다.");
    }

    @GetMapping("/join-proj-list")
    @Operation(
            summary = "메인 화면에서 프로젝트 리스트",
            description = "현재 사용자가 참여하고 있는 프로젝트 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public AxResponseEntity<List<ProjectRes>> getJoinProjectList(@RequestParam(required = false) String username) {
        log.debug("프로젝트 참여 목록 조회 {}", tokenInfo.getUserName());
        // username = "SGO1847295";

        List<ProjectRes> projectList = projectService.getJoinProjectList(tokenInfo.getUserName());
        return AxResponseEntity.ok(projectList, "프로젝트 참여 목록을 성공적으로 조회했습니다.");
    }

    @GetMapping("/join-private-proj-list")
    @Operation(
            summary = "참여 프로젝트 목록 조회 - 탈퇴화면에서 필요 함, Public, R0004 인 경우 대상제외",
            description = "현재 사용자가 참여하고 있는 프로젝트 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public AxResponseEntity<List<ProjPrivateRes>> getJoinPrivateProjectList(
            @RequestParam(required = false) String username) {
        log.debug("프로젝트 참여 목록 조회");

        List<ProjPrivateRes> projectList = projectService.getJoinPrivateProjectList(tokenInfo.getUserName());
        return AxResponseEntity.ok(projectList, "프로젝트 참여 목록을 성공적으로 조회했습니다.");
    }

    @GetMapping("/not-join-proj-list")
    @Operation(
            summary = "미참여 프로젝트 목록 조회",
            description = "현재 사용자가 참여하고 있지 않는 프로젝트 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public AxResponseEntity<List<ProjPrivateRes>> getNotJoinProjectList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String keyword
    ) {
        log.debug("미참여 프로젝트 목록 조회 {} - {} - {}", tokenInfo.getUserName(), condition, keyword);
        List<ProjPrivateRes> projectList = projectService.getNotJoinProjectList(tokenInfo.getUserName(), condition, keyword);
        return AxResponseEntity.ok(projectList, "프로젝트 참여 목록을 성공적으로 조회했습니다.");
    }

    @GetMapping("/not-join-proj-detail")
    @Operation(
            summary = "미참여 프로젝트 목록 조회",
            description = "현재 사용자가 참여하고 있지 않는 프로젝트 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public AxResponseEntity<List<ProjDetailRes>> getNotJoinProjectDetail(
            @RequestParam(required = false) String projectId) {
        log.debug(">> << 프로젝트 참여 목록 상세  {}", projectId);
        List<ProjDetailRes> projectList = projectService.getNotJoinProjectDetail(projectId);
        log.debug("projectList {}", projectList.toString());
        return AxResponseEntity.ok(projectList, "프로젝트 참여 목록을 성공적으로 조회했습니다.");
    }

    @GetMapping("/join-user-list")
    @Operation(
            summary = "참여 프로젝트 유저 목록 조회",
            description = "현재 사용자가 참여하고 있는 프로젝트 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public AxResponseEntity<List<ProjUserRes>> getProjectUserList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String keyword
    ) {
        log.debug("프로젝트 참여 목록 조회 {} - {} - {}", tokenInfo.getUserName(), condition, keyword);

        List<ProjUserRes> projectList = projectService.getProjectUserList(tokenInfo.getUserName(), condition, keyword);
        return AxResponseEntity.ok(projectList, "프로젝트 참여 목록을 성공적으로 조회했습니다.");
    }

    @PostMapping("/join")
    @Operation(
            summary = "프로젝트 참여",
            description = "사용자명과 프로젝트 정보로 참여를 진행 함."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로젝트 참여 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public AxResponseEntity<ProjInfoReq> joinPorject(@Valid @RequestBody ProjInfoReq projJoinReq,
                                                     HttpServletResponse response) throws Exception {

        log.debug("+++++++++++++++++++++++++ join project ++++++++++++++++++++++++++++++++" + projJoinReq.toString());
        String retruenVal = "";
        retruenVal = projectService.joinProject(projJoinReq);
        if (retruenVal != null) {
            projJoinReq.getProject().setUuid(retruenVal);
            return AxResponseEntity.ok(projJoinReq, "프로젝트를 성공적으로 참여 되었습니다.");
        } else {
            return AxResponseEntity.ok(projJoinReq, "프로젝트를 참여에 실패 하였습니다.");

        }

    }

    @PostMapping("/quit")
    @Operation(
            summary = "프로젝트 탈퇴",
            description = "사용자명과 프로젝트 정보로 탈퇴를 진행 함."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로젝트 탈퇴 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public AxResponseEntity<ProjInfoReq> quitPorject(@Valid @RequestBody ProjInfoReq projQuitReq,
                                                     HttpServletResponse response) throws Exception {

        log.debug("+++++++++++++++++++++++++ quit project ++++++++++++++++++++++++++++++++" + projQuitReq.toString());
        int retruenVal = -1;
        retruenVal = projectService.quitProject(projQuitReq);
        if (retruenVal > -1) {
            return AxResponseEntity.ok(projQuitReq, "프로젝트를 성공적으로 탈퇴 되었습니다.");
        } else {
            return AxResponseEntity.ok(projQuitReq, "프로젝트를 탈퇴가 실패 되었습니다.");
        }

    }

    @DeleteMapping("/{prjSeq}")
    @Operation(
            summary = "프로젝트 생성 롤백",
            description = "결재 취소 등 프로젝트 생성 롤백처리"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "프로젝트 삭제 성공")
    })
    public AxResponseEntity<Void> deletePorject(@PathVariable Long prjSeq) throws Exception {
        try {
            if (prjSeq != 99999) {
                projectService.deleteProject(prjSeq);
            }
        } catch (NullPointerException ne) {
            return AxResponseEntity.deleted("프로젝트를 성공적으로 삭제했습니다.");
        } catch (RuntimeException re) {
            return AxResponseEntity.deleted("프로젝트를 성공적으로 삭제했습니다.");
        } catch (Exception e) {
            return AxResponseEntity.deleted("프로젝트를 성공적으로 삭제했습니다.");
        }

        return AxResponseEntity.deleted("프로젝트를 성공적으로 삭제했습니다.");
    }

    @GetMapping("/{memberId}/getme")
    @Operation(
            summary = "프로젝트 셍성자 유저정보 조회",
            description = "프로젝트 셍성자 유저정보 조회 함"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public AxResponseEntity<ProjUserRes> getProjectUserInfo(@PathVariable String memberId) {
        log.debug("+++++++++++++++++++++++++ getProjectUserInfo ++++++++++++++++++++++++++++++++" + tokenInfo.getUserName() + "dd");
        ProjUserRes projUserRes = projectService.getProjectUserInfo(tokenInfo.getUserName());
        return AxResponseEntity.ok(projUserRes, "프로젝트 생성자 정보를 성공적으로 조회했습니다.");
    }

}
