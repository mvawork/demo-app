package ru.menshevva.demoapp.service.users.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.menshevva.demoapp.exception.EAppException;
import ru.menshevva.demoapp.security.dto.UserData;
import ru.menshevva.demoapp.security.entities.UserEntity;
import ru.menshevva.demoapp.security.entities.UserEntity_;
import ru.menshevva.demoapp.service.users.UserCRUDService;

import java.math.BigInteger;

@Service
@Slf4j
public class UserCRUDServiceImpl implements UserCRUDService {

    @PersistenceContext
    private EntityManager em;


    @Override
    @Transactional(readOnly = true)
    public UserData read(BigInteger id) {
        var cb = em.getCriteriaBuilder();
        var query = cb.createTupleQuery();
        var root = query.from(UserEntity.class);
        query.multiselect(root.get(UserEntity_.userLogin).alias(UserEntity_.USER_LOGIN),
                root.get(UserEntity_.userName).alias(UserEntity_.USER_NAME));
        query.where(cb.equal(root.get(UserEntity_.userId), id));
        try {
            var t = em.createQuery(query).getSingleResult();
            return UserData.builder()
                    .userId(id)
                    .userLogin(t.get(UserEntity_.USER_LOGIN, UserEntity_.userLogin.getJavaType()))
                    .userName(t.get(UserEntity_.USER_NAME, UserEntity_.userName.getJavaType()))
                    .build();
        } catch (NoResultException e) {
            var s = "Пользователь с идентификатором %d не найден.".formatted(id);
            log.error(s);
            throw new EAppException(s);
        }

    }

    @Override
    @Transactional
    public void create(UserData value) {

        var cb = em.getCriteriaBuilder();
        var query = cb.createQuery(UserEntity.class);
        var root = query.from(UserEntity.class);
        query.where(cb.equal(root.get(UserEntity_.userLogin), value.getUserLogin()));
        if (em.createQuery(query).getResultStream().findFirst().orElse(null) == null) {
            var entity = new UserEntity();
            entity.setUserLogin(value.getUserLogin());
            entity.setUserName(value.getUserName());
            em.persist(entity);
            em.flush();
            value.setUserId(entity.getUserId());
        } else {
            var s = "Пользователь с логином %s уже был добавлен.".formatted(value.getUserLogin());
            log.error(s);
            try {
                throw new Exception(s);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    @Transactional
    public void update(UserData value) {
        var cb = em.getCriteriaBuilder();
        var query = cb.createCriteriaUpdate(UserEntity.class);
        var root = query.from(UserEntity.class);
        query.set(root.get(UserEntity_.userName), value.getUserName())
                .where(cb.equal(root.get(UserEntity_.userId), value.getUserId()));
        em.createQuery(query).executeUpdate();
    }

    @Override
    @Transactional
    public void delete(BigInteger id) {
        var cb = em.getCriteriaBuilder();
        var query = cb.createCriteriaDelete(UserEntity.class);
        var root = query.from(UserEntity.class);
        query.where(cb.equal(root.get(UserEntity_.userId), id));
        em.createQuery(query).executeUpdate();
    }
}
