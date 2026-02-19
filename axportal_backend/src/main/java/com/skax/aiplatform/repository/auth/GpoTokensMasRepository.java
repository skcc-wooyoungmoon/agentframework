package com.skax.aiplatform.repository.auth;

import com.skax.aiplatform.entity.GpoTokensMas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GpoTokensMasRepository extends JpaRepository<GpoTokensMas, String> {

    Optional<GpoTokensMas> findByMemberId(String memberId);

    Optional<GpoTokensMas> findByAccessToken(String accessToken);

    Optional<GpoTokensMas> findByRefreshToken(String refreshToken);

    void deleteByMemberId(String memberId);
}
