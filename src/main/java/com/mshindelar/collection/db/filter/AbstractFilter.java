package com.mshindelar.collection.db.filter;

import jakarta.persistence.criteria.*;
import java.util.Map;

public abstract class AbstractFilter {

    public abstract Predicate toPredicate(Root root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder,
                                          Map<String, Join<Object, Object>> attributeToJoin);

    public Predicate getPredicate(Filter filter, CriteriaBuilder criteriaBuilder, Path expression) {
        if (filter.getOperator().equals(FilterOperator.BETWEEN) && filter.getSecondaryValue() == null) {
            throw new IllegalArgumentException("Must set secondary filter value if using the between operator");
        }

        Predicate predicate = null;
        switch (filter.getOperator()) {
            case EQUAL_TO -> predicate = criteriaBuilder.equal(expression, filter.getValue());
            case LIKE -> predicate = criteriaBuilder.like(expression, "%" + filter.getValue() + "%");
            case IN -> predicate = criteriaBuilder.in(expression).value(filter.getValue());
            case GT -> predicate = criteriaBuilder.greaterThan(expression, (Comparable) filter.getValue());
            case LT -> predicate = criteriaBuilder.lessThan(expression, (Comparable) filter.getValue());
            case GTE -> predicate = criteriaBuilder.greaterThan(expression, (Comparable) filter.getValue());
            case LTE -> predicate = criteriaBuilder.lessThan(expression, (Comparable) filter.getValue());
            case NOT_EQUAL -> predicate = criteriaBuilder.notEqual(expression, filter.getValue());
            case BETWEEN -> predicate = criteriaBuilder.between(expression, (Comparable) filter.getValue(),
                    (Comparable) filter.getSecondaryValue());
            default -> throw new IllegalArgumentException(String.format("%s is not a valid operator",
                    filter.getOperator()));
        }

        return predicate;
    }

    public Predicate getPredicateFromFilter(Filter filter, Root root, CriteriaBuilder criteriaBuilder, Map<String,
            Join<Object, Object>> attributeToJoin) {
        if (attributeToJoin != null && attributeToJoin.get(filter.getEntity().getSimpleName()) != null) {
            return getPredicate(filter, criteriaBuilder, attributeToJoin.get(filter.getEntity().getSimpleName())
                    .get(filter.getField()));
        } else {
            return getPredicate(filter, criteriaBuilder, root.get(filter.getField()));
        }
    }
}
