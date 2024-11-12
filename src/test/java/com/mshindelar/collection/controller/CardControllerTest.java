package com.mshindelar.collection.controller;

import com.mshindelar.collection.dto.card.CardDto;
import com.mshindelar.collection.dto.card.YGOCardDto;
import com.mshindelar.collection.model.collection.request.CardFilterChain;
import com.mshindelar.collection.model.collection.request.FilterType;
import com.mshindelar.collection.db.filter.FilterOperator;
import com.mshindelar.collection.db.filter.card.CardFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class CardControllerTest extends BaseContollerTest {
    @Autowired
    private CardController cardController;

    @Test
    public void testFilterCards() throws Exception {
        CardFilter equipRaceFilter = new CardFilter(CardFilter.CardField.RACE, FilterOperator.EQUAL_TO, "Equip");

        CardFilter contRaceFilter = new CardFilter(CardFilter.CardField.RACE, FilterOperator.EQUAL_TO, "Continuous");
        CardFilter notSpellType = new CardFilter(CardFilter.CardField.TYPE, FilterOperator.NOT_EQUAL, "Spell Card");

        CardFilterChain link1 = new CardFilterChain(FilterType.AND, null, List.of(
                new CardFilterChain(FilterType.SIMPLE, contRaceFilter, null),
                new CardFilterChain(FilterType.SIMPLE, notSpellType, null)));

        CardFilterChain link2 = new CardFilterChain(FilterType.SIMPLE, equipRaceFilter, null);

        CardFilterChain chain = new CardFilterChain(FilterType.OR, null, List.of(link1, link2));

        List<CardDto> cards = this.cardController.getFilteredCards(chain);
        Assertions.assertNotNull(cards);
        Assertions.assertTrue(cards.size() > 0);

        cards.forEach(c -> {
            YGOCardDto yc = (YGOCardDto) c;

            Assertions.assertTrue(yc.getRace().equals("Continuous") || yc.getRace().equals("Equip"));

            if (yc.getRace().equals("Continuous")) {
                Assertions.assertEquals("Trap Card", yc.getType());
            }
        });
    }

    @Test
    public void testFilterCards_InvalidFilter() throws Exception {
        CardFilterChain chain = new CardFilterChain(FilterType.SIMPLE, null, null);

        Throwable t = null;

        try { this.cardController.getFilteredCards(chain); }
        catch (Exception e) { t = e; }

        Assertions.assertNotNull(t);
        Assertions.assertTrue(t instanceof ResponseStatusException);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ((ResponseStatusException) t).getStatusCode());
    }
}
