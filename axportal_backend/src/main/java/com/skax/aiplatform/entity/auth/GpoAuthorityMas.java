package com.skax.aiplatform.entity.auth;

import com.skax.aiplatform.entity.common.BaseEntity2;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GPO_AUTHORITY_MAS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GpoAuthorityMas extends BaseEntity2 {

    /**
     * 권한 아이디
     */
    @Id
    @Column(name = "AUTHORITY_ID")
    private String authorityId;

    /**
     * 상위 권한 아이디
     */
    @Column(name = "HRNK_AUTHORITY_ID")
    private String hrnkAuthorityId;

    /**
     * 권한명
     */
    @Column(name = "AUTHORITY_NM")
    private String authorityNm;

    /**
     * 상위 메뉴 (1depth)
     */
    @Column(name = "ONE_DPH_MENU")
    private String oneDphMenu;

    /**
     * 하위 메뉴 (2depth)
     */
    @Column(name = "TWO_DPH_MENU")
    private String twoDphMenu;

    /**
     * 상세 권한 목록 (구체적인 기능들)
     */
    @Column(name = "DTL_CTNT")
    private String dtlCtnt;

}
