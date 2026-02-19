package com.skax.aiplatform.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    /**
     * 최초 생성일시
     */
    @CreatedDate
    @Column(name = "FST_CREATED_AT", updatable = false, nullable = false)
    private LocalDateTime fstCreatedAt;

    /**
     * 최종 수정일시
     */
    @LastModifiedDate
    @Column(name = "LST_UPDATED_AT")   //, insertable = false)
    private LocalDateTime lstUpdatedAt;

}
