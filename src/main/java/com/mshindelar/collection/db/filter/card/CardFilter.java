package com.mshindelar.collection.db.filter.card;

import com.mshindelar.collection.db.filter.AbstractFilter;
import com.mshindelar.collection.db.filter.SimpleFilter;
import com.mshindelar.collection.model.card.Card;
import com.mshindelar.collection.db.filter.Filter;
import com.mshindelar.collection.db.filter.FilterOperator;

public class CardFilter extends Filter {

    public CardFilter() {
        this.setEntity(Card.class);
    }
    public CardFilter(CardField field, FilterOperator operator, Object value) {
        super(field.getName(), operator, value, null, null, Card.class);
    }

    public AbstractFilter asSimpleFilter() { return new SimpleFilter(this); }

    public enum CardField {
        ARCHETYPE("archetype"),
        ATTACK("attack"),
        ATTRIBUTE("attribute"),
        DEFENSE("defense"),
        LEVEL("level"),
        NAME("name"),
        RACE("race"),
        TYPE("type");

        private String name;

        CardField(String name) { this.name = name; }

        public String getName() { return this.name; }
    }
}
