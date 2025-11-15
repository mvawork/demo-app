package ru.menshevva.demoapp.security.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Collection;

@Entity
@Table(name = "DAT_USER", schema = "AUTH")
@Getter
@Setter
@SequenceGenerator(name = "SQ_DAT_USER", schema = "AUTH", sequenceName = "SQ_DAT_USER", allocationSize = 1)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_DAT_USER")
    @Column(name = "user_id")
    private BigInteger userId;

    @Column(name = "user_login")
    private String userLogin;

    @Column(name = "user_name")
    private String userName;

    @ManyToMany
    @JoinTable(name = "DAT_USERS_ROLES", schema = "AUTH",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    private Collection<RoleEntity> roles;

}
