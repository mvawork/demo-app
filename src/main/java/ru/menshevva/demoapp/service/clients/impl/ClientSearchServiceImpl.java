package ru.menshevva.demoapp.service.clients.impl;


import com.vaadin.flow.data.provider.Query;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Service;
import ru.menshevva.demoapp.dto.clients.ClientListData;
import ru.menshevva.demoapp.entities.second.ClientEntity;
import ru.menshevva.demoapp.entities.second.ClientEntity_;
import ru.menshevva.demoapp.service.clients.ClientSearchFilter;
import ru.menshevva.demoapp.service.clients.ClientSearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static ru.menshevva.demoapp.service.clients.ClientSearchFilter.FILTER_CLIENT_ID;
import static ru.menshevva.demoapp.service.clients.ClientSearchFilter.FILTER_CLIENT_NAME;

@Service
public class ClientSearchServiceImpl implements ClientSearchService {

    @PersistenceContext(unitName = "second")
    private EntityManager em;


    @Override
    public Stream<ClientListData> fetch(Query<ClientListData, ClientSearchFilter> query) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(ClientEntity.class);
        // Выборка полей
        cq.multiselect(root.get(ClientEntity_.clientId).alias(ClientEntity_.CLIENT_ID),
                root.get(ClientEntity_.clientName).alias(ClientEntity_.CLIENT_NAME)
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
                .map(t -> ClientListData.builder()
                        .clientId(t.get(ClientEntity_.CLIENT_ID, ClientEntity_.clientId.getJavaType()))
                        .clientName(t.get(ClientEntity_.CLIENT_NAME, ClientEntity_.clientName.getJavaType()))
                        .build());

    }

    @Override
    public int getCount(Query<ClientListData, ClientSearchFilter> query) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(ClientEntity.class);
        var col = cb.count(root);
        cq.multiselect(col);
        buildWhere(cb, cq, root, query);
        TypedQuery<Tuple> tq = em.createQuery(cq);
        setParams(tq, query);
        var t = tq.getSingleResult();
        return t.get(0, col.getJavaType()).intValue();

    }

    private void buildWhere(CriteriaBuilder cb, CriteriaQuery<Tuple> cq, Root<ClientEntity> root, Query<ClientListData, ClientSearchFilter> query) {
        var predicates = new ArrayList<Predicate>();
        query.getFilter().ifPresent(filter -> {
            if (filter.getClientId() != null) {
                predicates.add(cb.equal(root.get(ClientEntity_.clientId),
                        cb.parameter(ClientEntity_.clientId.getJavaType(), FILTER_CLIENT_ID)));
            }
            if (filter.getClientName() != null && !filter.getClientName().isEmpty()) {
                predicates.add(cb.like(root.get(ClientEntity_.clientName),
                        cb.parameter(ClientEntity_.clientName.getJavaType(), FILTER_CLIENT_NAME)));
            }

        });
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

    }

    private void setParams(TypedQuery<Tuple> tq, Query<ClientListData, ClientSearchFilter> query) {
        var params = tq.getParameters();
        if (params.isEmpty()) {
            return;
        }
        var filter = query.getFilter().orElse(ClientSearchFilter.builder().build());
        params.stream()
                .filter(v -> v.getName() != null && !v.getName().isEmpty())
                .forEach(v -> {
                    switch (v.getName()) {
                        case FILTER_CLIENT_ID -> tq.setParameter(v.getName(), filter.getClientId());
                        case FILTER_CLIENT_NAME -> tq.setParameter(v.getName(), filter.getClientName());
                    }
                });

    }

    private void buildOrder(CriteriaBuilder cb, CriteriaQuery<Tuple> cq, Root<ClientEntity> root,
                            Query<ClientListData, ClientSearchFilter> query) {
        if (query.getSortOrders() != null && !query.getSortOrders().isEmpty()) {
            List<Order> orders = new ArrayList<>();
            query.getSortOrders()
                    .forEach(
                            v -> {
                                var expression = switch (v.getSorted()) {
                                    case FILTER_CLIENT_ID -> root.get(ClientEntity_.clientId);
                                    case FILTER_CLIENT_NAME -> root.get(ClientEntity_.clientName);
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


}
