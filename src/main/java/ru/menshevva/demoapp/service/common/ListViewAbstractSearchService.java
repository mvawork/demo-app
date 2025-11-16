package ru.menshevva.demoapp.service.common;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import jakarta.persistence.criteria.*;

import java.util.stream.Stream;

public interface ListViewAbstractSearchService<T, F> {

    int getCount(Query<T, F> query);

    Stream<T> fetch(Query<T, F> query);


    default Order createOrder(CriteriaBuilder cb, SortDirection sortDirection, Expression<?> expression) {
        return switch (sortDirection) {
            case ASCENDING -> cb.asc(expression);
            case DESCENDING -> cb.desc(expression);
        };
    }

}
