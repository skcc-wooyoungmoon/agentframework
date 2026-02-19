package com.skax.aiplatform.mapper.common;

import com.skax.aiplatform.client.sktai.model.dto.request.ModelImportRequest;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelExportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 마이그레이션 매퍼
 * 
 * <p>마이그레이션 관련 DTO 변환을 담당하는 MapStruct 매퍼입니다.
 * ModelExportResponse를 ModelImportRequest로 변환합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-11-11
 * @version 1.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MigMapper {
    
    /**
     * ModelExportResponse를 ModelImportRequest로 변환
     * 
     * @param exportResponse Export 응답
     * @return Import 요청
     */
    ModelImportRequest toImportRequest(ModelExportResponse exportResponse);
    
    /**
     * ExportModel을 ImportModel로 변환
     */
    ModelImportRequest.ImportModel toImportModel(ModelExportResponse.ExportModel exportModel);
    
    /**
     * ProviderInfo 변환
     */
    ModelImportRequest.ProviderInfo toProviderInfo(ModelExportResponse.ProviderInfo exportProvider);
    
    /**
     * LanguageInfo 변환
     */
    ModelImportRequest.LanguageInfo toLanguageInfo(ModelExportResponse.LanguageInfo exportLanguage);
    
    /**
     * LanguageInfo 리스트 변환
     */
    List<ModelImportRequest.LanguageInfo> toLanguageInfoList(List<ModelExportResponse.LanguageInfo> exportLanguages);
    
    /**
     * TaskInfo 변환
     */
    ModelImportRequest.TaskInfo toTaskInfo(ModelExportResponse.TaskInfo exportTask);
    
    /**
     * TaskInfo 리스트 변환
     */
    List<ModelImportRequest.TaskInfo> toTaskInfoList(List<ModelExportResponse.TaskInfo> exportTasks);
    
    /**
     * TagInfo 변환
     */
    ModelImportRequest.TagInfo toTagInfo(ModelExportResponse.TagInfo exportTag);
    
    /**
     * TagInfo 리스트 변환
     */
    List<ModelImportRequest.TagInfo> toTagInfoList(List<ModelExportResponse.TagInfo> exportTags);
    
    /**
     * CustomRuntimeInfo 변환
     */
    ModelImportRequest.CustomRuntimeInfo toCustomRuntimeInfo(ModelExportResponse.CustomRuntimeInfo exportRuntime);
    
    /**
     * CustomRuntimeInfo 리스트 변환
     */
    List<ModelImportRequest.CustomRuntimeInfo> toCustomRuntimeInfoList(List<ModelExportResponse.CustomRuntimeInfo> exportRuntimes);
}

