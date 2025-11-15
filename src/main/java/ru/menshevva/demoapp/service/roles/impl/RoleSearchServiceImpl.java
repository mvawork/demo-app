package ru.menshevva.demoapp.service.roles.impl;

import com.vaadin.flow.data.provider.Query;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.menshevva.demoapp.dto.role.RoleListData;
import ru.menshevva.demoapp.security.entities.RoleEntity;
import ru.menshevva.demoapp.security.entities.RoleEntity_;
import ru.menshevva.demoapp.service.common.AbstractSearchService;
import ru.menshevva.demoapp.service.roles.RoleSearchFilter;
import ru.menshevva.demoapp.service.roles.RoleSearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static ru.menshevva.demoapp.service.roles.RoleSearchFilter.*;

@Service
@Slf4j
public class RoleSearchServiceImpl extends AbstractSearchService implements RoleSearchService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Stream<RoleListData> fetch(Query<RoleListData, RoleSearchFilter> query) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(RoleEntity.class);
        cq.multiselect(root.get(RoleEntity_.roleId).alias(RoleEntity_.ROLE_ID),
                root.get(RoleEntity_.roleName).alias(RoleEntity_.ROLE_NAME),
                root.get(RoleEntity_.roleDescription).alias(RoleEntity_.ROLE_DESCRIPTION));
        buildWhere(cb, cq, root, query);
        buildOrder(cb, cq, root, query);
        var tq = em.createQuery(cq);
        setParams(tq, query);
        return tq
                .setFirstResult(query.getOffset())
                .setMaxResults(query.getLimit())
                .getResultList()
                .stream()
                .map(t -> RoleListData.builder()
                        .roleId(t.get(RoleEntity_.ROLE_ID, RoleEntity_.roleId.getJavaType()))
                        .roleName(t.get(RoleEntity_.ROLE_NAME, RoleEntity_.roleName.getJavaType()))
                        .roleDescription(t.get(RoleEntity_.ROLE_DESCRIPTION, RoleEntity_.roleDescription.getJavaType()))
                        .build());

    }

    @Override
    public int getCount(Query<RoleListData, RoleSearchFilter> query) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(RoleEntity.class);
        var col = cb.count(root);
        cq.multiselect(col);
        buildWhere(cb, cq, root, query);
        TypedQuery<Tuple> tq = em.createQuery(cq);
        setParams(tq, query);
        var t = tq.getSingleResult();
        return t.get(0, col.getJavaType()).intValue();
    }

    private void buildOrder(CriteriaBuilder cb, CriteriaQuery<Tuple> criteriaQuery, Root<RoleEntity> root, Query<RoleListData, RoleSearchFilter> query) {
        if (query.getSortOrders() == null) {
            return;
        }
        List<Order> orders = new ArrayList<>();
        query.getSortOrders()
                .forEach(
                        v -> {
                            var expression = switch (v.getSorted()) {
                                case FILTER_ROLE_ID -> root.get(RoleEntity_.roleId);
                                case FILTER_ROLE_NAME -> root.get(RoleEntity_.roleName);
                                case FILTER_ROLE_DESCRIPTION -> root.get(RoleEntity_.roleDescription);
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


    protected void setParams(TypedQuery<Tuple> tq, Query<RoleListData, RoleSearchFilter> query) {
        var params = tq.getParameters();
        if (params.isEmpty()) {
            return;
        }
        var filter = query.getFilter().orElse(RoleSearchFilter.builder().build());
        params.stream()
                .filter(v -> v.getName() != null && !v.getName().isEmpty())
                .forEach(v -> {
                    switch (v.getName()) {
                        case FILTER_ROLE_ID -> tq.setParameter(v.getName(), filter.getRoleId());
                        case FILTER_ROLE_NAME -> tq.setParameter(v.getName(), filter.getRoleName());
                        case FILTER_ROLE_DESCRIPTION -> tq.setParameter(v.getName(), filter.getRoleDescription());
                        case FILTER_ROLE_LABEL -> tq.setParameter(v.getName(), filter.getRoleLabel());
                    }
                });
    }


    protected void buildWhere(CriteriaBuilder cb, CriteriaQuery<Tuple> criteriaQuery, Root<RoleEntity> root, Query<RoleListData, RoleSearchFilter> query) {
        var predicates = new ArrayList<Predicate>();
        query.getFilter().ifPresent(filter -> {
            if (filter.getRoleId() != null) {
                predicates.add(cb.equal(root.get(RoleEntity_.roleId),
                        cb.parameter(RoleEntity_.roleId.getJavaType(), FILTER_ROLE_ID)));
            }
            if (filter.getRoleName() != null && !filter.getRoleName().isEmpty()) {
                predicates.add(cb.like(root.get(RoleEntity_.roleName),
                        cb.parameter(RoleEntity_.roleName.getJavaType(), FILTER_ROLE_NAME)));
            }
            if (filter.getRoleDescription() != null && !filter.getRoleDescription().isEmpty()) {
                predicates.add(cb.like(root.get(RoleEntity_.roleDescription),
                        cb.parameter(RoleEntity_.roleDescription.getJavaType(), FILTER_ROLE_DESCRIPTION)));
            }

            if (filter.getRoleLabel() != null && !filter.getRoleLabel().isEmpty()) {
                predicates.add(
                        cb.like(cb.concat(root.get(RoleEntity_.roleName), cb.concat(cb.literal(" - "), root.get(RoleEntity_.roleDescription))),
                                cb.concat(cb.literal("%"),
                                        cb.concat(cb.parameter(RoleEntity_.roleDescription.getJavaType(), FILTER_ROLE_LABEL), cb.literal("%")
                                        )

                                )
                        )
                );
            }

        });
        if (!predicates.isEmpty()) {
            criteriaQuery.where(predicates.toArray(new Predicate[0]));
        }
    }
}
