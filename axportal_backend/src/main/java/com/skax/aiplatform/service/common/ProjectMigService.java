package com.skax.aiplatform.service.common;

public interface ProjectMigService {

    /**
     * 프로젝트 정보 Export
     * 특정 프로젝트의 정보를 파일로 저장 (Append/Update)
     */
    void exportProject(Long prjSeq);

    /**
     * 프로젝트 정보 Import
     * 파일의 정보를 기반으로 DB 동기화 (Insert/Update/Delete)
     */
    void importProjects();
}
