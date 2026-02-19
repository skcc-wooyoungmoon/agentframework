package com.skax.aiplatform.entity.common.approval;

import com.skax.aiplatform.entity.common.BaseEntity2;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "gpo_gylj_line_mas")
public class GpoGyljLineMas extends BaseEntity2 {
    @EmbeddedId
    private GpoGyljLineMasId id;

    @Column(name = "role_seq" )
    private Long roleSeq;

}