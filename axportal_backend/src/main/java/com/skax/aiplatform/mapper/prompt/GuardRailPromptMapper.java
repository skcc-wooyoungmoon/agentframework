package com.skax.aiplatform.mapper.prompt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.skax.aiplatform.client.sktai.agent.dto.request.PromptCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.PromptUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptFilterByTagsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptMessagesResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptTagsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptVariablesResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptVersionResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptVersionsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PromptsResponse;
import com.skax.aiplatform.common.util.DateUtils;
import com.skax.aiplatform.dto.prompt.request.GuardRailPromptCreateReq;
import com.skax.aiplatform.dto.prompt.request.GuardRailPromptUpdateReq;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptBuiltinRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptByIdRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptCreateRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptLatestByIdRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptMsgsByIdRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptTagsListByIdRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptVarsByIdRes;
import com.skax.aiplatform.dto.prompt.response.GuardRailPromptVerListByIdRes;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GuardRailPromptMapper {

    // ================= Create/Update Request 매핑 =================

    /**
     * GuardRailPromptCreateReq를 SKTAI API 요청 형식으로 변환
     */
    default PromptCreateRequest toNewCreateRequest(GuardRailPromptCreateReq request) {
        if (request == null) {
            return null;
        }

        return PromptCreateRequest.builder()
                .description(request.getDesc())
                .messages(convertMessages(request.getMessages()))
                .name(request.getName())
                .projectId(request.getProjectId())
                .tags(convertTags(request.getTags()))
                .release(true)
                .variables(List.of())
                .ptype(2) // GuardRail prompts always have ptype = 2
                .build();
    }

    /**
     * GuardRailPromptUpdateReq를 SKTAI API 요청 형식으로 변환
     */
    default PromptUpdateRequest toNewUpdateRequest(String promptUuid, GuardRailPromptUpdateReq request) {
        if (request == null) {
            return null;
        }

        return PromptUpdateRequest.builder()
                .description(request.getDesc())
                .messages(convertMessagesForUpdate(request.getMessages()))
                .newName(request.getNewName())
                .tags(convertTagsForUpdate(request.getTags()))
                .variables(List.of())
                .release(request.isRelease())
                .ptype(2) // GuardRail prompts always have ptype = 2
                .build();
    }

    /**
     * SKTAI API 응답을 GuardRailPromptCreateRes로 변환
     */
    default GuardRailPromptCreateRes toNewCreateResponse(PromptCreateResponse response) {
        if (response == null) {
            return null;
        }

        return GuardRailPromptCreateRes.builder()
                .promptUuid(response.getPromptUuid())
                .build();
    }

    // ================= Helper Methods =================

    private List<PromptCreateRequest.PromptMessage> convertMessages(List<GuardRailPromptCreateReq.Message> messages) {
        if (messages == null) {
            return null;
        }

        // system 메시지(mtype=1)가 하나만 있는지 확인
        long systemMessageCount = messages.stream()
                .filter(Objects::nonNull)
                .filter(msg -> msg.getMtype() == 1)
                .count();

        if (systemMessageCount > 1) {
            // system 메시지가 여러 개인 경우, 첫 번째 것만 유지하고 나머지는 제거
            java.util.concurrent.atomic.AtomicBoolean firstSystemFound =
                    new java.util.concurrent.atomic.AtomicBoolean(false);
            return messages.stream()
                    .filter(Objects::nonNull)
                    .filter(msg -> {
                        if (msg.getMtype() == 1) {
                            if (firstSystemFound.compareAndSet(false, true)) {
                                return true; // 첫 번째 system 메시지만 유지
                            }
                            return false; // 나머지 system 메시지는 제거
                        }
                        return true; // system이 아닌 메시지는 모두 유지
                    })
                    .map(msg -> PromptCreateRequest.PromptMessage.builder()
                            .message(msg.getMessage())
                            .mtype(msg.getMtype())
                            .build())
                    .toList();
        }

        return messages.stream()
                .map(msg -> PromptCreateRequest.PromptMessage.builder()
                        .message(msg.getMessage())
                        .mtype(msg.getMtype())
                        .build())
                .toList();
    }

    private List<PromptCreateRequest.PromptTag> convertTags(List<GuardRailPromptCreateReq.Tag> tags) {
        if (tags == null) {
            return null;
        }

        return tags.stream()
                .map(tag -> PromptCreateRequest.PromptTag.builder()
                        .tag(tag.getTag())
                        .build())
                .toList();
    }

    private List<PromptCreateRequest.PromptVariable> convertVariables(
            List<GuardRailPromptCreateReq.Variable> variables) {
        if (variables == null) {
            return null;
        }

        return variables.stream()
                .map(var -> PromptCreateRequest.PromptVariable.builder()
                        .validation(var.getValidation())
                        .variable(var.getVariable())
                        .build())
                .toList();
    }

    // ================= Update Helper Methods =================

    private List<PromptUpdateRequest.PromptMessage> convertMessagesForUpdate(
            List<GuardRailPromptUpdateReq.Message> messages) {
        if (messages == null) {
            return null;
        }

        // system 메시지(mtype=1)가 하나만 있는지 확인
        long systemMessageCount = messages.stream()
                .filter(Objects::nonNull)
                .filter(msg -> msg.getMtype() == 1)
                .count();

        if (systemMessageCount > 1) {
            // system 메시지가 여러 개인 경우, 첫 번째 것만 유지하고 나머지는 제거
            java.util.concurrent.atomic.AtomicBoolean firstSystemFound =
                    new java.util.concurrent.atomic.AtomicBoolean(false);
            return messages.stream()
                    .filter(Objects::nonNull)
                    .filter(msg -> {
                        if (msg.getMtype() == 1) {
                            if (firstSystemFound.compareAndSet(false, true)) {
                                return true; // 첫 번째 system 메시지만 유지
                            }
                            return false; // 나머지 system 메시지는 제거
                        }
                        return true; // system이 아닌 메시지는 모두 유지
                    })
                    .map(msg -> PromptUpdateRequest.PromptMessage.builder()
                            .message(msg.getMessage())
                            .mtype(msg.getMtype())
                            .build())
                    .toList();
        }

        return messages.stream()
                .map(msg -> PromptUpdateRequest.PromptMessage.builder()
                        .message(msg.getMessage())
                        .mtype(msg.getMtype())
                        .build())
                .toList();
    }

    private List<PromptUpdateRequest.PromptTag> convertTagsForUpdate(List<GuardRailPromptUpdateReq.Tag> tags) {
        if (tags == null) {
            return null;
        }

        return tags.stream()
                .map(tag -> PromptUpdateRequest.PromptTag.builder()
                        .tag(tag.getTag())
                        .build())
                .toList();
    }

    private List<PromptUpdateRequest.PromptVariable> convertVariablesForUpdate(
            List<GuardRailPromptUpdateReq.Variable> variables) {
        if (variables == null) {
            return null;
        }

        return variables.stream()
                .map(var -> PromptUpdateRequest.PromptVariable.builder()
                        .validation(var.getValidation())
                        .variable(var.getVariable())
                        .build())
                .toList();
    }

    // ================= Builtin Prompt 매핑 =================

    /**
     * JSON 응답을 직접 GuardRailPromptBuiltinRes로 변환
     * 실제 API 응답 구조에 맞게 수정
     */
    default GuardRailPromptBuiltinRes fromBuiltinResponse(Object response) {
        // 실제 API 응답 구조에 따라 수정 필요
        // 현재는 JSON 응답 구조를 기반으로 함
        return null;
    }

    private Logger log() {
        return org.slf4j.LoggerFactory.getLogger(getClass());
    }

    /* ================= 목록 요약 매핑 ================= */
    default GuardRailPromptRes from(PromptsResponse.PromptSummary s) {
        if (s == null) {
            return null;
        }

        return GuardRailPromptRes.builder()
                .uuid(nullIfBlank(s.getUuid()))
                .name(trimToNull(s.getName()))
                .createdAt(toKstString(OffsetDateTime.parse(s.getCreatedAt()).toLocalDateTime()))
                .updatedAt(toKstStringFromIso(s.getUpdatedAt()))
                .releaseVersion(nullIfBlank(s.getReleaseVersion()))
                .latestVersion(nullIfBlank(s.getLatestVersion()))
                .ptype(nullIfBlank(s.getPtype()))
                .tags(convertTagsToTagInfo(s.getTags()))
                .build();
    }

    /* ================= 목록 매핑 (태그 검색) ================= */
    default GuardRailPromptRes from(PromptFilterByTagsResponse.Item s) {
        if (s == null) {
            return null;
        }
        return GuardRailPromptRes.builder()
                .uuid(s.getUuid())
                .name(s.getName())
                .createdAt(toKstString(s.getCreatedAt()))
                .updatedAt(null)
                .releaseVersion(s.getReleaseVersion() != null ? String.valueOf(s.getReleaseVersion()) : null)
                .latestVersion(null)
                .ptype(s.getPtype() != null ? String.valueOf(s.getPtype()) : null)
                .tags(convertTagListToTagInfo(s.getTagList()))
                .build();
    }

    /* ================= 단건 상세 매핑 ================= */
    default GuardRailPromptByIdRes from(PromptResponse r) {
        if (r == null || r.getData() == null) {
            return GuardRailPromptByIdRes.builder().build();
        }
        var d = r.getData();
        return GuardRailPromptByIdRes.builder()
                .uuid(nullIfBlank(d.getUuid()))
                .name(trimToNull(d.getName()))
                .description(trimToNull(d.getDescription()))
                .projectId(nullIfBlank(d.getProjectId()))
                .createdAt(toKstString(OffsetDateTime.parse(d.getCreatedAt()).toLocalDateTime()))
                .updatedAt(d.getUpdatedAt() != null ?
                        toKstString(OffsetDateTime.parse(d.getUpdatedAt()).toLocalDateTime()) : "")
                .ptype(asInt(d.getPtype()))
                .deleteFlag(asBool(d.getDeleteFlag()))
                .releaseVersion(asInt(d.getReleaseVersion()))
                .tags(convertTagsToTagInfo(d.getTags()))
                .build();
    }

    /* ================= 버전 목록 매핑 ================= */
    default GuardRailPromptVerListByIdRes from(PromptVersionsResponse r) {
        if (r == null || r.getData() == null || r.getData().isEmpty()) {
            return GuardRailPromptVerListByIdRes.builder().build();
        }
        var list = r.getData();

        // 첫 번째 유효 uuid 사용 (모든 항목 동일 UUID 가정)
        String promptUuid = list.stream()
                .filter(Objects::nonNull)
                .map(PromptVersionsResponse.VersionData::getUuid)
                .filter(s -> s != null && !s.isBlank())
                .findFirst()
                .orElse(null);

        List<GuardRailPromptVerListByIdRes.VersionItem> items = list.stream()
                .filter(Objects::nonNull)
                .map(v -> GuardRailPromptVerListByIdRes.VersionItem.builder()
                        .versionUuid(nullIfBlank(v.getVersionId()))
                        .version(v.getVersion())
                        .createdAt(DateUtils.toDateTimeString(v.getCreatedAt()))
                        .release(Boolean.TRUE.equals(v.getRelease()))
                        .deleteFlag(Boolean.TRUE.equals(v.getDeleteFlag()))
                        .createdBy(trimToNull(v.getCreatedBy()))
                        .build())
                .toList();

        return GuardRailPromptVerListByIdRes.builder()
                .promptUuid(promptUuid)
                .totalVersions(items.size())
                .versions(items)
                .build();
    }

    /* ================= 최신 버전 단건 매핑 ================= */
    default GuardRailPromptLatestByIdRes from(PromptVersionResponse r) {
        if (r == null || r.getData() == null) {
            return GuardRailPromptLatestByIdRes.builder().build();
        }
        var d = r.getData();
        return GuardRailPromptLatestByIdRes.builder()
                .promptUuid(nullIfBlank(d.getUuid()))
                .versionUuid(nullIfBlank(d.getVersionId()))
                .version(d.getVersion())
                .createdAt(nullIfBlank(d.getCreatedAt()))
                .release(Boolean.TRUE.equals(d.getRelease()))
                .deleteFlag(Boolean.TRUE.equals(d.getDeleteFlag()))
                .createdBy(trimToNull(d.getCreatedBy()))
                .build();
    }

    /* ============ 프롬프트 메시지 조회 (특정 버전 조건) ========= */
    default GuardRailPromptMsgsByIdRes from(PromptMessagesResponse r) {
        if (r == null || r.getData() == null || r.getData().isEmpty()) {
            return GuardRailPromptMsgsByIdRes.builder().build();
        }

        // data 배열 가져오기
        List<PromptMessagesResponse.PromptMessage> rows = r.getData();

        // Version ID 체크
        warnIfMixedVersionIds(
                rows.stream().map(PromptMessagesResponse.PromptMessage::getVersionId).toList()
        );

        // 버전 UUID: 첫 원소의 version_id 사용 (혼재 가능성 거의 없다고 가정)
        String versionUuid = firstNonNull(rows, PromptMessagesResponse.PromptMessage::getVersionId);

        // sequence 오름차순 정렬 후 화면 모델로 변환
        List<GuardRailPromptMsgsByIdRes.MessageItem> messages = rows.stream()
                .filter(Objects::nonNull)
                .sorted(
                        Comparator
                                .comparing(PromptMessagesResponse.PromptMessage::getSequence,
                                        Comparator.nullsLast(Integer::compareTo))
                                .thenComparing(pm -> Objects.toString(pm.getMessage(), ""))
                )
                .map(pm -> GuardRailPromptMsgsByIdRes.MessageItem.builder()
                        .messageId(pm.getVersionId() != null && pm.getSequence() != null ?
                                pm.getVersionId() + ":" + pm.getSequence() : null)
                        .mtype(pm.getMtype())
                        .message(pm.getMessage())
                        .order(pm.getSequence())
                        .build()
                )
                .toList();

        return GuardRailPromptMsgsByIdRes.builder()
                .versionUuid(versionUuid)
                .messages(messages)
                .build();
    }


    /* ============ 프롬프트 특정 버전 변수 조회 ========= */
    default GuardRailPromptVarsByIdRes from(PromptVariablesResponse r) {
        if (r == null || r.getData() == null || r.getData().isEmpty()) {
            return GuardRailPromptVarsByIdRes.builder().build();
        }

        // 원소들
        List<PromptVariablesResponse.PromptVariableItem> rows = r.getData();

        warnIfMixedVersionIds(
                rows.stream().map(PromptVariablesResponse.PromptVariableItem::getVersionId).toList()
        );

        // 버전 UUID: 첫 번째 non-null version_id 사용
        String versionUuid = firstNonNull(rows, PromptVariablesResponse.PromptVariableItem::getVersionId);

        // 변수
        List<GuardRailPromptVarsByIdRes.VariableItem> variables = rows.stream()
                .filter(Objects::nonNull)
                .map(v -> GuardRailPromptVarsByIdRes.VariableItem.builder()
                        .variableId(v.getVariableUuid())
                        .variable(v.getVariable())
                        .validation(v.getValidation())
                        .validationFlag(v.getValidationFlag())
                        .tokenLimitFlag(v.getTokenLimitFlag())
                        .tokenLimit(v.getTokenLimit())
                        .build()
                )
                .toList();


        return GuardRailPromptVarsByIdRes.builder()
                .versionUuid(versionUuid)
                .variables(variables)
                .build();
    }

    /* ============ 프롬프트 특정 버전 태그 조회 ========= */
    default GuardRailPromptTagsListByIdRes from(PromptTagsResponse r) {
        if (r == null || r.getData() == null || r.getData().isEmpty()) {
            return GuardRailPromptTagsListByIdRes.builder().build();
        }

        List<PromptTagsResponse.PromptTag> rows = r.getData();

        warnIfMixedVersionIds(
                rows.stream().map(PromptTagsResponse.PromptTag::getVersionId).toList()
        );

        // 버전 UUID: 첫 번째 non-null version_id 사용
        String versionUuid = firstNonNull(rows, PromptTagsResponse.PromptTag::getVersionId);

        // 태그 목록 매핑
        List<GuardRailPromptTagsListByIdRes.PromptTag> tags = rows.stream()
                .filter(Objects::nonNull)
                .map(t -> GuardRailPromptTagsListByIdRes.PromptTag.builder()
                        .tagId(t.getTagUuid())
                        .tag(t.getTag())
                        .build()
                )
                .toList();


        return GuardRailPromptTagsListByIdRes.builder()
                .versionUuid(versionUuid)
                .tags(tags)
                .build();
    }

    /* ================= Utility Methods ================= */

    /* ================= 태그 정보 변환 ================= */
    private List<GuardRailPromptRes.TagInfo> convertTagsToTagInfo(List<JsonNode> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }

        List<GuardRailPromptRes.TagInfo> out = new ArrayList<>(tags.size());
        for (JsonNode n : tags) {
            if (n == null || n.isNull()) {
                continue;
            }

            String tagValue = n.hasNonNull("tag") ? n.get("tag").asText() : null;
            String versionId = n.hasNonNull("version_id") ? n.get("version_id").asText() : null;

            if (tagValue != null && !tagValue.trim().isEmpty()) {
                out.add(GuardRailPromptRes.TagInfo.builder()
                        .tag(tagValue.trim())
                        .versionId(versionId)
                        .build());
            }
        }
        return out;
    }


    /* ================= 태그 문자열 리스트를 TagInfo로 변환 ================= */
    private List<GuardRailPromptRes.TagInfo> convertTagListToTagInfo(List<String> tagList) {
        if (tagList == null || tagList.isEmpty()) {
            return List.of();
        }

        return tagList.stream()
                .filter(tag -> tag != null && !tag.trim().isEmpty())
                .map(tag -> GuardRailPromptRes.TagInfo.builder()
                        .tag(tag.trim())
                        .versionId(null)  // 태그 검색 API는 versionId 정보 없음
                        .build())
                .toList();
    }

    private <T, R> R firstNonNull(
            List<T> rows,
            Function<? super T, ? extends R> getter
    ) {
        if (rows == null || rows.isEmpty()) {
            return null;
        }
        for (T t : rows) {
            if (t == null) {
                continue;
            }
            R v = getter.apply(t);
            if (v != null) {
                return v;
            }
        }
        return null;
    }

    private String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    default String toKstString(LocalDateTime utcDateTime) {
        if (utcDateTime == null) {
            return null;
        }
        return DateUtils.toDateTimeString(utcDateTime.plusHours(9));
    }

    default String toKstStringFromIso(String isoDateTime) {
        if (!StringUtils.hasText(isoDateTime)) {
            return null;
        }
        try {
            Instant instant = Instant.parse(isoDateTime);
            return DateUtils.toDateTimeString(LocalDateTime.ofInstant(instant, ZoneOffset.UTC).plusHours(9));
        } catch (DateTimeParseException e) {
            try {
                OffsetDateTime offsetDateTime = OffsetDateTime.parse(isoDateTime);
                return DateUtils.toDateTimeString(offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime().plusHours(9));
            } catch (DateTimeParseException ignored) {
                return isoDateTime;
            }
        }
    }

    private Integer asInt(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number n) {
            return n.intValue();
        }
        String s = String.valueOf(raw).trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException ignore) {
            return null;
        }
    }

    private Boolean asBool(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof Boolean b) {
            return b;
        }
        String s = String.valueOf(raw).trim().toLowerCase();
        if (s.isEmpty()) {
            return null;
        }
        return switch (s) {
            case "true", "1", "y", "yes", "t" -> Boolean.TRUE;
            case "false", "0", "n", "no", "f" -> Boolean.FALSE;
            default -> null;
        };
    }

    /**
     * 응답 data 배열 안에 서로 다른 version_id가 혼재되어 있는지 감지하여 경고 로그를 남깁니다.
     *
     * <p>정상 응답이라면 배열의 모든 원소가 동일한 version_id를 가져야 합니다.
     * 서로 다른 값이 섞여 있으면 백엔드 조합/조인 이상이거나 캐시/라우팅 문제일 가능성이 높습니다.</p>
     *
     * <p>성능:
     * 입력 크기가 아주 큰 경우를 대비해 stream distinct 후 count만 계산합니다.
     * 로그는 warn 레벨이므로 운영 환경에서는 주기적으로만 발생해야 하며,
     * 과도한 로그 발생 시 백엔드 팀과 협의하여 원인 제거가 우선입니다.</p>
     *
     * @param versionIds data 배열에서 추출한 version_id 목록(null 포함 가능)
     */
    private void warnIfMixedVersionIds(List<String> versionIds) {
        if (versionIds == null || versionIds.isEmpty()) {
            return;
        }

        String first = null;
        for (String v : versionIds) {
            if (v == null) {
                continue;
            }
            if (first == null) {
                first = v;
            } else if (!first.equals(v)) {
                log().warn("Mixed version_id in response: {}", versionIds);
                return;
            }
        }
    }

}

