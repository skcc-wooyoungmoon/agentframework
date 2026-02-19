package com.skax.aiplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 기본 엔티티 클래스
 * 
 * <p>모든 엔티티의 공통 필드를 정의하는 추상 클래스입니다.
 * JPA Auditing을 통해 생성일시, 수정일시 등의 공통 필드를 자동 관리합니다.</p>
 * 
 * <h3>생명주기 이벤트:</h3>
 * <ul>
 *   <li><strong>@PrePersist</strong>: 최초 생성 시 createAt, createBy만 설정</li>
 *   <li><strong>@PreUpdate</strong>: 수정 시 updateAt, updateBy만 설정</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-08-05
 * @version 1.1.0
 */
@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    /**
     * 생성일시
     * 
     * <p>엔티티가 최초 생성된 시간입니다.
     * @PrePersist 시점에 자동으로 설정되며, 이후 변경되지 않습니다.</p>
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createAt;

    /**
     * 수정일시
     * 
     * <p>엔티티가 마지막으로 수정된 시간입니다.
     * @PreUpdate 시점에만 자동으로 설정됩니다.</p>
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updateAt;

    /**
     * 생성자
     * 
     * <p>엔티티를 최초 생성한 사용자 정보입니다.
     * @PrePersist 시점에 자동으로 설정되며, 이후 변경되지 않습니다.</p>
     */
    @CreatedBy
    @Column(name = "created_by", length = 50, updatable = false)
    private String createBy;

    /**
     * 수정자
     * 
     * <p>엔티티를 마지막으로 수정한 사용자 정보입니다.
     * @PreUpdate 시점에만 자동으로 설정됩니다.</p>
     */
    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updateBy;

    /**
     * 엔티티 최초 생성 시 실행
     * 
     * <p>POST 요청으로 엔티티가 생성될 때 createAt만 설정합니다.
     * updateAt과 updateBy는 null로 유지됩니다.</p>
     */
    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
        // updateAt, updateBy는 생성 시점에 설정하지 않음 (null 유지)
    }

    /**
     * 엔티티 수정 시 실행
     * 
     * <p>PUT/PATCH 요청으로 엔티티가 수정될 때만 updateAt을 설정합니다.
     * updateBy는 @LastModifiedBy를 통해 자동으로 설정됩니다.</p>
     */
    @PreUpdate
    protected void onUpdate() {
        this.updateAt = LocalDateTime.now();
        // updateBy는 @LastModifiedBy를 통해 자동으로 설정됨
    }
}
