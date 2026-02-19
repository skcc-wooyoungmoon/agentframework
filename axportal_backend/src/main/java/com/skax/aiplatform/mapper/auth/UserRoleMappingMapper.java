package com.skax.aiplatform.mapper.auth;

import com.skax.aiplatform.client.sktai.auth.dto.request.RoleMappingProjectDto;
import com.skax.aiplatform.client.sktai.auth.dto.request.RoleMappingRoleDto;
import com.skax.aiplatform.client.sktai.auth.dto.request.UserRoleMappingUpdateItemDto;
import com.skax.aiplatform.client.sktai.auth.dto.response.RoleAvailablePageResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper to convert GET /users/{id}/role-available items
 * into PUT /users/{id}/role-mappings request items.
 */
@Mapper(componentModel = "spring")
public interface UserRoleMappingMapper {

    UserRoleMappingMapper INSTANCE = Mappers.getMapper(UserRoleMappingMapper.class);

    // Single item mapping
    UserRoleMappingUpdateItemDto toUpdateItem(RoleAvailablePageResponseDto.Item item);

    // Nested type mappings
    RoleMappingProjectDto toProject(RoleAvailablePageResponseDto.Project project);

    RoleMappingRoleDto toRole(RoleAvailablePageResponseDto.Role role);

    // Collection mapping
    List<UserRoleMappingUpdateItemDto> toUpdateItems(List<RoleAvailablePageResponseDto.Item> items);
}
