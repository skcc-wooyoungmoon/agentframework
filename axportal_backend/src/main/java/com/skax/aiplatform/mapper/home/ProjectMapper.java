package com.skax.aiplatform.mapper.home;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.skax.aiplatform.dto.common.response.MemberProjInfo;
import com.skax.aiplatform.dto.home.response.ProjDetailRes;
import com.skax.aiplatform.dto.home.response.ProjPrivateRes;
import com.skax.aiplatform.dto.home.response.ProjUserRes;
import com.skax.aiplatform.dto.home.response.ProjectRes;

@Mapper
public interface ProjectMapper {
    // 사용자가 참여 중인 프로젝트 목록 조회
    List<ProjectRes> findJoinProjectList(@Param("username") String username);

    List<ProjPrivateRes> findJoinPrivateProjectList(@Param("username") String username);

    List<ProjPrivateRes> findNotJoinProjectList(Map<String, Object> params);

    List<ProjUserRes> findProjectUserList(Map<String, Object> params);

    List<ProjDetailRes> findNotJoinProjectDetail(Long projectId);

    MemberProjInfo findProjectUserInfo(Map<String, Object> params);

}
