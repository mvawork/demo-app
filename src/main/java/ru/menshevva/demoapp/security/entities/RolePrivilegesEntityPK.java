package ru.menshevva.demoapp.security.entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Getter
@Setter
public class RolePrivilegesEntityPK implements Serializable {

    private BigInteger roleId;
    private BigInteger privilegeId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RolePrivilegesEntityPK that = (RolePrivilegesEntityPK) o;
        return Objects.equals(roleId, that.roleId) && Objects.equals(privilegeId, that.privilegeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, privilegeId);
    }
}
