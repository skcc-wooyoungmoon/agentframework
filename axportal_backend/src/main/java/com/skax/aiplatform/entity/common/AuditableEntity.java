package com.skax.aiplatform.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@MappedSuperclass
@Getter
public abstract class AuditableEntity extends BaseTimeEntity {

    /**
     * 생성자
     */
    @CreatedBy
    @Column(name = "CREATED_BY", updatable = false, nullable = false)
    private String createdBy;

    /**
     * 수정자
     */
    @LastModifiedBy
    @Column(name = "UPDATED_BY") //insertable = false)
    private String updatedBy;
}
