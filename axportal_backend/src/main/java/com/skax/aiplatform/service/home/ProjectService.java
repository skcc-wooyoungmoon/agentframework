package com.skax.aiplatform.service.home;

import com.skax.aiplatform.dto.home.request.ProjBaseInfoCreateReq;
import com.skax.aiplatform.dto.home.request.ProjInfoReq;
import com.skax.aiplatform.dto.home.request.ProjJoinReq;
import com.skax.aiplatform.dto.home.request.ProjectCreateReq;
import com.skax.aiplatform.dto.home.response.ProjDetailRes;
import com.skax.aiplatform.dto.home.response.ProjPrivateRes;
import com.skax.aiplatform.dto.home.response.ProjUserRes;
import com.skax.aiplatform.dto.home.response.ProjectRes;

import java.util.List;

public interface ProjectService {

    List<ProjectRes> getJoinProjectList(String username);

    List<ProjPrivateRes> getJoinPrivateProjectList(String username);

    List<ProjPrivateRes> getNotJoinProjectList(String username, String condition, String keyword);

    List<ProjDetailRes> getNotJoinProjectDetail(String projectId);

    List<ProjUserRes> getProjectUserList(String username, String name, String department);

    ProjBaseInfoCreateReq createProject(ProjBaseInfoCreateReq projBaseInfoCreateReq);

    void deleteProject(long prjSeq);

    void createProjectAfterProcess(boolean isApproval, ProjectCreateReq projectCreateReq);

    String joinProject(ProjInfoReq projJoinReq);

    void joinProjectAfterProcess(boolean isApproval, ProjJoinReq projJoinReq);

    int quitProject(ProjInfoReq projQuitReq);

    ProjUserRes getProjectUserInfo(String memberId);

}
