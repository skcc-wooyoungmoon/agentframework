package com.skax.aiplatform.entity.mapping;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.skax.aiplatform.entity.common.BaseEntity2;
import com.skax.aiplatform.entity.project.Project;
import com.skax.aiplatform.entity.role.Role;
import com.skax.aiplatform.entity.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.skax.aiplatform.entity.mapping.ProjectUserRoleStatus.ACTIVE;
import static com.skax.aiplatform.entity.mapping.ProjectUserRoleStatus.INACTIVE;

@Entity
@Table(name = "GPO_PRJUSERROLE_MAP_MAS")
@Getter
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectUserRole extends BaseEntity2 {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "s_seq_no_prjuserrole")
    @SequenceGenerator(
            name = "s_seq_no_prjuserrole",
            sequenceName = "S_SEQ_NO_PRJUSERROLE",
            allocationSize = 1
    )
    @Column(name = "SEQ_NO")
    private Long seqNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_NM")
    private ProjectUserRoleStatus statusNm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "PRJ_SEQ")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ROLE_SEQ")
    private Role role;

    // == 비즈니스 로직 == //

    public void activate() {
        this.statusNm = ACTIVE;
    }

    public void inActivate() {
        this.statusNm = INACTIVE;
    }

    public void updateStatus(ProjectUserRoleStatus status) {
        this.statusNm = status;
    }

    public void changeRole(Role role) {
        this.role = role;
    }

    public boolean isDifferentRole(Role other) {
        if (this.role == null || other == null) {
            return this.role != other;
        }
        return !this.role.getRoleSeq().equals(other.getRoleSeq());
    }

    public static ProjectUserRole create(Project project, Role role, User user) {
        return ProjectUserRole.builder()
                .project(project)
                .role(role)
                .user(user)
                .statusNm(INACTIVE)
                .build();
    }

    public void updateRole(Role newRole) {
        this.role = newRole;
    }

    /**
     * toString 메서드 - LazyInitializationException 방지
     *
     * @return 안전한 문자열 표현
     */
    @Override
    public String toString() {
        return "ProjectUserRole{" +
                "seqNo=" + seqNo +
                ", statusNm=" + statusNm +
                ", userId=" + (user != null ? user.getMemberId() : "null") +
                ", projectSeq=" + (project != null ? project.getPrjSeq() : "null") +
                ", roleSeq=" + (role != null ? role.getRoleSeq() : "null") +
                '}';
    }

}
