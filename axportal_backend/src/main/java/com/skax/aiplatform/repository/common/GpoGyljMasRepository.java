package com.skax.aiplatform.repository.common;

import com.skax.aiplatform.entity.common.approval.GpoGyljMas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GpoGyljMasRepository extends JpaRepository<GpoGyljMas, String> {

    // Optional<GpoGyljMas> findByGyljRespId(String gyljRespId);

    List<GpoGyljMas> findByEroCtnt(String eroCtnt);
}