package com.skax.aiplatform.service.vertica.impl;

import com.skax.aiplatform.dto.vertica.response.DwAccountByIdRes;
import com.skax.aiplatform.dto.vertica.response.DwAccountListRes;
import com.skax.aiplatform.repository.vertica.mapper.DwAccountByIdRowMapper;
import com.skax.aiplatform.repository.vertica.mapper.DwAccountRowMapper;
import com.skax.aiplatform.service.vertica.DwAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@ConditionalOnProperty(
        prefix = "vertica.datasource",
        name = "jdbc-url"
)
@ConditionalOnBean(name = "verticaJdbcTemplate")
public class DwAccountServiceImpl implements DwAccountService {

    private final JdbcTemplate verticaJdbcTemplate;

    public DwAccountServiceImpl(@Qualifier("verticaJdbcTemplate") JdbcTemplate verticaJdbcTemplate) {
        this.verticaJdbcTemplate = verticaJdbcTemplate;
        log.info("✅ VerticaSampleService 초기화 완료 - JdbcTemplate: {}", verticaJdbcTemplate.getClass().getSimpleName());
    }
    @Override
    public List<DwAccountListRes> getDwAccountList(String userId){
        String sql = """
                SELECT ROW_NUMBER() OVER (ORDER BY valid_start_date) as rownum,
                    user_name,
                    emp_no,
                    group_name,
                    dept_cd,
                    valid_start_date,
                    valid_end_date,
                    db_account_id,
                    db_name,
                    db_type,
                    ip_addr,
                    dw_data_gjdt,
                    dw_lst_jukja_dt,
                    CASE
                        WHEN CAST(TO_TIMESTAMP(valid_start_date, 'YYYY-MM-DD HH24:MI:SS') AS DATE) <= CURRENT_DATE
                            AND CAST(TO_TIMESTAMP(valid_end_date,   'YYYY-MM-DD HH24:MI:SS') AS DATE) >= CURRENT_DATE
                            THEN 'Y'
                        ELSE 'N'
                        END AS accountStatus
                 FROM DM_BASE.dwa_queryone_user
                WHERE db_type = 'Vertica'
                  AND db_name IN (
                                  '[TST] TADWDB_통합DW_ADWDB01T',
                                  '[TST] TADWDB_통합DW_ADWDB02T',
                                  '[TST] TADWDB_통합DW_ADWDB03T',
                                  '[TST] TADWDB_통합DW_ADWDB04T',
                                  '[TST] TADWDB_통합DW_ADWDB05T',
                                  '[TST] TADWDB_통합DW_ADWDB06T'
                    )
                  AND emp_no = ?
                """;
        log.info("vertica 쿼리 조회 실행: {}", sql);

        List<DwAccountListRes> result = verticaJdbcTemplate.query(sql, new DwAccountRowMapper(),userId);
        log.info("vertica 쿼리 조회 결과: {}", result.size());

        return result;
    }

    @Override
    public List<DwAccountByIdRes> getDwAccountById(String empNo, String accountId){
        String sql = """
                SELECT 
                    user_name,
                    emp_no
                 FROM DM_BASE.dwa_queryone_user
                WHERE db_type = 'Vertica'
                  AND db_name IN (
                                  '[TST] TADWDB_통합DW_ADWDB01T',
                                  '[TST] TADWDB_통합DW_ADWDB02T',
                                  '[TST] TADWDB_통합DW_ADWDB03T',
                                  '[TST] TADWDB_통합DW_ADWDB04T',
                                  '[TST] TADWDB_통합DW_ADWDB05T',
                                  '[TST] TADWDB_통합DW_ADWDB06T'
                    )
                  AND db_account_id = ?
                  AND emp_no = ?
                """;
        log.info("vertica 쿼리 조회 실행: {}", sql);

        List<DwAccountByIdRes> result = verticaJdbcTemplate.query(sql, new DwAccountByIdRowMapper(),accountId,empNo);
        log.info("vertica 쿼리 조회 결과: {}", result.size());

        return result;
    }
}
