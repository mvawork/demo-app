package ru.menshevva.demoapp.service.roles.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.menshevva.demoapp.dto.privilege.PrivilegeData;
import ru.menshevva.demoapp.exception.EAppException;
import ru.menshevva.demoapp.security.entities.PrivilegeEntity;
import ru.menshevva.demoapp.security.entities.PrivilegeEntity_;
import ru.menshevva.demoapp.service.roles.PrivilegeCRUDService;

import java.math.BigInteger;

@Service
@Slf4j
public class PrivilegeCRUDServiceImpl implements PrivilegeCRUDService {

    @PersistenceContext
    private EntityManager em;


    @Override
    @Transactional(readOnly = true)
    public PrivilegeData read(BigInteger privilegeId) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(PrivilegeEntity.class);
        cq.multiselect(root.get(PrivilegeEntity_.privilegeId).alias(PrivilegeEntity_.PRIVILEGE_ID),
                root.get(PrivilegeEntity_.privilegeName).alias(PrivilegeEntity_.PRIVILEGE_NAME),
                root.get(PrivilegeEntity_.privilegeDescription).alias(PrivilegeEntity_.PRIVILEGE_DESCRIPTION));
        cq.where(cb.equal(root.get(PrivilegeEntity_.privilegeId), privilegeId));
        try {
            var t = em.createQuery(cq).getSingleResult();
            return PrivilegeData
                    .builder()
                    .privilegeId(t.get(PrivilegeEntity_.PRIVILEGE_ID, PrivilegeEntity_.privilegeId.getJavaType()))
                    .privilegeName(t.get(PrivilegeEntity_.PRIVILEGE_NAME, PrivilegeEntity_.privilegeName.getJavaType()))
                    .privilegeDescription(t.get(PrivilegeEntity_.PRIVILEGE_DESCRIPTION, PrivilegeEntity_.privilegeDescription.getJavaType()))
                    .build();
        } catch (NoResultException e) {
            var s = "Привилегия с идентификатором %d не найдена.".formatted(privilegeId);
            log.error(s);
            throw new EAppException(s);
        }
    }

    @Override
    @Transactional
    public void create(PrivilegeData privilegeData) {
        var entity = PrivilegeEntity.builder()
                .privilegeName(privilegeData.getPrivilegeName())
                .privilegeDescription(privilegeData.getPrivilegeDescription())
                .build();
        em.persist(entity);
        em.flush();
        privilegeData.setPrivilegeId(entity.getPrivilegeId());
    }

    @Override
    @Transactional
    public void update(PrivilegeData privilegeData) {
        var cb = em.getCriteriaBuilder();
        var cu = cb.createCriteriaUpdate(PrivilegeEntity.class);
        var root = cu.from(PrivilegeEntity.class);
        cu.set(root.get(PrivilegeEntity_.privilegeName), privilegeData.getPrivilegeName());
        cu.set(root.get(PrivilegeEntity_.privilegeDescription), privilegeData.getPrivilegeDescription());
        cu.where(cb.equal(root.get(PrivilegeEntity_.privilegeId), privilegeData.getPrivilegeId()));
        em.createQuery(cu).executeUpdate();
    }

    @Override
    @Transactional
    public void delete(BigInteger privilegeId) {
        var cb = em.getCriteriaBuilder();
        var cu = cb.createCriteriaDelete(PrivilegeEntity.class);
        var root = cu.from(PrivilegeEntity.class);
        cu.where(cb.equal(root.get(PrivilegeEntity_.privilegeId), privilegeId));
        em.createQuery(cu).executeUpdate();
    }

}
