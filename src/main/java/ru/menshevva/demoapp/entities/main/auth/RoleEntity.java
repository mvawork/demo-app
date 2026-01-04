package ru.menshevva.demoapp.entities.main.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Collection;

@Entity
@Table(name = "DAT_ROLE", schema = "AUTH")
@Getter
@Setter
@SequenceGenerator(name = "SQ_DAT_ROLE", schema = "AUTH", sequenceName = "SQ_DAT_ROLE", allocationSize = 1)
public class RoleEntity {

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_DAT_ROLE")
    private BigInteger roleId;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "role_description")
    private String roleDescription;

    @ManyToMany
    @JoinTable(name = "DAT_ROLES_PRIVILEGES", schema = "AUTH",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "privilege_id"))
    private Collection<PrivilegeEntity> privileges;
}
