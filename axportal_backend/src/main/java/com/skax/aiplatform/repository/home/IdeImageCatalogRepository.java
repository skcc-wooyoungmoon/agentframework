package com.skax.aiplatform.repository.home;

import com.skax.aiplatform.entity.IdeImageCatalog;
import com.skax.aiplatform.entity.ide.GpoIdeImageMas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IdeImageCatalogRepository
        extends JpaRepository<GpoIdeImageMas, String> {


    @Query(value = """
              SELECT i.tag_ctnt FROM gpo_ide_image_mas i
               WHERE i.img_g = :ideType
                 AND i.pgm_version_no = :pythonVer
                 AND i.usyn = 1
               ORDER BY i.seq_no
            """, nativeQuery = true)
    Optional<String> findActiveImageTag(@Param("ideType") String ideType,
                                        @Param("pythonVer") String pythonVer);
}
