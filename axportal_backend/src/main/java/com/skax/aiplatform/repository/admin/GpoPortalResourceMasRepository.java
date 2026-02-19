package com.skax.aiplatform.repository.admin;

import com.skax.aiplatform.entity.auth.GpoPortalResourceMas;
import com.skax.aiplatform.entity.auth.GpoPortalResourceMasId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GpoPortalResourceMasRepository extends JpaRepository<GpoPortalResourceMas, GpoPortalResourceMasId> {

    List<GpoPortalResourceMas> findAllByIdAuthorityId(String authorityId);
}
