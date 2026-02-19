package com.skax.aiplatform.mapper.model;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.skax.aiplatform.client.lablup.api.dto.response.ScanArtifactResponse;
import com.skax.aiplatform.dto.model.common.ModelGardenInfo;
import com.skax.aiplatform.dto.model.request.CreateModelGardenReq;
import com.skax.aiplatform.dto.model.request.UpdateModelGardenReq;
import com.skax.aiplatform.dto.model.response.GetAvailableModelRes;
import com.skax.aiplatform.entity.model.GpoModelDoipMas;
import com.skax.aiplatform.entity.model.GpoUseGnynModelMas;
import com.skax.aiplatform.enums.ModelGardenStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * 모델 가든 관련 엔티티 ↔ DTO 변환 및 요청 데이터로 엔티티 필드 반영을 담당한다.
 */
@Slf4j
@Component
public class ModelGardenMapper {

    private static final String LM = "[ModelGarden:Mapper] ";
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 엔티티 → ModelGardenInfo (목록/상세 응답용)
     */
    public ModelGardenInfo toModelGardenInfo(GpoUseGnynModelMas entity) {
        if (entity == null) {
            return null;
        }
        ModelGardenInfo dto = new ModelGardenInfo();
        dto.setId(entity.getSeqNo().toString());
        dto.setArtifact_id(entity.getDoipInfo().getArtifactId());
        dto.setRevision_id(entity.getDoipInfo().getRevisionId());
        dto.setName(entity.getModelNm());
        dto.setDescription(entity.getDtlCtn());
        dto.setSize(entity.getModelNo());
        if (entity.getPrmtCnt() != null) {
            dto.setParam_size(entity.getPrmtCnt().toString());
        }
        dto.setServing_type(addUnderbar(entity.getDplyTyp()));
        dto.setVersion(entity.getDplyVer());
        dto.setProvider(entity.getSupjCoNm());
        dto.setProviderId(entity.getSupjCoId());
        dto.setType(entity.getModelTyp());
        dto.setLicense(entity.getCertNm());
        dto.setTags(entity.getTagCtnt());
        dto.setLangauges(entity.getPgmDescCtnt());
        dto.setUrl(entity.getCallUrl());
        dto.setIdentifier(entity.getUuid());
        dto.setStatusNm(entity.getDoipInfo().getStatusNm());
        dto.setDoipAt(entity.getDoipInfo().getDoipAt());
        dto.setDoipMn(entity.getDoipInfo().getDoipMn());
        dto.setChkAt(entity.getDoipInfo().getChkAt());
        dto.setChkMn(entity.getDoipInfo().getChkMn());
        dto.setCreated_at(entity.getFstCreatedAt().format(DATETIME_FORMAT));
        dto.setUpdated_at(entity.getLstUpdatedAt().format(DATETIME_FORMAT));
        dto.setCreated_by(entity.getCreatedBy());
        dto.setUpdated_by(entity.getUpdatedBy());
        dto.setDeleted(entity.getDelYn() == 0 ? "N" : "Y");
        return dto;
    }

    /**
     * CreateModelGardenReq → 신규 엔티티 생성용
     */
    public GpoUseGnynModelMas toEntity(CreateModelGardenReq request) {
        if (request == null) {
            return null;
        }
        ModelGardenStatus statusNm = null;
        if (request.getStatusNm() != null) {
            statusNm = ModelGardenStatus.fromCode(request.getStatusNm());
        }
        return GpoUseGnynModelMas.builder()
                .modelNm(request.getName())
                .dtlCtn(request.getDescription())
                .modelNo(request.getSize())
                .prmtCnt(request.getParam_size() != null ? request.getParam_size().longValue() : null)
                .dplyTyp(extractUnderbar(request.getServing_type()))
                .supjCoNm(request.getProvider())
                .supjCoId(request.getProviderId())
                .modelTyp(request.getType())
                .certNm(request.getLicense())
                .tagCtnt(request.getTags())
                .pgmDescCtnt(request.getLangauges())
                .callUrl(request.getUrl())
                .uuid(request.getIdentifier())
                .doipInfo(GpoModelDoipMas.builder()
                        .statusNm(statusNm)
                        .artifactId(request.getArtifact_id() == null || request.getArtifact_id().isEmpty() ? "N/A" : request.getArtifact_id())
                        .revisionId(request.getRevision_id() == null || request.getRevision_id().isEmpty() ? "N/A" : request.getRevision_id())
                        .build())
                .build();
    }

