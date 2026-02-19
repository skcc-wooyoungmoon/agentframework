package com.skax.aiplatform.mapper.prompt;

import com.skax.aiplatform.dto.prompt.response.WorkFlowRes;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.prompt.WorkFlow;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {UUID.class})
public abstract class WorkFlowMapper {

    @Autowired
    protected GpoUsersMasRepository gpoUsersMasRepository;

    @Mappings({
            @Mapping(source = "workflowName", target = "workflowName"),
            @Mapping(source = "versionNo", target = "versionNo"),
            @Mapping(source = "xmlText", target = "xmlText"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "projectScope", target = "projectScope"),
            @Mapping(source = "tag", target = "tagsRaw"),
            // Audit fields
            @Mapping(source = "fstCreatedAt", target = "createdAt"),
            @Mapping(source = "lstUpdatedAt", target = "updatedAt"),
            // derived/processed fields handled in @AfterMapping
            @Mapping(target = "workflowId", ignore = true),
            @Mapping(target = "projectSeq", ignore = true),
            @Mapping(target = "tags", ignore = true),
            @Mapping(target = "isActive", ignore = true),
            // createdBy/updatedBy will be set in @AfterMapping using gpo_users_mas join
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "updatedBy", ignore = true)
    })
    public abstract WorkFlowRes toDto(WorkFlow entity);

    @AfterMapping
    protected void fillDerivedFields(WorkFlow src, @MappingTarget WorkFlowRes dst) {
        // workflowId 변환: String -> UUID
        if (src.getWorkflowId() != null && !src.getWorkflowId().isBlank()) {
            try {
                // 8자리 String을 UUID 형식으로 변환 (대문자 유지)
                // 예: "ABC12345" -> "ABC12345-0000-0000-0000-000000000000"
                String paddedId = String.format("%s-0000-0000-0000-000000000000", src.getWorkflowId().toUpperCase());
                dst.setWorkflowId(UUID.fromString(paddedId));
            } catch (IllegalArgumentException e) {
                // 변환 실패 시 null
                dst.setWorkflowId(null);
            }
        }

        dst.setProjectSeq(src.getProjectSeq());

        // isActive 변환: Integer 1/0 -> char 'Y'/'N'
        if (src.getIsActive() != null) {
            dst.setIsActive(src.getIsActive().equals(1) ? 'Y' : 'N');
        } else {
            dst.setIsActive('N');
        }

        // createdBy / updatedBy 포맷팅: '{jkw_nm} | {dept_nm}'
        // createdBy/updatedBy에는 member_id가 저장되어 있다고 가정 (요구사항 기준)
        String createdById = src.getCreatedBy();
        String updatedById = src.getUpdatedBy();
        dst.setCreatedBy(formatUser(createdById));
        dst.setUpdatedBy(formatUser(updatedById));

        // 태그 변환
        String raw = src.getTag();
        if (raw == null || raw.isBlank()) {
            dst.setTags(Collections.emptyList());
            return;
        }
        List<String> list = Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        dst.setTags(list);
    }

    protected String formatUser(String memberIdOrUuid) {
        if (memberIdOrUuid == null || memberIdOrUuid.isBlank()) {
            return null;
        }
        // 우선 member_id로 조회, 없으면 uuid로 조회 (호환성)
        GpoUsersMas user = gpoUsersMasRepository.findByMemberId(memberIdOrUuid)
                .orElseGet(() -> gpoUsersMasRepository.findByUuid(memberIdOrUuid).orElse(null));
        if (user == null) {
            return memberIdOrUuid; // 조회 실패 시 원본 값을 반환
        }
        String name = safe(user.getJkwNm());
        String dept = safe(user.getDeptNm());
        if (name.isEmpty() && dept.isEmpty()) {
            return memberIdOrUuid;
        }
        return String.format("%s | %s", name, dept);
    }

    private String safe(String v) {
        return v == null ? "" : v;
    }

    // 변환 헬퍼
    protected Instant map(LocalDateTime value) {
        return value != null ? value.atZone(ZoneId.systemDefault()).toInstant() : null;
    }
}