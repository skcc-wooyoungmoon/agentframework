package com.skax.aiplatform.repository.auth;

import java.util.Optional;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skax.aiplatform.entity.GpoUsersMas;

@Repository
public interface GpoUsersMasRepository extends JpaRepository<GpoUsersMas, String> {

    Optional<GpoUsersMas> findByMemberId(String memberId);

    boolean existsByMemberId(String memberId);

    Optional<GpoUsersMas> findByUuid(String uuid);

    List<GpoUsersMas> findByUuidIn(Collection<String> uuids);

}
