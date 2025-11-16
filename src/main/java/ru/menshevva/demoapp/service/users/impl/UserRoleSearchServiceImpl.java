package ru.menshevva.demoapp.service.users.impl;

import com.vaadin.flow.data.provider.Query;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.menshevva.demoapp.dto.userrole.UserRoleListData;
import ru.menshevva.demoapp.security.entities.*;
import ru.menshevva.demoapp.security.entities.RoleEntity;
import ru.menshevva.demoapp.security.entities.UserRoleEntity;
import ru.menshevva.demoapp.service.users.UserRoleSearchFilter;
import ru.menshevva.demoapp.service.users.UserRoleSearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static ru.menshevva.demoapp.service.users.UserRoleSearchFilter.*;

@Service
@Slf4j
public class UserRoleSearchServiceImpl implements UserRoleSearchService {

    @PersistenceContext
    private EntityManager em;

    protected void setParams(TypedQuery<Tuple> tq, Query<UserRoleListData, UserRoleSearchFilter> query) {
        var params = tq.getParameters();
        if (params.isEmpty()) {
            return;
        }
        var filter = query.getFilter().orElse(UserRoleSearchFilter.builder().build());
        params.stream()
                .filter(v -> v.getName() != null && !v.getName().isEmpty())
                .forEach(v -> {
                    switch (v.getName()) {
                        case FILTER_USER_ID -> tq.setParameter(v.getName(), filter.getUserId());
                        case FILTER_ROLE_ID -> tq.setParameter(v.getName(), filter.getRoleId());
                        case FILTER_ROLE_NAME -> tq.setParameter(v.getName(), filter.getRoleName());
                        case FILTER_ROLE_DESCRIPTION -> tq.setParameter(v.getName(), filter.getRoleDescription());
                    }
                });
    }

    @Override
    public int getCount(Query<UserRoleListData, UserRoleSearchFilter> query) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var userRole = cq.from(UserRoleEntity.class);
        var joinRole = userRole.join(UserRoleEntity_.role);
        var col = cb.count(userRole);
        cq.multiselect(col);
        buildWhere(cb, cq, userRole, joinRole, query);
        TypedQuery<Tuple> tq = em.createQuery(cq);
        setParams(tq, query);
        var t = tq.getSingleResult();
        return t.get(0, col.getJavaType()).intValue();
    }

    @Override
    public Stream<UserRoleListData> fetch(Query<UserRoleListData, UserRoleSearchFilter> query) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var userRoleEntity = cq.from(UserRoleEntity.class);
        var roleEntityJoin = userRoleEntity.join(UserRoleEntity_.role);

        cq.multiselect(userRoleEntity.get(UserRoleEntity_.userId).alias(UserEntity_.USER_ID),
                userRoleEntity.get(UserRoleEntity_.roleId).alias(UserRoleEntity_.ROLE_ID),
                roleEntityJoin.get(RoleEntity_.roleName).alias(RoleEntity_.ROLE_NAME),
                roleEntityJoin.get(RoleEntity_.roleDescription).alias(RoleEntity_.ROLE_DESCRIPTION)
                );
        buildWhere(cb, cq, userRoleEntity, roleEntityJoin, query);
        buildOrder(cb, cq, userRoleEntity, roleEntityJoin, query);
        var tq = em.createQuery(cq);
        setParams(tq, query);
        return tq
                .setFirstResult(query.getOffset())
                .setMaxResults(query.getLimit())
                .getResultList()
                .stream()
                .map(t -> UserRoleListData.builder()
                        .userId(t.get(UserRoleEntity_.USER_ID, UserRoleEntity_.userId.getJavaType()))
                        .roleId(t.get(UserRoleEntity_.ROLE_ID, UserRoleEntity_.roleId.getJavaType()))
                        .roleName(t.get(RoleEntity_.ROLE_NAME, RoleEntity_.roleName.getJavaType()))
                        .roleDescription(t.get(RoleEntity_.ROLE_DESCRIPTION, RoleEntity_.roleDescription.getJavaType()))
                        .build());
    }

    private void buildOrder(CriteriaBuilder cb, CriteriaQuery<Tuple> criteriaQuery, Root<UserRoleEntity> root,
                             Join<UserRoleEntity, RoleEntity> joinRole,
                            Query<UserRoleListData, UserRoleSearchFilter> query) {
        List<Order> orders = new ArrayList<>();
        query.getSortOrders()
                .forEach(
                        v -> {
                            var expression = switch (v.getSorted()) {
                                case FILTER_USER_ID -> root.get(UserRoleEntity_.userId);
                                case FILTER_ROLE_ID -> root.get(UserRoleEntity_.roleId);
                                case FILTER_ROLE_NAME -> joinRole.get(RoleEntity_.roleName);
                                case FILTER_ROLE_DESCRIPTION -> joinRole.get(RoleEntity_.roleDescription);
                                case null, default -> null;
                            };
                            if (expression != null) {
                                orders.add(createOrder(cb, v.getDirection(), expression));
                            }
                        }
                );
        if (!orders.isEmpty()) {
            criteriaQuery.orderBy(orders);
        }

    }


    protected void buildWhere(CriteriaBuilder cb, CriteriaQuery<Tuple> criteriaQuery, Root<UserRoleEntity> userRoleEntity,
                              Join<UserRoleEntity, RoleEntity> joinRole,
                              Query<UserRoleListData, UserRoleSearchFilter> query) {
        var predicates = new ArrayList<Predicate>();
        query.getFilter().ifPresent(filter -> {
            if (filter.getUserId() != null) {
                predicates.add(cb.equal(userRoleEntity.get(UserRoleEntity_.userId),
                        cb.parameter(UserRoleEntity_.userId.getJavaType(), FILTER_USER_ID)));
            }
            if (filter.getRoleId() != null) {
                predicates.add(cb.equal(userRoleEntity.get(UserRoleEntity_.roleId),
                        cb.parameter(UserRoleEntity_.roleId.getJavaType(), FILTER_ROLE_ID)));
            }
            if (filter.getRoleName() != null && !filter.getRoleName().isEmpty()) {
                predicates.add(cb.like(joinRole.get(RoleEntity_.roleName),
                        cb.parameter(RoleEntity_.roleName.getJavaType(), FILTER_ROLE_NAME)));
            }
            if (filter.getRoleDescription() != null && !filter.getRoleDescription().isEmpty()) {
                predicates.add(cb.like(joinRole.get(RoleEntity_.roleDescription),
                        cb.parameter(RoleEntity_.roleDescription.getJavaType(), FILTER_ROLE_DESCRIPTION)));
            }
        });
        if (!predicates.isEmpty()) {
            criteriaQuery.where(predicates.toArray(new Predicate[0]));
        }
    }

}
