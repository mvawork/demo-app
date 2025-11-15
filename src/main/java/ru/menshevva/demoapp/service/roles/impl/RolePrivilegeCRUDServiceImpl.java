package ru.menshevva.demoapp.service.roles.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.menshevva.demoapp.dto.roleprivilege.RolePrivilegeData;
import ru.menshevva.demoapp.security.entities.RolePrivilegeEntity;
import ru.menshevva.demoapp.security.entities.RolePrivilegeEntity_;
import ru.menshevva.demoapp.service.roles.RolePrivilegeCRUDService;

import java.math.BigInteger;

@Service
@Slf4j
public class RolePrivilegeCRUDServiceImpl implements RolePrivilegeCRUDService {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void create(RolePrivilegeData value) {
        var e = new RolePrivilegeEntity();
        e.setRoleId(value.getRoleId());
        e.setPrivilegeId(value.getPrivilegeId());
        em.persist(e);
    }

    @Override
    @Transactional
    public void delete(RolePrivilegeData value) {
        var cb = em.getCriteriaBuilder();
        var cd = cb.createCriteriaDelete(RolePrivilegeEntity.class);
        var root = cd.from(RolePrivilegeEntity.class);
        cd.where(cb.equal(root.get(RolePrivilegeEntity_.roleId), value.getRoleId()), cb.equal(root.get(RolePrivilegeEntity_.privilegeId), value.getPrivilegeId()));
        em.createQuery(cd).executeUpdate();
    }

    @Override
    @Transactional
    public void deleteForRole(BigInteger roleId) {
        var cb = em.getCriteriaBuilder();
        var cd = cb.createCriteriaDelete(RolePrivilegeEntity.class);
        var root = cd.from(RolePrivilegeEntity.class);
        cd.where(cb.equal(root.get(RolePrivilegeEntity_.roleId), roleId));
        em.createQuery(cd).executeUpdate();
    }

}
