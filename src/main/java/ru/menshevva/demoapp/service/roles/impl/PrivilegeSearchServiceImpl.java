package ru.menshevva.demoapp.service.roles.impl;

import com.vaadin.flow.data.provider.Query;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.menshevva.demoapp.dto.privilege.PrivilegeListData;
import ru.menshevva.demoapp.entities.main.auth.PrivilegeEntity;
import ru.menshevva.demoapp.entities.main.auth.PrivilegeEntity_;
import ru.menshevva.demoapp.service.roles.PrivilegeSearchFilter;
import ru.menshevva.demoapp.service.roles.PrivilegeSearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static ru.menshevva.demoapp.service.roles.PrivilegeSearchFilter.*;

@Service
@Slf4j
public class PrivilegeSearchServiceImpl implements PrivilegeSearchService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Stream<PrivilegeListData> fetch(Query<PrivilegeListData, PrivilegeSearchFilter> query) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(PrivilegeEntity.class);
        // Выборка полей
        cq.multiselect(root.get(PrivilegeEntity_.privilegeId).alias(PrivilegeEntity_.PRIVILEGE_ID),
                root.get(PrivilegeEntity_.privilegeName).alias(PrivilegeEntity_.PRIVILEGE_NAME),
                root.get(PrivilegeEntity_.privilegeDescription).alias(PrivilegeEntity_.PRIVILEGE_DESCRIPTION)
        );
        buildWhere(cb, cq, root, query);
        buildOrder(cb, cq, root, query);
        var tq = em.createQuery(cq);
        setParams(tq, query);
        return tq
                .setFirstResult(query.getOffset())
                .setMaxResults(query.getLimit())
                .getResultList()
                .stream()
                .map(t -> PrivilegeListData.builder()
                        .privilegeId(t.get(PrivilegeEntity_.PRIVILEGE_ID, PrivilegeEntity_.privilegeId.getJavaType()))
                        .privilegeName(t.get(PrivilegeEntity_.PRIVILEGE_NAME, PrivilegeEntity_.privilegeName.getJavaType()))
                        .privilegeDescription(t.get(PrivilegeEntity_.PRIVILEGE_DESCRIPTION, PrivilegeEntity_.privilegeDescription.getJavaType()))
                        .build());


    }

    private void setParams(TypedQuery<Tuple> tq, Query<PrivilegeListData, PrivilegeSearchFilter> query) {
        var params = tq.getParameters();
        if (params.isEmpty()) {
            return;
        }
        var filter = query.getFilter().orElse(PrivilegeSearchFilter.builder().build());
        params.stream()
                .filter(v -> v.getName() != null && !v.getName().isEmpty())
                .forEach(v -> {
                    switch (v.getName()) {
                        case FILTER_PRIVILEGE_ID -> tq.setParameter(v.getName(), filter.getPrivilegeId());
                        case FILTER_PRIVILEGE_NAME -> tq.setParameter(v.getName(), filter.getPrivilegeName());
                        case FILTER_PRIVILEGE_DESCRIPTION ->
                                tq.setParameter(v.getName(), filter.getPrivilegeDescription());
                        case FILTER_PRIVILEGE_LABEL -> tq.setParameter(v.getName(), filter.getPrivilegeLabel());
                    }
                });

    }

    private void buildOrder(CriteriaBuilder cb, CriteriaQuery<Tuple> cq, Root<PrivilegeEntity> root,
                            Query<PrivilegeListData, PrivilegeSearchFilter> query) {
        if (query.getSortOrders() != null && !query.getSortOrders().isEmpty()) {
            List<Order> orders = new ArrayList<>();
            query.getSortOrders()
                    .forEach(
                            v -> {
                                var expression = switch (v.getSorted()) {
                                    case FILTER_PRIVILEGE_ID -> root.get(PrivilegeEntity_.privilegeId);
                                    case FILTER_PRIVILEGE_NAME -> root.get(PrivilegeEntity_.privilegeName);
                                    case FILTER_PRIVILEGE_DESCRIPTION ->
                                            root.get(PrivilegeEntity_.privilegeDescription);
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

    }

    private void buildWhere(CriteriaBuilder cb, CriteriaQuery<Tuple> cq, Root<PrivilegeEntity> root, Query<PrivilegeListData, PrivilegeSearchFilter> query) {
        var predicates = new ArrayList<Predicate>();
        query.getFilter().ifPresent(filter -> {
            if (filter.getPrivilegeId() != null) {
                predicates.add(cb.equal(root.get(PrivilegeEntity_.privilegeId),
                        cb.parameter(PrivilegeEntity_.privilegeId.getJavaType(), FILTER_PRIVILEGE_ID)));
            }
            if (filter.getPrivilegeName() != null && !filter.getPrivilegeName().isEmpty()) {
                predicates.add(cb.like(root.get(PrivilegeEntity_.privilegeName),
                        cb.parameter(PrivilegeEntity_.privilegeName.getJavaType(), FILTER_PRIVILEGE_NAME)));
            }
            if (filter.getPrivilegeDescription() != null && !filter.getPrivilegeDescription().isEmpty()) {
                predicates.add(cb.like(root.get(PrivilegeEntity_.privilegeDescription),
                        cb.parameter(PrivilegeEntity_.privilegeDescription.getJavaType(), FILTER_PRIVILEGE_DESCRIPTION)));
            }
            if (filter.getPrivilegeLabel() != null) {
                predicates.add(
                        cb.like(cb.concat(root.get(PrivilegeEntity_.privilegeName), cb.concat(cb.literal(" - "), root.get(PrivilegeEntity_.privilegeDescription))),
                                cb.concat(cb.literal("%"),
                                        cb.concat(cb.parameter(PrivilegeEntity_.privilegeDescription.getJavaType(), FILTER_PRIVILEGE_LABEL), cb.literal("%")
                                        )

                                )
                        )
                );
            }

        });
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

    }

    @Override
    public int getCount(Query<PrivilegeListData, PrivilegeSearchFilter> query) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(PrivilegeEntity.class);
        var col = cb.count(root);
        cq.multiselect(col);
        buildWhere(cb, cq, root, query);
        TypedQuery<Tuple> tq = em.createQuery(cq);
        setParams(tq, query);
        var t = tq.getSingleResult();
        return t.get(0, col.getJavaType()).intValue();

    }
}
