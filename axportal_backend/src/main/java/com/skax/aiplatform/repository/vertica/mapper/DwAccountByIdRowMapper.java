package com.skax.aiplatform.repository.vertica.mapper;

import com.skax.aiplatform.dto.vertica.response.DwAccountByIdRes;
import com.skax.aiplatform.dto.vertica.response.DwAccountListRes;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Vertica DW Account Mapper
 * 
 * <p>DM_BASE.dwa_queryone_user 테이블에 대한 mapper 입니다.</p>
 */

public class DwAccountByIdRowMapper implements RowMapper<DwAccountByIdRes> {
    @Override
    public DwAccountByIdRes mapRow(ResultSet rs, int rowNum) throws SQLException {
        return DwAccountByIdRes.builder()
                .userName(rs.getString("user_name"))
                .empNo(rs.getString("emp_no"))
                .build();
    }
}