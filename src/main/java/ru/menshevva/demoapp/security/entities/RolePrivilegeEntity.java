package ru.menshevva.demoapp.security.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Entity
@Table(name = "DAT_ROLES_PRIVILEGES", schema = "AUTH")
@Getter
@Setter
@IdClass(RolePrivilegesEntityPK.class)
public class RolePrivilegeEntity {

    @Id
    @Column(name = "ROLE_ID")
    private BigInteger roleId;

    @Id
    @Column(name = "PRIVILEGE_ID")
    private BigInteger privilegeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", insertable = false, updatable = false)
    private RoleEntity roleEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "privilege_id", referencedColumnName = "privilege_id", insertable = false, updatable = false)
    private PrivilegeEntity privilegeEntity;

}