    /**
     * ScanArtifactResponse → GetAvailableModelRes (각 리비전을 개별 아티팩트 항목으로 펼침)
     */
    public GetAvailableModelRes toGetAvailableModelRes(ScanArtifactResponse response) {
        if (response == null) {
            return GetAvailableModelRes.builder().build();
        }
        List<GetAvailableModelRes.Artifact> convertedArtifacts = response.getArtifacts().stream()
                .flatMap(artifact -> artifact.getRevisions().stream()
                        .map(revision -> GetAvailableModelRes.Artifact.builder()
                                .id(artifact.getId())
                                .name(artifact.getName())
                                .type(artifact.getType())
                                .description(artifact.getDescription())
                                .registryId(artifact.getRegistryId())
                                .sourceRegistryId(artifact.getSourceRegistryId())
                                .registryType(artifact.getRegistryType())
                                .sourceRegistryType(artifact.getSourceRegistryType())
                                .scannedAt(artifact.getScannedAt())
                                .updatedAt(artifact.getUpdatedAt())
                                .readonly(artifact.getReadonly())
                                .revision_id(revision.getId())
                                .version(revision.getVersion())
                                .size(revision.getSize())
                                .status(revision.getStatus())
                                .revisionCreatedAt(revision.getCreatedAt())
                                .revisionUpdatedAt(revision.getUpdatedAt())
                                .build()))
                .collect(Collectors.toList());
        log.debug("{}toGetAvailableModelRes artifactCount={}", LM, convertedArtifacts.size());
        return GetAvailableModelRes.builder()
                .artifacts(convertedArtifacts)
                .build();
    }

    /**
     * UpdateModelGardenReq 값으로 기존 엔티티 필드만 반영 (null이 아닌 필드만)
     */
    public void updateFromRequest(GpoUseGnynModelMas entity, UpdateModelGardenReq request) {
        if (entity == null || request == null) {
            return;
        }
        if (request.getName() != null) {
            entity.setModelNm(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDtlCtn(request.getDescription());
        }
        if (request.getParam_size() != null) {
            entity.setPrmtCnt(request.getParam_size().longValue());
        }
        if (request.getProvider() != null) {
            entity.setSupjCoNm(request.getProvider());
        }
        if (request.getProviderId() != null) {
            entity.setSupjCoId(request.getProviderId());
        }
        if (request.getType() != null) {
            entity.setModelTyp(request.getType());
        }
        if (request.getUrl() != null) {
            entity.setCallUrl(request.getUrl());
        }
        if (request.getIdentifier() != null) {
            entity.setUuid(request.getIdentifier());
        }
        if (request.getTags() != null) {
            entity.setTagCtnt(request.getTags());
        }
        if (request.getLangauges() != null) {
            entity.setPgmDescCtnt(request.getLangauges());
        }
        if (request.getLicense() != null) {
            entity.setCertNm(request.getLicense());
        }
        if (request.getStatusNm() != null) {
            entity.getDoipInfo().setStatusNm(ModelGardenStatus.fromCode(request.getStatusNm()));
        }
        if (request.getFistChkDtlCtnt() != null) {
            entity.getDoipInfo().setFistChkDtlCtnt(request.getFistChkDtlCtnt());
        }
        if (request.getSecdChkDtlCtnt() != null) {
            entity.getDoipInfo().setSecdChkDtlCtnt(request.getSecdChkDtlCtnt());
        }
        if (request.getVanbBrDtlCtnt() != null) {
            entity.getDoipInfo().setVanbBrDtlCtnt(request.getVanbBrDtlCtnt());
        }
        if (request.getVanbBrSmryCtnt() != null) {
            entity.getDoipInfo().setVanbBrSmryCtnt(request.getVanbBrSmryCtnt());
        }
        if (request.getFileDivCnt() != null) {
            entity.getDoipInfo().setFileDivCnt(request.getFileDivCnt());
        }
    }

    /**
     * API/저장 시 serving type 변환: self-hosting → slfhosting (DB 저장 제한 대응)
     */
    public String extractUnderbar(String servingType) {
        if (servingType == null) {
            return null;
        }
        if ("self-hosting".equals(servingType)) {
            return "slfhosting";
        }
        return servingType;
    }

    /**
     * 응답 시 serving type 변환: slfhosting → self-hosting (API는 self-hosting으로 노출)
     */
    public String addUnderbar(String dplyTyp) {
        if (dplyTyp == null) {
            return null;
        }
        if ("slfhosting".equals(dplyTyp)) {
            return "self-hosting";
        }
        return dplyTyp;
    }
}
