package com.mshindelar.collection.model.collection.request;

import com.mshindelar.collection.db.filter.AbstractFilter;
import com.mshindelar.collection.db.filter.AndFilter;
import com.mshindelar.collection.db.filter.OrFilter;
import com.mshindelar.collection.db.filter.SimpleFilter;
import com.mshindelar.collection.exception.InvalidFilterException;
import com.mshindelar.collection.db.filter.card.CardFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardFilterChain {
    private FilterType filterType;
    private CardFilter filter;
    private List<CardFilterChain> chain;

    public AbstractFilter toFilter() {
        List<AbstractFilter> filters = null;
        if (chain != null && !this.filterType.equals(FilterType.SIMPLE)) {
            // We shouldn't be doing a compound filter with only one inner filter
            if (chain.size() < 2)
                throw new InvalidFilterException("Cannot have a complex filter chain with only one inner filter");

            filters = chain.stream().map(CardFilterChain::toFilter).toList();
        }

        if (filterType.equals(FilterType.SIMPLE) && filter == null)
            throw new InvalidFilterException("Unspecified filter");

        AbstractFilter filter = null;
        switch (filterType) {
            case AND -> filter = new AndFilter(filters);
            case OR -> filter = new OrFilter(filters);
            case SIMPLE -> filter = new SimpleFilter(this.filter);
            default -> throw new InvalidFilterException("Unrecognized filter type");
        }
        return filter;
    }
}
