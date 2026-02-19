package com.skax.aiplatform.mapper.admin;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.skax.aiplatform.dto.admin.request.UserUsageMgmtReq;
import com.skax.aiplatform.dto.admin.response.UserUsageMgmtRes;
import com.skax.aiplatform.entity.UserUsageMgmt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserUsageMgmtMapper {
    /**
     * UserUsageMgmtReq를 UserUsageMgmt로 변환
     */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userName", source = "userName")
    @Mapping(target = "projectName", source = "projectName")
    @Mapping(target = "roleName", source = "roleName")
    @Mapping(target = "menuPath", source = "menuPath")
    @Mapping(target = "action", source = "action")
    @Mapping(target = "targetAsset", source = "targetAsset")
    @Mapping(target = "resourceType", source = "resourceType")
    @Mapping(target = "apiEndpoint", source = "apiEndpoint")
    @Mapping(target = "errCode", source = "errCode")
    @Mapping(target = "clientIp", source = "clientIp")
    @Mapping(target = "userAgent", source = "userAgent")
    @Mapping(target = "requestContent", source = "requestContent")
    @Mapping(target = "firstRequestDetail", source = "firstRequestDetail")
    @Mapping(target = "secondRequestDetail", source = "secondRequestDetail")
    @Mapping(target = "thirdRequestDetail", source = "thirdRequestDetail")
    @Mapping(target = "fourthRequestDetail", source = "fourthRequestDetail")
    @Mapping(target = "responseContent", source = "responseContent")
    @Mapping(target = "firstResponseDetail", source = "firstResponseDetail")
    @Mapping(target = "secondResponseDetail", source = "secondResponseDetail")
    @Mapping(target = "thirdResponseDetail", source = "thirdResponseDetail")
    @Mapping(target = "fourthResponseDetail", source = "fourthResponseDetail")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "createdBy", source = "createdBy")
    UserUsageMgmt toEntity(UserUsageMgmtReq userUsageMgmtReq);
 

    /**
     * UserUsageMgmt를 UserUsageMgmtRes로 변환
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "userName", source = "userName")
    @Mapping(target = "projectName", source = "projectName")
    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "roleName", source = "roleName")
    @Mapping(target = "menuPath", source = "menuPath")
    @Mapping(target = "action", source = "action")
    @Mapping(target = "targetAsset", source = "targetAsset")
    @Mapping(target = "resourceType", source = "resourceType")
    @Mapping(target = "apiEndpoint", source = "apiEndpoint")
    @Mapping(target = "errCode", source = "errCode")
    @Mapping(target = "clientIp", source = "clientIp")
    @Mapping(target = "userAgent", source = "userAgent")
    @Mapping(target = "requestContent", source = "requestContent")
    @Mapping(target = "responseContent", source = "responseContent")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "createdBy", source = "createdBy")
    UserUsageMgmtRes toResponse(UserUsageMgmt userUsageMgmt);

    /**
     * LocalDateTime을 String으로 변환
     * 
     * @param dateTime LocalDateTime
     * @return 포맷팅된 날짜 문자열
     */
    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


}
