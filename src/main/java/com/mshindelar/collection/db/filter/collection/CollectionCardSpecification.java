package com.mshindelar.collection.db.filter.collection;

import com.mshindelar.collection.db.filter.AbstractFilter;
import com.mshindelar.collection.db.filter.GenericSpecification;
import com.mshindelar.collection.db.filter.JoinDataSupplier;
import com.mshindelar.collection.model.collection.CollectionCard;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import java.util.LinkedHashMap;
import java.util.Map;

public class CollectionCardSpecification extends GenericSpecification<CollectionCard> {

    public CollectionCardSpecification(AbstractFilter filter) {
        super(filter, new CollectionCardJoinDataSupplier());
    }

    private static class CollectionCardJoinDataSupplier implements JoinDataSupplier<CollectionCard> {
        @Override
        public Map<String, Join<Object, Object>> getJoinData(Root<CollectionCard> root, CriteriaQuery<?> query) {
            Map<String, Join<Object, Object>> attributeToJoinMap = new LinkedHashMap<>();
            Join<Object, Object> joinCard = root.join("card", JoinType.INNER);
            attributeToJoinMap.put("Card", joinCard);
            return attributeToJoinMap;
        }
    }
}
