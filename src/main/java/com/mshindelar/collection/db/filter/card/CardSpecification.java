package com.mshindelar.collection.db.filter.card;

import com.mshindelar.collection.db.filter.AbstractFilter;
import com.mshindelar.collection.model.card.Card;
import com.mshindelar.collection.db.filter.GenericSpecification;
import com.mshindelar.collection.db.filter.JoinDataSupplier;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import java.util.LinkedHashMap;
import java.util.Map;

public class CardSpecification extends GenericSpecification<Card> {
    public CardSpecification(AbstractFilter filter) {
        super(filter, new JoinDataSupplier<Card>() {
            @Override
            public Map<String, Join<Object, Object>> getJoinData(Root<Card> root, CriteriaQuery<?> query) {
                return new LinkedHashMap<>();
            }
        });
    }
}
