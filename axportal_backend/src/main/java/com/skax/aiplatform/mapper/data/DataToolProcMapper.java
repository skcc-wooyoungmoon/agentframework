package com.skax.aiplatform.mapper.data;

import com.skax.aiplatform.client.sktai.data.dto.response.*;
import com.skax.aiplatform.dto.data.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Builder;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true) )
public interface DataToolProcMapper {
    @Mapping(target = "id",          source = "id")
    @Mapping(target = "name",        expression = "java(trimToNull(s.getName()))")
    @Mapping(target = "description", expression = "java(trimToNull(s.getDescription()))")
    @Mapping(target = "type",        expression = "java(trimToNull(s.getType()))")
    @Mapping(target = "dataType",    expression = "java(trimToNull(s.getDataType()))")
    @Mapping(target = "rulePattern", expression = "java(trimToNull(s.getRulePattern()))")
    @Mapping(target = "ruleValue",   expression = "java(trimToNull(s.getRuleValue()))")
    @Mapping(target = "code",        expression = "java(trimToNull(s.getCode()))")
    @Mapping(target = "defaultKey",  expression = "java(trimToNull(s.getDefaultKey()))")
    @Mapping(target = "projectId",   expression = "java(trimToNull(s.getProjectId()))")
    @Mapping(target = "createdBy",   expression = "java(trimToNull(s.getCreatedBy()))")
    @Mapping(target = "updatedBy",   expression = "java(trimToNull(s.getUpdatedBy()))")
    @Mapping(target = "createdAt",   source = "createdAt")
    @Mapping(target = "updatedAt",   source = "updatedAt")
    DataToolProcRes from(Processor s);

    default DataToolProcDetailRes from(ProcessorDetail r) {
        if (r == null) return null;
        return DataToolProcDetailRes.builder()
                .id(r.getId())
                .name(trimToNull(r.getName()))
                .description(trimToNull(r.getDescription()))
                .type(trimToNull(r.getType()))
                .dataType(trimToNull(r.getDataType()))
                .rulePattern(trimToNull(r.getRulePattern()))
                .ruleValue(trimToNull(r.getRuleValue()))
                .code(trimToNull(r.getCode()))
                .defaultKey(trimToNull(r.getDefaultKey()))
                .projectId(trimToNull(r.getProjectId()))
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .createdBy(trimToNull(r.getCreatedBy()))
                .updatedBy(trimToNull(r.getUpdatedBy()))
                .build();
    }

    default String trimToNull(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }
}