package com.skax.aiplatform.entity.sample;

import com.skax.aiplatform.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 샘플 사용자 엔티티
 * 
 * <p>샘플 사용자 정보를 저장하는 엔티티입니다.
 * BaseEntity를 상속받아 공통 필드(생성일시, 수정일시 등)를 포함합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-03
 * @version 2.0.0
 */
@Entity
@Table(name = "sample_users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SampleUser extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "full_name", length = 100)
    private String fullName;
    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "department", length = 50)
    private String department;
    
    @Column(name = "position", length = 50)
    private String position;

    /**
     * 사용자 정보 업데이트
     * 
     * @param email 이메일
     * @param fullName 전체 이름
     * @param phoneNumber 전화번호
     * @param department 부서
     * @param position 직급
     */
    public void updateInfo(String email, String fullName, String phoneNumber, 
                          String department, String position) {
        if (email != null) this.email = email;
        if (fullName != null) this.fullName = fullName;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (department != null) this.department = department;
        if (position != null) this.position = position;
    }
    
    /**
     * 사용자 활성화 상태 변경
     * 
     * @param isActive 활성화 여부
     */
    public void updateActiveStatus(Boolean isActive) {
        if (isActive != null) this.isActive = isActive;
    }
}
