package ru.menshevva.demoapp.entities.main.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Entity
@Table(name = "dat_users_roles", schema = "auth")
@Getter
@Setter
public class UserRoleEntity {

    @Id
    @Column(name = "user_id")
    private BigInteger userId;

    @Id
    @Column(name = "role_id")
    private BigInteger roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", insertable = false, updatable = false)
    private RoleEntity role;

}
