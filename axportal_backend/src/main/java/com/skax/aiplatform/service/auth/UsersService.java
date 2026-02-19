package com.skax.aiplatform.service.auth;

import com.skax.aiplatform.client.sktai.auth.dto.response.MeResponse;
import com.skax.aiplatform.dto.auth.response.UsersMeRes;

public interface UsersService {
    MeResponse getMe() throws Exception;

    /**
     * 특정 사용자 정보를 조회
     *
     * @return 특정 사용자 정보
     * @throws Exception 조회 실패시 예외 발생
     */
    UsersMeRes getUserInfo(String memberId) throws Exception;

    /**
     * 현재 사용자 정보를 조회한다
     *
     * @return 현재 사용자 정보
     * @throws Exception 조회 실패시 예외 발생
     */
    UsersMeRes getUserInfo() throws Exception;
}
