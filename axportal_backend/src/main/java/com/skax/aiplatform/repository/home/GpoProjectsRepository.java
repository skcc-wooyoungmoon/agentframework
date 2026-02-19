package com.skax.aiplatform.repository.home;

import com.skax.aiplatform.entity.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GpoProjectsRepository extends JpaRepository<Project, Long> {
    boolean existsByPrjSeq(Long prjSeq);

    List<Project> findByPrjNmContainingIgnoreCase(String prjNm);

    @Modifying
    @Query(value = "INSERT INTO GPO_PROJECTS_MAS (PRJ_SEQ, UUID, GPO_PRJ_NM, DTL_CTNT, STATUS_NM, SSTV_INF_INCL_YN, SSTV_INF_INCL_DTL_CTNT, FST_CREATED_AT, LST_UPDATED_AT, CREATED_BY, UPDATED_BY) " +
            "VALUES (:prjSeq, :uuid, :prjNm, :dtlCtnt, :statusNm, :sstvInfInclYn, :sstvInfInclDesc, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, :createdBy, :updatedBy)", nativeQuery = true)
    void insertProjectWithSeq(@Param("prjSeq") Long prjSeq,
                              @Param("uuid") String uuid,
                              @Param("prjNm") String prjNm,
                              @Param("dtlCtnt") String dtlCtnt,
                              @Param("statusNm") String statusNm,
                              @Param("sstvInfInclYn") String sstvInfInclYn,
                              @Param("sstvInfInclDesc") String sstvInfInclDesc,
                              @Param("createdBy") String createdBy,
                              @Param("updatedBy") String updatedBy);

    Project getByPrjSeq(long prjSeq);
}
