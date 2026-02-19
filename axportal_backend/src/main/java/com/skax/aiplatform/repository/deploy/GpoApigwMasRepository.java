package com.skax.aiplatform.repository.deploy;


import org.springframework.data.jpa.repository.JpaRepository;

import com.skax.aiplatform.entity.deploy.GpoApigwMas;

public interface GpoApigwMasRepository extends JpaRepository<GpoApigwMas, String> {
    
    GpoApigwMas findByGpoApiId(String gpoApiId);

    GpoApigwMas findByGpoTskId(String gpoTskId);
}
