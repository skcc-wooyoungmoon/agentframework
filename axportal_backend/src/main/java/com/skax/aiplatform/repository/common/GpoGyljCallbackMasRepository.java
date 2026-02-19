package com.skax.aiplatform.repository.common;


import com.skax.aiplatform.entity.common.approval.GpoGyljCallbackMas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GpoGyljCallbackMasRepository extends JpaRepository<GpoGyljCallbackMas, Long> {

    boolean existsByGyljRespId(String gyljRespId);
    GpoGyljCallbackMas findByGyljRespId(String gyljRespId);
}
