package com.mshindelar.collection.db.filter;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SimpleFilter extends AbstractFilter {

    private Filter filter;

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, Map<String,
            Join<Object, Object>> attributeToJoin) {
        return getPredicateFromFilter(filter,root,criteriaBuilder,attributeToJoin);
    }
}
