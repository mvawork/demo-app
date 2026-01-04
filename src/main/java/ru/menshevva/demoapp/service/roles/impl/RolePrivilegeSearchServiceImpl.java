package ru.menshevva.demoapp.service.roles.impl;

import com.vaadin.flow.data.provider.Query;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.menshevva.demoapp.dto.roleprivilege.RolePrivilegeListData;
import ru.menshevva.demoapp.entities.main.auth.*;
import ru.menshevva.demoapp.service.roles.RolePrivilegeSearchFilter;
import ru.menshevva.demoapp.service.roles.RolePrivilegeSearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static ru.menshevva.demoapp.service.roles.RolePrivilegeSearchFilter.*;
import static ru.menshevva.demoapp.service.roles.RoleSearchFilter.FILTER_ROLE_DESCRIPTION;
import static ru.menshevva.demoapp.service.roles.RoleSearchFilter.FILTER_ROLE_ID;
import static ru.menshevva.demoapp.service.roles.RoleSearchFilter.FILTER_ROLE_NAME;

@Service
@Slf4j
public class RolePrivilegeSearchServiceImpl implements RolePrivilegeSearchService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Stream<RolePrivilegeListData> fetch(Query<RolePrivilegeListData, RolePrivilegeSearchFilter> query) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(RolePrivilegeEntity.class);
        // Связка с таблицей ролей
        var roleJoin = root.join(RolePrivilegeEntity_.roleEntity);
        // Связка с таблицей привилегий
        var privilegeJoin = root.join(RolePrivilegeEntity_.privilegeEntity);
        // Выборка полей
        cq.multiselect(root.get(RolePrivilegeEntity_.roleId).alias(RoleEntity_.ROLE_ID),
                roleJoin.get(RoleEntity_.roleName).alias(RoleEntity_.ROLE_NAME),
                roleJoin.get(RoleEntity_.roleDescription).alias(RoleEntity_.ROLE_DESCRIPTION),
                root.get(RolePrivilegeEntity_.privilegeId).alias(PrivilegeEntity_.PRIVILEGE_ID),
                privilegeJoin.get(PrivilegeEntity_.privilegeName).alias(PrivilegeEntity_.PRIVILEGE_NAME),
                privilegeJoin.get(PrivilegeEntity_.privilegeDescription).alias(PrivilegeEntity_.PRIVILEGE_DESCRIPTION)
        );
        buildWhere(cb, cq, root, roleJoin, privilegeJoin, query);
        buildOrder(cb, cq, root, roleJoin, privilegeJoin, query);
        var tq = em.createQuery(cq);
        setParams(tq, query);
        return tq
                .setFirstResult(query.getOffset())
                .setMaxResults(query.getLimit())
                .getResultList()
                .stream()
                .map(t -> RolePrivilegeListData.builder()
                        .roleId(t.get(RoleEntity_.ROLE_ID, RoleEntity_.roleId.getJavaType()))
                        .roleName(t.get(RoleEntity_.ROLE_NAME, RoleEntity_.roleName.getJavaType()))
                        .roleDescription(t.get(RoleEntity_.ROLE_DESCRIPTION, RoleEntity_.roleDescription.getJavaType()))
                        .privilegeId(t.get(PrivilegeEntity_.PRIVILEGE_ID, PrivilegeEntity_.privilegeId.getJavaType()))
                        .privilegeName(t.get(PrivilegeEntity_.PRIVILEGE_NAME, PrivilegeEntity_.privilegeName.getJavaType()))
                        .privilegeDescription(t.get(PrivilegeEntity_.PRIVILEGE_DESCRIPTION, PrivilegeEntity_.privilegeDescription.getJavaType()))
                        .build());


    }

    private void setParams(TypedQuery<Tuple> tq, Query<RolePrivilegeListData, RolePrivilegeSearchFilter> query) {
        var params = tq.getParameters();
        if (params.isEmpty()) {
            return;
        }
        var filter = query.getFilter().orElse(RolePrivilegeSearchFilter.builder().build());
        params.stream()
                .filter(v -> v.getName() != null && !v.getName().isEmpty())
                .forEach(v -> {
                    switch (v.getName()) {
                        case FILTER_ROLE_ID -> tq.setParameter(v.getName(), filter.getRoleId());
                        case FILTER_ROLE_NAME -> tq.setParameter(v.getName(), filter.getRoleName());
                        case FILTER_ROLE_DESCRIPTION -> tq.setParameter(v.getName(), filter.getRoleDescription());
                        case FILTER_PRIVILEGE_ID -> tq.setParameter(v.getName(), filter.getPrivilegeId());
                        case FILTER_PRIVILEGE_NAME -> tq.setParameter(v.getName(), filter.getPrivilegeName());
                        case FILTER_PRIVILEGE_DESCRIPTION -> tq.setParameter(v.getName(), filter.getPrivilegeDescription());
                    }
                });

    }

    private void buildOrder(CriteriaBuilder cb, CriteriaQuery<Tuple> cq, Root<RolePrivilegeEntity> root,
                            Join<RolePrivilegeEntity, RoleEntity> roleJoin, Join<RolePrivilegeEntity,
                    PrivilegeEntity> privilegeJoin, Query<RolePrivilegeListData, RolePrivilegeSearchFilter> query) {
        List<Order> orders = new ArrayList<>();
        query.getSortOrders()
                .forEach(
                        v -> {
                            var expression = switch (v.getSorted()) {
                                case FILTER_ROLE_ID -> root.get(RolePrivilegeEntity_.roleId);
                                case FILTER_ROLE_NAME -> roleJoin.get(RoleEntity_.roleName);
                                case FILTER_ROLE_DESCRIPTION -> roleJoin.get(RoleEntity_.roleDescription);
                                case FILTER_PRIVILEGE_ID -> root.get(RolePrivilegeEntity_.privilegeId);
                                case FILTER_PRIVILEGE_NAME -> privilegeJoin.get(PrivilegeEntity_.privilegeName);
                                case FILTER_PRIVILEGE_DESCRIPTION -> privilegeJoin.get(PrivilegeEntity_.privilegeDescription);
                                case null, default -> null;
                            };
                            if (expression != null) {
                                orders.add(createOrder(cb, v.getDirection(), expression));
                            }
                        }
                );
        if (!orders.isEmpty()) {
            cq.orderBy(orders);
        }

    }

    private void buildWhere(CriteriaBuilder cb, CriteriaQuery<Tuple> cq, Root<RolePrivilegeEntity> root, Join<RolePrivilegeEntity, RoleEntity> roleJoin, Join<RolePrivilegeEntity, PrivilegeEntity> privilegeJoin, Query<RolePrivilegeListData, RolePrivilegeSearchFilter> query) {
        var predicates = new ArrayList<Predicate>();
        query.getFilter().ifPresent(filter -> {
            if (filter.getRoleId() != null) {
                predicates.add(cb.equal(root.get(RolePrivilegeEntity_.roleId),
                        cb.parameter(RoleEntity_.roleId.getJavaType(), FILTER_ROLE_ID)));
            }
            if (filter.getRoleName() != null && !filter.getRoleName().isEmpty()) {
                predicates.add(cb.like(roleJoin.get(RoleEntity_.roleName),
                        cb.parameter(RoleEntity_.roleName.getJavaType(), FILTER_ROLE_NAME)));
            }
            if (filter.getRoleDescription() != null && !filter.getRoleDescription().isEmpty()) {
                predicates.add(cb.like(roleJoin.get(RoleEntity_.roleDescription),
                        cb.parameter(RoleEntity_.roleDescription.getJavaType(), FILTER_ROLE_DESCRIPTION)));
            }
            if (filter.getPrivilegeId() != null) {
                predicates.add(cb.equal(root.get(RolePrivilegeEntity_.privilegeId),
                        cb.parameter(RolePrivilegeEntity_.privilegeId.getJavaType(), FILTER_PRIVILEGE_ID)));
            }
            if (filter.getPrivilegeName() != null && !filter.getPrivilegeName().isEmpty()) {
                predicates.add(cb.like(privilegeJoin.get(PrivilegeEntity_.privilegeName),
                        cb.parameter(PrivilegeEntity_.privilegeName.getJavaType(), FILTER_PRIVILEGE_NAME)));
            }
            if (filter.getPrivilegeDescription() != null && !filter.getPrivilegeDescription().isEmpty()) {
                predicates.add(cb.like(privilegeJoin.get(PrivilegeEntity_.privilegeDescription),
                            cb.parameter(PrivilegeEntity_.privilegeDescription.getJavaType(), FILTER_PRIVILEGE_DESCRIPTION)));
            }

        });
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

    }

    @Override
    public int getCount(Query<RolePrivilegeListData, RolePrivilegeSearchFilter> query) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(RolePrivilegeEntity.class);
        // Связка с таблицей ролей
        var roleJoin = root.join(RolePrivilegeEntity_.roleEntity);
        // Связка с таблицей привилегий
        var privilegeJoin = root.join(RolePrivilegeEntity_.privilegeEntity);
        var col = cb.count(root);
        cq.multiselect(col);
        buildWhere(cb, cq, root, roleJoin, privilegeJoin, query);
        TypedQuery<Tuple> tq = em.createQuery(cq);
        setParams(tq, query);
        var t = tq.getSingleResult();
        return t.get(0, col.getJavaType()).intValue();

    }
}
