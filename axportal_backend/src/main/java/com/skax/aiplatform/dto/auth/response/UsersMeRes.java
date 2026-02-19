package com.skax.aiplatform.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersMeRes {
    private List<ProjectInfoRes> projectList;
    private ProjectInfoRes activeProject;
    private UserInfoRes userInfo;
    private AdxpProjectInfoRes adxpProject;
    private List<String> menuAuthList;
    private List<String> functionAuthList;
    private Integer unreadAlarmCount;
}