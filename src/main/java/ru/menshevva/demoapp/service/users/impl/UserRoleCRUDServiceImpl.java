package ru.menshevva.demoapp.service.users.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.menshevva.demoapp.dto.userrole.UserRoleData;
import ru.menshevva.demoapp.entities.main.auth.UserRoleEntity;
import ru.menshevva.demoapp.entities.main.auth.UserRoleEntity_;
import ru.menshevva.demoapp.service.users.UserRoleCRUDService;

@Service
public class UserRoleCRUDServiceImpl implements UserRoleCRUDService {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void create(UserRoleData value) {
        var e = new UserRoleEntity();
        e.setUserId(value.getUserId());
        e.setRoleId(value.getRoleId());
        em.persist(e);
    }

    @Override
    @Transactional
    public void delete(UserRoleData value) {
        var cb = em.getCriteriaBuilder();
        var cd = cb.createCriteriaDelete(UserRoleEntity.class);
        var root = cd.from(UserRoleEntity.class);
        cd.where(cb.equal(root.get(UserRoleEntity_.userId), value.getUserId()),
                cb.equal(root.get(UserRoleEntity_.roleId), value.getRoleId()));
        em.createQuery(cd).executeUpdate();

    }
}
