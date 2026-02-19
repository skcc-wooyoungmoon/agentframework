package com.skax.aiplatform.repository.home;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skax.aiplatform.entity.ide.GpoIdeResourceMas;
import com.skax.aiplatform.entity.ide.ImageType;

/**
 * IDE 자원 Repository (v2)
 */
public interface GpoIdeResourceMasRepository extends JpaRepository<GpoIdeResourceMas, ImageType> {

}
