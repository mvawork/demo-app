package ru.menshevva.demoapp.service.roles.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.menshevva.demoapp.dto.role.RoleData;
import ru.menshevva.demoapp.entities.main.auth.RoleEntity_;
import ru.menshevva.demoapp.exception.EAppException;
import ru.menshevva.demoapp.entities.main.auth.RoleEntity;
import ru.menshevva.demoapp.service.roles.RoleCRUDService;
import ru.menshevva.demoapp.service.roles.RolePrivilegeCRUDService;

import java.math.BigInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleCRUDServiceImpl implements RoleCRUDService {

    @PersistenceContext
    private EntityManager em;

    private final RolePrivilegeCRUDService rolePrivilegeCRUDService;

    @Override
    @Transactional(readOnly = true)
    public RoleData read(BigInteger roleId) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(RoleEntity.class);
        cq.multiselect(root.get(RoleEntity_.roleName).alias(RoleEntity_.ROLE_NAME),
                root.get(RoleEntity_.roleDescription).alias(RoleEntity_.ROLE_DESCRIPTION));
        cq.where(cb.equal(root.get(RoleEntity_.roleId), roleId));
        try {
            var result = em.createQuery(cq).getSingleResult();
            return RoleData.builder()
                    .roleId(roleId)
                    .roleName(result.get(RoleEntity_.ROLE_NAME, RoleEntity_.roleName.getJavaType()))
                    .roleDescription(result.get(RoleEntity_.ROLE_DESCRIPTION, RoleEntity_.roleDescription.getJavaType()))
                    .build();
        } catch (NoResultException e) {
            var s = "Роль с идентификатором %d не найдена".formatted(roleId);
            log.error(s);
            throw new EAppException(s);
        }
    }

    @Override
    @Transactional
    public void create(RoleData roleData) {
        var e = new RoleEntity();
        e.setRoleName(roleData.getRoleName());
        e.setRoleDescription(roleData.getRoleDescription());
        em.persist(e);
        em.flush();
        roleData.setRoleId(e.getRoleId());
    }

    @Override
    @Transactional
    public void update(RoleData roleData) {
        var cb = em.getCriteriaBuilder();
        var cu = cb.createCriteriaUpdate(RoleEntity.class);
        var root = cu.from(RoleEntity.class);
        cu.set(root.get(RoleEntity_.roleName), roleData.getRoleName());
        cu.set(root.get(RoleEntity_.roleDescription), roleData.getRoleDescription());
        cu.where(cb.equal(root.get(RoleEntity_.roleId), roleData.getRoleId()));
        em.createQuery(cu).executeUpdate();
    }

    @Override
    @Transactional
    public void delete(BigInteger roleId) {
        rolePrivilegeCRUDService.deleteForRole(roleId);
        var cb = em.getCriteriaBuilder();
        var cu = cb.createCriteriaDelete(RoleEntity.class);
        var root = cu.from(RoleEntity.class);
        cu.where(cb.equal(root.get(RoleEntity_.roleId), roleId));
        em.createQuery(cu).executeUpdate();
    }

}
