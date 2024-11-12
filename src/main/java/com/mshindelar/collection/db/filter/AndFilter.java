package com.mshindelar.collection.db.filter;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AndFilter extends AbstractFilter{

    private List<AbstractFilter> filters;

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder,
                                 Map<String, Join<Object, Object>> attributeToJoin ) {
        return criteriaBuilder.and(filters.stream().map(filter -> filter
                .toPredicate(root,query,criteriaBuilder,attributeToJoin)).toList().toArray(Predicate[]::new));
    }
}