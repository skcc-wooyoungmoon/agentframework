package com.skax.aiplatform.repository.home;

import com.skax.aiplatform.entity.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GpoRolesRepository extends JpaRepository<Role, Long> {
    boolean existsByRoleSeq(Long roleSeq);

    @Modifying
    @Query(value = "INSERT INTO GPO_ROLES_MAS (ROLE_SEQ, PRJ_SEQ, UUID, RIGHT_SCOP_CTNT, GPO_ROLE_NM, DTL_CTNT, STATUS_NM, ROLE_TYPE, FST_CREATED_AT, LST_UPDATED_AT, CREATED_BY, UPDATED_BY) " +
            "VALUES (:roleSeq, :prjSeq, :uuid, :scope, :roleNm, :dtlCtnt, :statusNm, :roleType, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, :createdBy, :updatedBy)", nativeQuery = true)
    void insertRoleWithSeq(@Param("roleSeq") Long roleSeq,
                           @Param("prjSeq") Long prjSeq,
                           @Param("uuid") String uuid,
                           @Param("scope") String scope,
                           @Param("roleNm") String roleNm,
                           @Param("dtlCtnt") String dtlCtnt,
                           @Param("statusNm") String statusNm,
                           @Param("roleType") String roleType,
                           @Param("createdBy") String createdBy,
                           @Param("updatedBy") String updatedBy);
}
