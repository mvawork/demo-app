package ru.menshevva.demoapp.entities.main.auth;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.Collection;

@Entity
@Table(name = "DAT_PRIVILEGE", schema = "AUTH")
@Getter
@Setter
@SequenceGenerator(name = "SQ_DAT_PRIVILEGE", schema = "AUTH", sequenceName = "SQ_DAT_PRIVILEGE", allocationSize = 1)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivilegeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SQ_DAT_PRIVILEGE")
    @Column(name = "PRIVILEGE_ID")
    private BigInteger privilegeId;

    @Column(name = "PRIVILEGE_NAME")
    private String privilegeName;

    @Column(name = "PRIVILEGE_DESCRIPTION")
    private String privilegeDescription;


    @ManyToMany(mappedBy = "privileges")
    private Collection<RoleEntity> roles;

}
