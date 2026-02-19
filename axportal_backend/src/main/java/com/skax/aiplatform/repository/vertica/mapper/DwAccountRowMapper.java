package com.skax.aiplatform.repository.vertica.mapper;

import com.skax.aiplatform.dto.vertica.response.DwAccountListRes;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Vertica DW Account Mapper
 * 
 * <p>DM_BASE.dwa_queryone_user 테이블에 대한 mapper 입니다.</p>
 */

public class DwAccountRowMapper implements RowMapper<DwAccountListRes> {
    @Override
    public DwAccountListRes mapRow(ResultSet rs, int rowNum) throws SQLException {
        return DwAccountListRes.builder()
                .userName(rs.getString("user_name"))
                .empNo(rs.getString("emp_no"))
                .groupName(rs.getString("group_name"))
                .deptCd(rs.getString("dept_cd"))
                .validStartDate(rs.getString("valid_start_date"))
                .validEndDate(rs.getString("valid_end_date"))
                .dbAccountId(rs.getString("db_account_id"))
                .dbName(rs.getString("db_name"))
                .dbType(rs.getString("db_type"))
                .ipAddr(rs.getString("ip_addr"))
                .dwDataGjdt(rs.getString("dw_data_gjdt"))
                .dwLstJukjaDt(rs.getString("dw_lst_jukja_dt"))
                .accountStatus(rs.getString("accountStatus"))
                .build();
    }
}