package ru.menshevva.demoapp.service.users.impl;

import com.vaadin.flow.data.provider.Query;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.menshevva.demoapp.dto.UserListData;
import ru.menshevva.demoapp.security.entities.UserEntity;
import ru.menshevva.demoapp.security.entities.UserEntity_;
import ru.menshevva.demoapp.service.users.UserSearchFilter;
import ru.menshevva.demoapp.service.users.UserSearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static ru.menshevva.demoapp.service.users.UserSearchFilter.*;


@Service
@Slf4j
public class UserSearchServiceImpl implements UserSearchService {


    @PersistenceContext
    private EntityManager em;

    protected void setParams(TypedQuery<Tuple> tq, Query<UserListData, UserSearchFilter> query) {
        var params = tq.getParameters();
        if (params.isEmpty()) {
            return;
        }
        var filter = query.getFilter().orElse(UserSearchFilter.builder().build());
        params.stream()
                .filter(v -> v.getName() != null && !v.getName().isEmpty())
                .forEach(v -> {
                    switch (v.getName()) {
                        case FILTER_USER_ID -> tq.setParameter(v.getName(), filter.getUserId());
                        case FILTER_USER_LOGIN -> tq.setParameter(v.getName(), filter.getUserLogin());
                        case FILTER_USER_NAME -> tq.setParameter(v.getName(), filter.getUserName());
                    }
                });
    }

    @Override
    public int getCount(Query<UserListData, UserSearchFilter> query) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(UserEntity.class);
        var col = cb.count(root);
        cq.multiselect(col);
        buildWhere(cb, cq, root, query);
        TypedQuery<Tuple> tq = em.createQuery(cq);
        setParams(tq, query);
        var t = tq.getSingleResult();
        return t.get(0, col.getJavaType()).intValue();
    }

    @Override
    public Stream<UserListData> fetch(Query<UserListData, UserSearchFilter> query) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createTupleQuery();
        var root = cq.from(UserEntity.class);
        cq.multiselect(root.get(UserEntity_.userId).alias(UserEntity_.USER_ID),
                root.get(UserEntity_.userLogin).alias(UserEntity_.USER_LOGIN),
                root.get(UserEntity_.userName).alias(UserEntity_.USER_NAME));
        buildWhere(cb, cq, root, query);
        buildOrder(cb, cq, root, query);
        var tq = em.createQuery(cq);
        setParams(tq, query);
        return tq
                .setFirstResult(query.getOffset())
                .setMaxResults(query.getLimit())
                .getResultList()
                .stream()
                .map(t -> UserListData.builder()
                        .userId(t.get(UserEntity_.USER_ID, UserEntity_.userId.getJavaType()))
                        .userLogin(t.get(UserEntity_.USER_LOGIN, UserEntity_.userLogin.getJavaType()))
                        .userName(t.get(UserEntity_.USER_NAME, UserEntity_.userName.getJavaType()))
                        .build());
    }


    private void buildOrder(CriteriaBuilder cb, CriteriaQuery<Tuple> criteriaQuery, Root<UserEntity> root, Query<UserListData, UserSearchFilter> query) {
        if (query.getSortOrders() == null) {
            return;
        }
        List<Order> orders = new ArrayList<>();
        query.getSortOrders()
                .forEach(
                        v -> {
                            var expression = switch (v.getSorted()) {
                                case FILTER_USER_ID -> root.get(UserEntity_.userId);
                                case FILTER_USER_LOGIN -> root.get(UserEntity_.userLogin);
                                case FILTER_USER_NAME -> root.get(UserEntity_.userName);
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


    protected void buildWhere(CriteriaBuilder cb, CriteriaQuery<Tuple> criteriaQuery, Root<UserEntity> root,
                              Query<UserListData, UserSearchFilter> query) {
        var predicates = new ArrayList<Predicate>();
        query.getFilter().ifPresent(filter -> {
            if (filter.getUserId() != null) {
                predicates.add(cb.equal(root.get(UserEntity_.userId),
                        cb.parameter(UserEntity_.userId.getJavaType(), FILTER_USER_ID)));
            }
            if (filter.getUserLogin() != null && !filter.getUserLogin().isEmpty()) {
                predicates.add(cb.like(root.get(UserEntity_.userLogin),
                        cb.parameter(UserEntity_.userLogin.getJavaType(), FILTER_USER_LOGIN)));
            }
            if (filter.getUserName() != null && !filter.getUserName().isEmpty()) {
                predicates.add(cb.like(root.get(UserEntity_.userName),
                        cb.parameter(UserEntity_.userName.getJavaType(), FILTER_USER_NAME)));
            }
        });
        if (!predicates.isEmpty()) {
            criteriaQuery.where(predicates.toArray(new Predicate[0]));
        }
    }

}
