package com.skax.aiplatform.entity.common.approval;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class GpoGyljLineMasId implements Serializable {
    private static final long serialVersionUID = 6710032312908224189L;
    @Size(max = 100)
    @NotNull
    @Column(name = "gylj_line_nm", nullable = false, length = 100)
    private String gyljLineNm;

    @NotNull
    @Column(name = "gylj_line_sno", nullable = false, precision = 3)
    private BigDecimal gyljLineSno;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GpoGyljLineMasId entity = (GpoGyljLineMasId) o;
        return Objects.equals(this.gyljLineNm, entity.gyljLineNm) &&
                Objects.equals(this.gyljLineSno, entity.gyljLineSno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gyljLineNm, gyljLineSno);
    }

}