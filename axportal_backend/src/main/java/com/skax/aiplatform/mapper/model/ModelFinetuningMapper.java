package com.skax.aiplatform.mapper.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.ReportingPolicy;

import com.skax.aiplatform.client.sktai.finetuning.dto.request.TrainingUpdate;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingRead;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingStatusRead;
import com.skax.aiplatform.dto.model.request.ModelFineTuningUpdateReq;
import com.skax.aiplatform.dto.model.response.ModelFineTuningCreateRes;
import com.skax.aiplatform.dto.model.response.ModelFineTuningStatusRes;
import com.skax.aiplatform.dto.model.response.ModelFineTuningTrainingRes;
import com.skax.aiplatform.dto.model.response.ModelFineTuningTrainingsRes;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModelFinetuningMapper {

    /**
     * SKTAI TrainingRead를 FineTuningTrainingRes로 변환
     * 
     * @param request SKTAI 응답
     * @return  응답 DTO
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "prevStatus", source = "prevStatus")
    @Mapping(target = "progress", source = "progress")
    @Mapping(target = "resource", source = "resource")
    @Mapping(target = "datasetIds", source = "datasetIds")
    @Mapping(target = "baseModelId", source = "baseModelId")
    @Mapping(target = "params", source = "params")
    @Mapping(target = "envs", source = "envs")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "taskId", source = "taskId")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "trainerId", source = "trainerId")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ModelFineTuningTrainingsRes toResponse(TrainingRead trainingRead);


    /**
     * SKTAI TrainingRead를 FineTuningTrainingRes 변환
     * 
     * @param request SKTAI 응답
     * @return  응답 DTO
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "prevStatus", source = "prevStatus")
    @Mapping(target = "progress", source = "progress")
    @Mapping(target = "resource", source = "resource")
    @Mapping(target = "datasetIds", source = "datasetIds")
    @Mapping(target = "baseModelId", source = "baseModelId")
    @Mapping(target = "params", source = "params")
    @Mapping(target = "envs", source = "envs")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "taskId", source = "taskId")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "trainerId", source = "trainerId")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    ModelFineTuningTrainingRes toDetailResponse(TrainingRead trainingRead);


        /**
     * SKTAI TrainingRead를 FineTuningTrainingRes 변환
     * 
     * @param request SKTAI 응답
     * @return  응답 DTO
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "prevStatus", source = "prevStatus")
    @Mapping(target = "progress", source = "progress")
    @Mapping(target = "resource", source = "resource")
    @Mapping(target = "datasetIds", source = "datasetIds")
    @Mapping(target = "baseModelId", source = "baseModelId")
    @Mapping(target = "params", source = "params")
    @Mapping(target = "envs", source = "envs")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "taskId", source = "taskId")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "trainerId", source = "trainerId")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ModelFineTuningCreateRes toCreateResponse(TrainingRead trainingRead);

    

    /**
     * FineTuningUpdateReq를 SKTAI TrainingUpdate로 변환
     * 
     * @param request 생성 요청 DTO
     * @return SKTAI 요청 DTO
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "prevStatus", source = "prevStatus")
    @Mapping(target = "progress", source = "progress")
    @Mapping(target = "resource", source = "resource")
    @Mapping(target = "datasetIds", source = "datasetIds")
    @Mapping(target = "baseModelId", source = "baseModelId")
    @Mapping(target = "params", source = "params")
    @Mapping(target = "envs", source = "envs")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "taskId", source = "taskId")
    @Mapping(target = "trainerId", source = "trainerId")
    TrainingUpdate toSktaiUpdateTrainingRequest(ModelFineTuningUpdateReq request);
    

    /**
     * SKTAI TrainingStatusRead를 FineTuningStatusRes로 변환
     * 
     * @param request SKTAI 응답
     * @return  응답 DTO
     */
    @Mapping(target = "status", source = "status")
    @Mapping(target = "prevStatus", source = "prevStatus")
    ModelFineTuningStatusRes toTrainingStatusResponse(TrainingStatusRead status);



    // /**
    //  * DatasetTags 리스트를 String 리스트로 변환
    //  * 
    //  * @param tags DatasetTags 리스트
    //  * @return String 리스트
    //  */
    // @Named("mapTagsToStrings")
    // default List<String> mapNameToStrings(List<DatasetTags> tags) {
    //     if (tags == null) {
    //         return null;
    //     }
    //     return tags.stream()
    //             .map(tag -> tag.getName())
    //             .collect(Collectors.toList());
    // }

}
