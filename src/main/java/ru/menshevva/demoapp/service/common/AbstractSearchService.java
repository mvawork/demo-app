package ru.menshevva.demoapp.service.common;

import com.vaadin.flow.data.provider.SortDirection;
import jakarta.persistence.criteria.*;

public abstract class AbstractSearchService {

    protected Order createOrder(CriteriaBuilder cb, SortDirection sortDirection, Expression<?> expression) {
        return switch (sortDirection) {
            case ASCENDING -> cb.asc(expression);
            case DESCENDING -> cb.desc(expression);
        };
    }

}
