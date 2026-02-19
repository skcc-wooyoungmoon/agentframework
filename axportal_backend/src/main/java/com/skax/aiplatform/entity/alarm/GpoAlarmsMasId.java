package com.skax.aiplatform.entity.alarm;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class GpoAlarmsMasId implements Serializable {
    private static final long serialVersionUID = -5602060924576397847L;
    @Size(max = 50)
    @NotNull
    @Column(name = "alarm_id", nullable = false, length = 50)
    private String alarmId;

    @Size(max = 50)
    @NotNull
    @Column(name = "member_id", nullable = false, length = 50)
    private String memberId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        GpoAlarmsMasId entity = (GpoAlarmsMasId) o;
        return Objects.equals(this.alarmId, entity.alarmId) &&
                Objects.equals(this.memberId, entity.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alarmId, memberId);
    }

}