package com.mshindelar.collection.db.filter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericSpecification<T> implements Specification<T> {

    private AbstractFilter filter;
    private JoinDataSupplier<T> joinDataSupplier;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        if (filter != null) {
            if (joinDataSupplier != null) {
                return filter.toPredicate(root, query, criteriaBuilder, joinDataSupplier.getJoinData(root, query));
            } else {
                return filter.toPredicate(root, query, criteriaBuilder, null);
            }
        }
        return criteriaBuilder.conjunction();
    }
}
