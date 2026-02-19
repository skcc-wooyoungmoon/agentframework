package com.skax.aiplatform.service.vertica;

import com.skax.aiplatform.dto.vertica.response.DwAccountByIdRes;
import com.skax.aiplatform.dto.vertica.response.DwAccountListRes;

import java.util.List;

public interface DwAccountService {
    List<DwAccountListRes> getDwAccountList(String userId);
    List<DwAccountByIdRes> getDwAccountById(String empNo, String accountId);
}
