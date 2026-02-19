package com.skax.aiplatform.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Where;

import com.skax.aiplatform.entity.common.BaseEntity2;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 메뉴 엔티티
 * 
 * <p>
 * 메뉴 계층 구조를 저장하는 엔티티입니다.
 * BaseEntity를 상속받아 공통 필드(생성일시, 수정일시 등)를 포함합니다.
 * </p>
 * 
 * @author Generated
 * @since 2025-01-XX
 */
@Entity
@Table(name = "gpo_menu_visible_mas")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GpoMenuVisibleMas extends BaseEntity2 {

    /**
     * 메뉴 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gpo_menu_id", nullable = false)
    private Long id;

    /**
     * 부모 메뉴 ID (FK)
     * NULL인 경우 최상위 메뉴
     */
    @Column(name = "parnt_menu_id")
    private Long parentId;

    /**
     * 메뉴 코드 (UNIQUE)
     * 프론트엔드의 id와 매핑
     */
    @Column(name = "menu_c", length = 50, nullable = false, unique = true)
    private String code;

    /**
     * 메뉴명
     */
    @Column(name = "menu_nm", length = 100, nullable = false)
    private String name;

    /**
     * React Router 경로
     * 빈 문자열인 경우 부모 경로 상속
     */
    @Column(name = "menu_path", length = 255)
    private String path;

    /**
     * 아이콘 이름
     */
    @Column(name = "icn_nm", length = 100)
    private String icon;

    /**
     * 정렬 순서
     */
    @Column(name = "sort_seq_no")
    @Builder.Default
    private Integer orderNo = 0;

    /**
     * 깊이 (0=최상위)
     * parent_id로 계산 가능하지만 성능을 위해 저장
     */
    @Column(name = "dph_seq_no")
    @Builder.Default
    private Integer depth = 0;

    /**
     * 외부 링크 여부
     */
    @Column(name = "ex_link_yn")
    @Builder.Default
    private Integer isExternal = 0;

    /**
     * 표시 여부
     */
    @Column(name = "vsbl_yn")
    @Builder.Default
    private Integer visible = 1;

    /**
     * 활성화 여부
     */
    @Column(name = "actv_yn")
    @Builder.Default
    private Integer active = 1;

    /**
     * 설명
     */
    @Column(name = "dtl_ctnt", length = 500)
    private String description;

    /**
     * 권한 코드
     */
    @Column(name = "authority_id", length = 50)
    private String auth;

    /**
     * 부모 메뉴 (JPA 관계)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parnt_menu_id", insertable = false, updatable = false)
    private GpoMenuVisibleMas parent;

    /**
     * 하위 메뉴 목록 (JPA 관계)
     * visible과 active가 true인 메뉴만 자동으로 필터링
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @Where(clause = "vsbl_yn = 1 AND actv_yn = 1")
    @OrderBy("orderNo ASC")
    @Builder.Default
    private List<GpoMenuVisibleMas> children = new ArrayList<>();

    /**
     * depth 자동 계산
     * parent_id가 변경될 때 depth를 자동으로 계산
     * 주의: parent가 로드되지 않은 경우 Service 레벨에서 처리 필요
     */
    @PrePersist
    @PreUpdate
    private void calculateDepth() {
        if (parentId == null) {
            this.depth = 0;
        } else if (parent != null) {
            // parent가 로드된 경우에만 계산
            this.depth = parent.getDepth() + 1;
        }
        // parent가 null인 경우는 Service 레벨에서 처리
    }
}

