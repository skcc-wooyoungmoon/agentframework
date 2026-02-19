package com.skax.aiplatform.service.common.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.ione.api.dto.request.WorkGroupRegistRequest;
import com.skax.aiplatform.client.ione.api.dto.response.WorkGroupRegistResult;
import com.skax.aiplatform.client.ione.api.service.IoneApiService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.entity.common.enums.YNStatus;
import com.skax.aiplatform.entity.mapping.ProjectUserRole;
import com.skax.aiplatform.entity.mapping.ProjectUserRoleStatus;
import com.skax.aiplatform.entity.project.Project;
import com.skax.aiplatform.entity.project.ProjectStatus;
import com.skax.aiplatform.entity.role.Role;
import com.skax.aiplatform.entity.user.User;
import com.skax.aiplatform.repository.admin.GpoRoleAuthMapMasRepository;
import com.skax.aiplatform.repository.admin.ProjectMgmtRepository;
import com.skax.aiplatform.repository.admin.ProjectUserRoleRepository;
import com.skax.aiplatform.repository.admin.RoleRepository;
import com.skax.aiplatform.repository.home.GpoPrjuserroleRepository;
import com.skax.aiplatform.repository.home.GpoProjectsRepository;
import com.skax.aiplatform.repository.home.GpoRolesRepository;
import com.skax.aiplatform.repository.home.GpoUsersRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.common.ProjectMigService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectMigServiceImpl implements ProjectMigService {

    private final GpoProjectsRepository gpoProjectsRepository;
    private final ObjectMapper objectMapper;

    private final AdminAuthService adminAuthService;
    private final GpoPrjuserroleRepository gpoPrjuserroleRepository;
    private final GpoUsersRepository gpoUsersRepository;
    private final ProjectUserRoleRepository projectUserRoleRepository;
    private final GpoRoleAuthMapMasRepository gpoRoleAuthMapMasRepository;
    private final RoleRepository roleRepository;
    private final GpoRolesRepository gpoRolesRepository;
    private final ProjectMgmtRepository projectMgmtRepository;
    private final IoneApiService ioneApiService;

    private static final String EXPORT_FILE_NAME = "project_migration_data.json";

    private static final long PUBLIC_PROJECT_SEQ = -999L;
    private static final long PROJECT_ADMIN_ROLE_SEQ = -299L;
    private static final long PORTAL_ADMIN_ROLE_SEQ = -199L;

    @Value("${migration.base-dir}")
    private String migrationBaseDir;

    /**
     * 프로젝트 정보 Export
     * 특정 프로젝트의 정보를 파일로 저장 (Append/Update)
     */
    @Override
    @Transactional(readOnly = true)
    public void exportProject(Long prjSeq) {
        log.info("Start exporting project with prjSeq: {}", prjSeq);

        Project project = gpoProjectsRepository.findById(prjSeq)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with prjSeq: " + prjSeq));

        ProjectMigDto dto = toDto(project);

        String fileDir = migrationBaseDir + File.separator + EXPORT_FILE_NAME;
        File file = new File(fileDir);
        List<ProjectMigDto> projectList = new ArrayList<>();

        if (file.exists()) {
            try {
                projectList = objectMapper.readValue(file, new TypeReference<List<ProjectMigDto>>() {
                });
            } catch (IOException e) {
                log.error("Failed to read existing migration file", e);
                throw new RuntimeException("Failed to read existing migration file", e);
            }
        }

        // Update if exists, else add
        boolean updated = false;
        for (int i = 0; i < projectList.size(); i++) {
            if (projectList.get(i).getPrjSeq().equals(prjSeq)) {
                projectList.set(i, dto);
                updated = true;
                break;
            }
        }

        if (!updated) {
            projectList.add(dto);
        }

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, projectList);
            log.info("Successfully exported project to {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to write project to file", e);
            throw new RuntimeException("Failed to write project to file", e);
        }
    }

    /**
     * 프로젝트 정보 Import
     * 파일의 정보를 기반으로 DB 동기화 (Insert/Update/Delete)
     */
    @Override
    @Transactional
    public void importProjects() {
        String fileDir = migrationBaseDir + File.separator + EXPORT_FILE_NAME;
        log.info("Start importing projects from {}", fileDir);

        File file = new File(fileDir);
        if (!file.exists()) {
            log.warn("Migration file not found: {}", fileDir);
            return;
        }

        List<ProjectMigDto> fileProjects;
        try {
            fileProjects = objectMapper.readValue(file, new TypeReference<List<ProjectMigDto>>() {
            });
        } catch (IOException e) {
            log.error("Failed to read migration file", e);
            throw new RuntimeException("Failed to read migration file", e);
        }

        /* 로컬 테스트용 데이터 */
        // List<ProjectMigDto> fileProjects = List.of(ProjectMigDto.builder()
        //         .prjSeq(299L)
        //         .uuid("1234567890")
        //         .prjNm("test")
        //         .dtlCtnt("test")
        //         .statusNm("ONGOING")
        //         .sstvInfInclYn("N")
        //         .createdBy("admin")
        //         .updatedBy("admin")
        //         .build());

        List<Project> dbProjects = gpoProjectsRepository.findAll();
        Map<Long, Project> dbProjectMap = dbProjects.stream()
                .collect(Collectors.toMap(Project::getPrjSeq, Function.identity()));

        // 1. Update or Insert
        for (ProjectMigDto fileProject : fileProjects) {
            Optional<Project> first = dbProjects.stream().filter(p -> p.getPrjSeq().equals(fileProject.getPrjSeq())).findFirst();

            if (first.isPresent()) {
                // Update
                Project dbProject = first.get();
                updateProjectFromDto(dbProject, fileProject);
                gpoProjectsRepository.save(dbProject);
                log.info("Updated project: {}", fileProject.getPrjSeq());
            } else {
                // Insert
                insertProjectFromDto(fileProject);
                log.info("Inserted project: {}", fileProject.getPrjSeq());

                // Post-creation processing (ADXP groups, default roles)
                gpoProjectsRepository.findById(fileProject.getPrjSeq()).ifPresent(this::postCreateProject);
            }
        }

        // 2. Delete projects in DB but not in file
        /*Map<Long, ProjectMigDto> fileProjectMap = fileProjects.stream()
                .collect(Collectors.toMap(ProjectMigDto::getPrjSeq, Function.identity()));

        for (Project dbProject : dbProjects) {
            if (!fileProjectMap.containsKey(dbProject.getPrjSeq())) {
                // Pre-deletion processing (Clean up related entities, ADXP groups)
                preDeleteProject(dbProject.getPrjSeq());

                gpoProjectsRepository.delete(dbProject);
                log.info("Deleted project: {}", dbProject.getPrjSeq());
            }
        }*/

        log.info("Import completed.");
    }

    /**
     * 프로젝트 생성 후처리
     */
    private void postCreateProject(Project project) {
        if (project.getPrjSeq() == PUBLIC_PROJECT_SEQ) {
            return;
        }

        try {
            // 1. ADXP 그룹 생성
            String projectSeq = String.valueOf(project.getPrjSeq());
            String adminGroupNm = "P%s_R-299".formatted(projectSeq);
            String devGroupNm = "P%s_R-298".formatted(projectSeq);
            String testGroupNm = "P%s_R-297".formatted(projectSeq);

            try {
                if (adminAuthService.findGroupNamesByKeyword("P%s_".formatted(projectSeq)).isEmpty()) {
                    adminAuthService.createGroup(adminGroupNm);
                    adminAuthService.createGroup(devGroupNm);
                    adminAuthService.createGroup(testGroupNm);

                    log.info("Created ADXP groups for project: {}", project.getPrjSeq());
                }
            } catch (RuntimeException re) {
                log.warn("Failed to create ADXP groups for project: {}", project.getPrjSeq(), re);
            } catch (Exception e) {
                log.warn("Failed to create ADXP groups for project: {}", project.getPrjSeq(), e);
            }

            // 2. 포탈 관리자에게 프로젝트 관리자 권한 부여
            Role adminRole = gpoRolesRepository.findById(PROJECT_ADMIN_ROLE_SEQ).orElse(null);
            if (adminRole != null) {
                List<String> portalAdminIds = gpoPrjuserroleRepository.findMemberIdsByPrjSeqAndRoleSeq(PUBLIC_PROJECT_SEQ, PORTAL_ADMIN_ROLE_SEQ);
                if (portalAdminIds != null) {
                    for (String portalAdminId : portalAdminIds) {
                        try {
                            User portalAdmin = gpoUsersRepository.findById(portalAdminId).orElse(null);
                            if (portalAdmin != null) {
                                // 중복 체크
                                if (gpoPrjuserroleRepository.findByMemberIdAndPrjSeq(portalAdminId, project.getPrjSeq()).isEmpty()) {
                                    gpoPrjuserroleRepository.save(ProjectUserRole.builder()
                                            .statusNm(ProjectUserRoleStatus.INACTIVE)
                                            .user(portalAdmin)
                                            .project(project)
                                            .role(adminRole)
                                            .build());

                                    // ADXP 그룹 추가
                                    adminAuthService.assignUserToGroup(portalAdminId, adminGroupNm);
                                    log.info("Assigned portal admin {} to project {}", portalAdminId, project.getPrjSeq());
                                }
                            }
                        } catch (RuntimeException re) {
                            log.warn("Failed to assign portal admin {} to project {}", portalAdminId, project.getPrjSeq(), re);
                        } catch (Exception e) {
                            log.warn("Failed to assign portal admin {} to project {}", portalAdminId, project.getPrjSeq(), e);
                        }
                    }
                }
            }

            // 3. API GW 업무 코드 등록
            try {
                WorkGroupRegistResult workGroupRegistResult =
                        ioneApiService.registWorkGroup(WorkGroupRegistRequest.builder()
                                .businessCode(project.getPrjSeq().toString())
                                .businessName(project.getPrjSeq().toString())
                                .build());
                log.info("API GW 업무 코드 등록 결과: {}", workGroupRegistResult);
            } catch (BusinessException e) {
                // 비즈니스 예외는 그대로 전파
                log.error("API GW 업무 코드 등록 실패 (BusinessException): {}", e.getMessage(), e);
                throw e;
            } catch (Exception e) {
                // 기타 예상치 못한 예외
                log.error("API GW 업무 코드 등록 실패 (예상치 못한 오류): {}", e.getMessage(), e);
                throw new BusinessException(ErrorCode.API_GW_WORK_GROUP_REGIST_FAILED, e.getMessage());
            }
        } catch (RuntimeException re) {
            log.error("Error during postCreateProject for prjSeq: {}", project.getPrjSeq(), re);
        } catch (Exception e) {
            log.error("Error during postCreateProject for prjSeq: {}", project.getPrjSeq(), e);
        }
    }

    /**
     * 프로젝트 삭제 전처리
     */
    private void preDeleteProject(Long prjSeq) {
        log.info("Starting pre-delete cleanup for project: {}", prjSeq);

        // ADXP 그룹 키워드
        String groupKeyword = "P" + prjSeq + "_";

        // 1. 프로젝트 참여자 목록 조회 (나중에 ADXP 권한 제거용)
        List<ProjectUserRole> userMappings = projectUserRoleRepository.findByProjectPrjSeq(prjSeq);
        List<String> memberIds = userMappings.stream()
                .map(m -> m.getUser().getMemberId())
                .distinct()
                .toList();

        // 2. DB 데이터 삭제
        try {
            gpoRoleAuthMapMasRepository.deleteByRolePrjSeq(prjSeq);
            projectUserRoleRepository.deleteByPrjSeq(prjSeq);
            roleRepository.deleteByPrjSeq(prjSeq);
            projectMgmtRepository.deleteByPrjSeq(prjSeq);
            log.info("Deleted related entities for project: {}", prjSeq);
        } catch (RuntimeException re) {
            log.error("Failed to delete related entities for project: {}", prjSeq, re);
            throw new RuntimeException("Failed to delete related entities", re);
        } catch (Exception e) {
            log.error("Failed to delete related entities for project: {}", prjSeq, e);
            throw new RuntimeException("Failed to delete related entities", e);
        }

        // 3. ADXP 권한 동기화 (비동기)
        CompletableFuture.runAsync(() -> {
            try {
                List<String> groupNames = adminAuthService.findGroupNamesByKeyword(groupKeyword);
                if (!groupNames.isEmpty() && !memberIds.isEmpty()) {
                    for (String memberId : memberIds) {
                        for (String groupName : groupNames) {
                            try {
                                adminAuthService.unassignUserFromGroup(memberId, groupName);
                            } catch (RuntimeException re) {
                                log.warn("Failed to unassign user {} from group {}", memberId, groupName);
                            } catch (Exception e) {
                                log.warn("Failed to unassign user {} from group {}", memberId, groupName);
                            }
                        }
                    }
                }
            } catch (RuntimeException re) {
                log.warn("Async ADXP cleanup failed for project: {}", prjSeq, re);
            } catch (Exception e) {
                log.warn("Async ADXP cleanup failed for project: {}", prjSeq, e);
            }
        });
    }

    private ProjectMigDto toDto(Project project) {
        return ProjectMigDto.builder()
                .prjSeq(project.getPrjSeq())
                .uuid(project.getUuid())
                .prjNm(project.getPrjNm())
                .dtlCtnt(project.getDtlCtnt())
                .statusNm(project.getStatusNm() != null ? project.getStatusNm().name() : null)
                .sstvInfInclYn(project.getSstvInfInclYn() != null ? project.getSstvInfInclYn().name() : null)
                .sstvInfInclDesc(project.getSstvInfInclDesc())
                .createdBy(project.getCreatedBy())
                .updatedBy(project.getUpdatedBy())
                .fstCreatedAt(project.getFstCreatedAt())
                .lstUpdatedAt(project.getLstUpdatedAt())
                .build();
    }

    private void updateProjectFromDto(Project target, ProjectMigDto source) {
        target.setUuid(source.getUuid());
        target.setPrjNm(source.getPrjNm());
        target.setDtlCtnt(source.getDtlCtnt());

        if (StringUtils.hasText(source.getStatusNm())) {
            try {
                target.setStatusNm(ProjectStatus.valueOf(source.getStatusNm()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid ProjectStatus: {}", source.getStatusNm());
            }
        } else {
            target.setStatusNm(null);
        }

        if (StringUtils.hasText(source.getSstvInfInclYn())) {
            try {
                target.setSstvInfInclYn(YNStatus.valueOf(source.getSstvInfInclYn()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid YNStatus: {}", source.getSstvInfInclYn());
            }
        } else {
            target.setSstvInfInclYn(null);
        }

        target.setSstvInfInclDesc(source.getSstvInfInclDesc());
    }

    private void insertProjectFromDto(ProjectMigDto source) {
        gpoProjectsRepository.insertProjectWithSeq(
                source.getPrjSeq(),
                source.getUuid(),
                source.getPrjNm(),
                source.getDtlCtnt(),
                source.getStatusNm(),
                source.getSstvInfInclYn(),
                source.getSstvInfInclDesc(),
                source.getCreatedBy(),
                source.getUpdatedBy()
        );
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectMigDto {
        private Long prjSeq;
        private String uuid;
        private String prjNm;
        private String dtlCtnt;
        private String statusNm;
        private String sstvInfInclYn;
        private String sstvInfInclDesc;
        private String createdBy;
        private String updatedBy;
        private LocalDateTime fstCreatedAt;
        private LocalDateTime lstUpdatedAt;
    }
}