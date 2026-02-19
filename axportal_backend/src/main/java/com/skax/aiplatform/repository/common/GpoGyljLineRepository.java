package com.skax.aiplatform.repository.common;

import com.skax.aiplatform.dto.common.request.ApprovalLineInfo;
import com.skax.aiplatform.entity.common.approval.GpoGyljLineMas;
import com.skax.aiplatform.entity.common.approval.GpoGyljLineMasId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GpoGyljLineRepository extends JpaRepository<GpoGyljLineMas, GpoGyljLineMasId> {

    @Query("SELECT new com.skax.aiplatform.dto.common.request.ApprovalLineInfo(" +
           "g.id.gyljLineNm, " +
           "CAST(g.id.gyljLineSno AS string), " +
           "r.roleNm) " +
           "FROM GpoGyljLineMas g " +
           "JOIN Role r ON r.roleSeq = g.roleSeq " +
           "WHERE g.id.gyljLineNm = :gyljLineNm")
    List<ApprovalLineInfo> findByGyljLineNm(@Param("gyljLineNm") String gyljLineNm);


}
