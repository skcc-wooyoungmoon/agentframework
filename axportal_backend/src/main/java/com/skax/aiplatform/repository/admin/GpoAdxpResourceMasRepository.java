package com.skax.aiplatform.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skax.aiplatform.entity.auth.GpoAdxpResourceMas;
import com.skax.aiplatform.entity.auth.GpoAdxpResourceMasId;

public interface GpoAdxpResourceMasRepository extends JpaRepository<GpoAdxpResourceMas, GpoAdxpResourceMasId> {

    List<GpoAdxpResourceMas> findAllByIdAuthorityId(String authorityId);
}
